package com.farmdora.farmdoraproduct.controller;

import com.farmdora.farmdoraproduct.common.response.HttpResponse;
import com.farmdora.farmdoraproduct.dto.*;
import com.farmdora.farmdoraproduct.service.BroadcastService;
import com.farmdora.farmdoraproduct.service.SaleService;
import com.farmdora.farmdoraproduct.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static com.farmdora.farmdoraproduct.common.response.ErrorMessage.UPDATE_FAIL;
import static com.farmdora.farmdoraproduct.common.response.SuccessMessage.*;

@RestController
@RequestMapping("${api.prefix}/video")
public class BroadcastController {

    private final BroadcastService broadcastService;
    private final StorageService storageService;
    private final SaleService saleService;

    public BroadcastController(BroadcastService broadcastService, StorageService storageService, SaleService saleService) {
        this.broadcastService = broadcastService;
        this.storageService = storageService;
        this.saleService = saleService;
    }

    @PostMapping("register")
    public HttpResponse addVideo(
            Principal principal,
            @RequestPart("video") MultipartFile video,
            @RequestPart(value = "title", required = true) String title,
            @RequestPart(value = "desc", required = true) String desc) throws IOException {

        //확장자 추출
        String extention = FilenameUtils.getExtension(video.getOriginalFilename());
        String filename = UUID.randomUUID().toString();

        storageService.upload("video/" + filename +"."+extention, video.getInputStream());

        //JWT 토큰에서 user 아이디 추출, sellerId로 변환
        Integer userId = Integer.parseInt(principal.getName());
        Integer sellerId = saleService.getSellerId(userId);

        BroadcastDto broadcastDto = BroadcastDto.builder()
                .sellerId(sellerId)
                .title(title)
                .desc(desc)
                .content(filename+"."+extention)
                .build();

        Integer broadcastId = broadcastService.createVideo(broadcastDto);

        if(broadcastId != 0 ) {
            //입력 성공 시
            return HttpResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message(REGISTER_VIDEO_SUCCESS.getMessage())
                    .build();
        }
        else{
            //입력 실패 시
            return HttpResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message(UPDATE_FAIL.getMessage())
                    .build();
        }
    }

    //전체 조회 기능 (판매자)
    @GetMapping("/seller/list/{size}")
    public ResponseEntity<?> sellerList(
                        Principal principal,
            @RequestParam(defaultValue = "0") int page, @PathVariable int size) throws IOException {

        //JWT 토큰에서 user 아이디 추출, sellerId로 변환
        Integer userId = Integer.parseInt(principal.getName());
        Integer sellerId = saleService.getSellerId(userId);

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastDto> result = broadcastService.getBroadcastsBySellerId(sellerId,"","", pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,SEARCH_VIDEOS_SUCCESS.getMessage(),result));
    }

    //검색 조회 기능 (판매자)
    @PostMapping("/seller/search")
    public ResponseEntity<?> sellerSearchList(
                        Principal principal,
            @RequestBody BroadcastSearchDto broadcastSearchDto) throws IOException {
        //JWT 토큰에서 user 아이디 추출, sellerId로 변환
        Integer userId = Integer.parseInt(principal.getName());
        Integer sellerId = saleService.getSellerId(userId);

        String keyword = broadcastSearchDto.getKeyword();
        String sortBy = broadcastSearchDto.getSort();

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(broadcastSearchDto.getPage());
        pageRequestDto.setSize(broadcastSearchDto.getSize());

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastDto> result = broadcastService.getBroadcastsBySellerId(sellerId,keyword,sortBy,pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,SEARCH_VIDEOS_SUCCESS.getMessage(),result));
    }


    //전체 조회 기능 (관리자)
    @GetMapping("/admin/list/{size}")
    public ResponseEntity<?> adminList(@RequestParam(defaultValue = "0") int page, @PathVariable int size ) throws IOException {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastDto> result = broadcastService.getAllBroadcasts("","",pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,SEARCH_VIDEOS_SUCCESS.getMessage(),result));
    }

    //검색 조회 기능 (관리자)
    @PostMapping("/admin/search")
    public ResponseEntity<?> adminSearchList(@RequestBody BroadcastSearchDto broadcastSearchDto) throws IOException {

        String keyword = broadcastSearchDto.getKeyword();
        String sortBy = broadcastSearchDto.getSort();

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(broadcastSearchDto.getPage());
        pageRequestDto.setSize(broadcastSearchDto.getSize());

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastDto> result = broadcastService.getAllBroadcasts(keyword,sortBy,pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,SEARCH_VIDEOS_SUCCESS.getMessage(),result));
    }

    // 동영상 삭제 (관리자, 판매자)
    @DeleteMapping("delete")
    public HttpResponse deleteVideo(@RequestBody BroadcastIdsDto request){

        List<Integer> broadcastIds = request.getBroadcastIds();

        for (Integer  broadcastId : broadcastIds) {
            broadcastService.deleteBroadcast(broadcastId);
        }

        //삭제 성공 시
        return HttpResponse.builder()
                .status(HttpStatus.OK.value())
                .message(DELETE_SUCCESS.getMessage())
                .build();
    }
    
    //bilnd 상태 업데이트 (관리자, 판매자)
    @PutMapping("updateStatus/{videoId}")
    public HttpResponse updateStatus(@PathVariable Integer videoId) {

        Integer result = broadcastService.updateStatus(videoId);

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

    //전체 조회 기능 (메인/비디오)
    @GetMapping("/main/list")
    public ResponseEntity<?> mainList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) throws IOException {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size); //6개 씩 추출

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastMainDto> result = broadcastService.findAllByIsBlindFalse(pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,SEARCH_VIDEOS_SUCCESS.getMessage(),result));
    }

    //상세 조회 기능 (메인/비디오)
    @GetMapping("/main/detail/{id}")
    public ResponseEntity<?> mainDetail(@PathVariable int id) {

        BroadcastMainDto broadcastMainDto = broadcastService.getVideoDetail(id);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,SEARCH_VIDEOS_SUCCESS.getMessage(),broadcastMainDto));
    }

    //최신 10개의 동영상 조회 (메인/홈), 페이지 사용하지 않지만 기존 코드 재활용
    @GetMapping("/main/home")
    public ResponseEntity<?> mainHomeSlider(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size); //6개 씩 추출

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastMainDto> result = broadcastService.findAllByIsBlindFalse(pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,SEARCH_VIDEOS_SUCCESS.getMessage(),result));
    }

}
