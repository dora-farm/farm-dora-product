package com.farmdora.farmdoraproduct.controller;

import com.farmdora.farmdoraproduct.common.response.HttpResponse;
import com.farmdora.farmdoraproduct.dto.*;
import com.farmdora.farmdoraproduct.jwt.JwtUtil;
import com.farmdora.farmdoraproduct.service.SaleService;
import com.farmdora.farmdoraproduct.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/my/seller/item")
public class SaleController {

    private final SaleService saleService;
    private final StorageService storageService;
    private final JwtUtil jwtUtil;

    public SaleController(SaleService saleService, StorageService storageService, JwtUtil jwtUtil) {
        this.saleService = saleService;
        this.storageService = storageService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("register")
    public HttpResponse addProduct(
            @RequestPart("productData") String productDataStr,
            @RequestPart("files") List<MultipartFile> files,
            HttpServletRequest httpServletRequest) throws IOException {

        System.out.println(jwtUtil.extractTokenFromCookie(httpServletRequest));

        // JSON 문자열을 DTO 객체로 직접 변환
        SaleRequestDto requestDto =
                new ObjectMapper().readValue(productDataStr, SaleRequestDto.class);

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
                .status(200)
                .message("저장 성공")
                .build();
    }

    @DeleteMapping("delete")
    public HttpResponse deleteProduct(@RequestBody SaleIdsDto request){

        List<Integer> saleIds = request.getSaleIds();
        // saleId 중 제일 처음 값을 기준으로 sale->seller_id->seller(user_id) 조회
        // 판매자 role일 경우 비교 휴 delete 실행
        Integer user_id= saleService.getUserIdBySaleId(saleIds.get(0));

//        if (user_id!= loginUser.getNo()) {
//            return HttpResponse.builder()
//                    .status()
//                    .data("삭제 권한이 없습니다.")
//                    .build();
//        }

        for (Integer saleId : saleIds) {
            saleService.deleteSale(saleId);
        }

        //삭제 성공 시
        return HttpResponse.builder()
                .status(200)
                .message("삭제 성공")
                .build();
    }

    @GetMapping("detail/{productId}")
    public HttpResponse detailProduct(@PathVariable Integer productId){
        SaleDetailDto saleDetailDto = saleService.getProductDetail(productId);

        return HttpResponse
                .builder()
                .status(200)
                .message("상세보기 성공")
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

        if(success==1) {
            //입력 성공 시
            return HttpResponse.builder()
                    .status(200)
                    .message("수정 성공")
                    .build();
        }
        else{
            return HttpResponse.builder()
                    .status(500)
                    .message("수정 실패")
                    .build();
        }
    }

    @PutMapping("updateStatus/{productId}")
    public HttpResponse updateStatus(@PathVariable Integer productId) {

        Integer result = saleService.updateStatus(productId);

        if (result == 1) {
            return HttpResponse.builder()
                    .status(200)
                    .message("상태 수정 성공")
                    .build();
        }
        else {
            return HttpResponse.builder()
                    .status(500)
                    .message("상태 수정 실패")
                    .build();
        }
    }

    // 메인 비디오 detail에서 사용하는 메소드, sellerid를 기준으로 판매글 정보를 조회하여 리턴한다.
    @GetMapping("video/{id}")
    public HttpResponse video(@PathVariable int id) {

        List<BroadcastSaleDto> broadcastSaleDtos = saleService.findSellerProductsBySellerId(id);

        return HttpResponse.builder()
                .status(200)
                .message("비디오 디테일 판매자 기준 판매글 조회 성공")
                .data(broadcastSaleDtos)
                .build();
    }

}
