package com.farmdora.farmdoraproduct.controller;

import com.farmdora.farmdoraproduct.common.response.HttpResponse;
import com.farmdora.farmdoraproduct.dto.*;
import com.farmdora.farmdoraproduct.service.BroadcastService;
import com.farmdora.farmdoraproduct.service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // 프론트와 테스트용 임시 추가
@RequestMapping("/video")
public class BroadcastController {

    private final BroadcastService broadcastService;
    private final StorageService storageService;

    public BroadcastController(BroadcastService broadcastService, StorageService storageService) {
        this.broadcastService = broadcastService;
        this.storageService = storageService;
    }

    @PostMapping("register")
    public HttpResponse addVideo(
            @RequestParam("title") String title,
            @RequestParam("desc") String desc,
            @RequestParam("video") MultipartFile video) throws IOException {

        //확장자 추출
        String extention = FilenameUtils.getExtension(video.getOriginalFilename());
        String filename = UUID.randomUUID().toString();

        System.out.println(filename);

        storageService.upload("video/" + filename +"."+extention, video.getInputStream());

        BroadcastDto broadcastDto = BroadcastDto.builder()
                .sellerId(1) // 추후 jwt 토큰에서 가져오기
                .title(title)
                .desc(desc)
                .content(filename+"."+extention)
                .build();

        Integer broadcastId = broadcastService.createVideo(broadcastDto);

        //입력 성공 시
        return HttpResponse.builder()
                .status(200)
                .message("저장 성공")
                .build();
    }

    //전체 조회 기능 (판매자)
    @GetMapping("/seller/list/{size}")
    public ResponseEntity<?> sellerList(@RequestParam(defaultValue = "0") int page, @PathVariable int size) throws IOException {
        //jwt 코드를 통해서 권한 확인 후 코드 실행
        int sellerId = 1;

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastDto> result = broadcastService.getBroadcastsBySellerId(sellerId,"","", pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,"판매자 동영상 리스트 조회 성공",result));
    }

    //검색 조회 기능 (판매자)
    @PostMapping("/seller/search")
    public ResponseEntity<?> sellerSearchList(@RequestBody BroadcastSearchDto broadcastSearchDto) throws IOException {
        //jwt 코드를 통해서 권한 확인 후 코드 실행
        int sellerId = 1;

        String keyword = broadcastSearchDto.getKeyword();
        String sortBy = broadcastSearchDto.getSort();

        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(broadcastSearchDto.getPage());
        pageRequestDto.setSize(broadcastSearchDto.getSize());

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastDto> result = broadcastService.getBroadcastsBySellerId(sellerId,keyword,sortBy,pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,"판매자 동영상 리스트 검색 조회 성공",result));
    }


    //전체 조회 기능 (관리자)
    @GetMapping("/admin/list/{size}")
    public ResponseEntity<?> adminList(@RequestParam(defaultValue = "0") int page, @PathVariable int size ) throws IOException {
        //jwt 코드를 통해서 권한 확인 후 코드 실행
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(size);

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastDto> result = broadcastService.getAllBroadcasts("","",pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,"관리자 동영상 리스트 조회 성공",result));
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
                .body(new HttpResponse(HttpStatus.OK,"관리자 동영상 리스트 검색 조회 성공",result));
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
                .status(200)
                .message("삭제 성공")
                .build();
    }
    
    //bilnd 상태 업데이트 (관리자, 판매자)
    @PutMapping("updateStatus/{videoId}")
    public HttpResponse updateStatus(@PathVariable Integer videoId) {

        Integer result = broadcastService.updateStatus(videoId);

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

    //전체 조회 기능 (메인)
    @GetMapping("/main/list")
    public ResponseEntity<?> adminList(@RequestParam(defaultValue = "0") int page) throws IOException {
        //jwt 코드를 통해서 권한 확인 후 코드 실행
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(page);
        pageRequestDto.setSize(8); //8개 씩 추출

        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<BroadcastListDto> result = broadcastService.findAllByIsBlindFalse(pageable);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK,"메인 동영상 리스트 조회 성공",result));
    }

}
