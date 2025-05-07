package com.farmdora.farmdoraproduct.controller;

import com.farmdora.farmdoraproduct.common.response.HttpResponse;
import com.farmdora.farmdoraproduct.dto.*;
import com.farmdora.farmdoraproduct.jwt.JwtUtil;
import com.farmdora.farmdoraproduct.service.SaleService;
import com.farmdora.farmdoraproduct.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.farmdora.farmdoraproduct.common.response.ErrorMessage.DELETE_FAIL;
import static com.farmdora.farmdoraproduct.common.response.ErrorMessage.UPDATE_FAIL;
import static com.farmdora.farmdoraproduct.common.response.SuccessMessage.*;

@RestController
@RequestMapping("${api.prefix}/my/seller/item")
public class SaleController {

    private final SaleService saleService;
    private final StorageService storageService;

    public SaleController(SaleService saleService, StorageService storageService, JwtUtil jwtUtil) {
        this.saleService = saleService;
        this.storageService = storageService;
    }

    @PostMapping("register")
    public HttpResponse addProduct(
            Principal principal,
            @RequestPart("productData") String productDataStr,
            @RequestPart("files") List<MultipartFile> files,
            HttpServletRequest httpServletRequest) throws IOException {
        //JWT 토큰에서 user 아이디 추출, sellerId로 변환
        Integer userId = Integer.parseInt(principal.getName());
        Integer sellerId = saleService.getSellerId(userId);
        System.out.println(sellerId);
        // JSON 문자열을 DTO 객체로 직접 변환
        SaleRequestDto requestDto =
                new ObjectMapper().readValue(productDataStr, SaleRequestDto.class);
        //principal에서 추출한 sellerId setter로 주입.
        requestDto.setSellerId(sellerId);
        ArrayList<SaleFileDto> fileList = new ArrayList<>();
        boolean isFirstFile = false; // 첫 번째 파일 여부를 추적하는 플래그, 0이 메인

        for (MultipartFile part : files) {
            if (part.getSize() == 0) {
                continue;
            }
            String filename = UUID.randomUUID().toString();
            storageService.upload("product/" + filename, part.getInputStream());

            SaleFileDto attachedFile = new SaleFileDto();
            attachedFile.setSaveFile(filename);
            attachedFile.setOriginFile(part.getOriginalFilename());
            attachedFile.setMain(isFirstFile); // 첫 번째 파일만 false, 나머지는 true
            isFirstFile = true; // 플래그를 true로 변경하여 이후 파일은 모두 isMain=true(1)가 되도록 함
            fileList.add(attachedFile);
        }

        requestDto.setFiles(fileList);

        Integer saleId = saleService.createSale(requestDto);

        //입력 성공 시
        return HttpResponse.builder()
                .status(HttpStatus.OK.value())
                .message(REGISTER_PRODUCT_SUCCESS.getMessage())
                .build();
    }

    @DeleteMapping("delete")
    public HttpResponse deleteProduct(@RequestBody SaleIdsDto request){

        List<Integer> saleIds = request.getSaleIds();

        for (Integer saleId : saleIds) {
            saleService.deleteSale(saleId);
        }

        //삭제 성공 시
        return HttpResponse.builder()
                .status(HttpStatus.OK.value())
                .message(DELETE_FAIL.getMessage())
                .build();
    }

    @GetMapping("detail/{productId}")
    public HttpResponse detailProduct(@PathVariable Integer productId){
        SaleDetailDto saleDetailDto = saleService.getProductDetail(productId);

        return HttpResponse
                .builder()
                .status(HttpStatus.OK.value())
                .message(SEARCH_SALES_SUCCESS.getMessage())
                .data(saleDetailDto)
                .build();
    }

    @PutMapping("update")
    public HttpResponse updateProduct(
            @RequestPart("productData") String productDataStr,
            @RequestPart("files") List<MultipartFile> files) throws IOException {

//         JSON 문자열을 DTO 객체로 직접 변환
        SaleRequestDto requestDto =
                new ObjectMapper().readValue(productDataStr, SaleRequestDto.class);

        int success = saleService.updateSale(requestDto, files);

        if (success == 1) {
            return HttpResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message(REVISE_SUCCESS.getMessage())
                    .build();
        }
        else {
            return HttpResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(UPDATE_FAIL.getMessage())
                    .build();
        }
    }

    @PutMapping("updateStatus/{productId}")
    public HttpResponse updateStatus(@PathVariable Integer productId) {

        Integer result = saleService.updateStatus(productId);

        if (result == 1) {
            return HttpResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message(REVISE_SUCCESS.getMessage())
                    .build();
        }
        else {
            return HttpResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(UPDATE_FAIL.getMessage())
                    .build();
        }
    }

    // 메인 비디오 detail에서 사용하는 메소드, sellerid를 기준으로 판매글 정보를 조회하여 리턴한다.
    @GetMapping("video/{id}")
    public HttpResponse video(@PathVariable int id) {

        List<BroadcastSaleDto> broadcastSaleDtos = saleService.findSellerProductsBySellerId(id);

        return HttpResponse.builder()
                .status(HttpStatus.OK.value())
                .message(SEARCH_SALES_SUCCESS.getMessage())
                .data(broadcastSaleDtos)
                .build();
    }

}
