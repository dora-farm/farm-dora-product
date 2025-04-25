package com.farmdora.farmdoraproduct.controller;

import com.farmdora.farmdoraproduct.common.response.HttpResponse;
import com.farmdora.farmdoraproduct.dto.BroadcastDto;
import com.farmdora.farmdoraproduct.service.BroadcastService;
import com.farmdora.farmdoraproduct.service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // 프론트와 테스트용 임시 추가
@RequestMapping("/my/seller/broadcast")
public class BroadcastController {

    private final BroadcastService broadcastService;
    private final StorageService storageService;

    public BroadcastController(BroadcastService broadcastService, StorageService storageService) {
        this.broadcastService = broadcastService;
        this.storageService = storageService;
    }

    @PostMapping("register/{title}")
    public HttpResponse addProduct(
            @PathVariable("title") String title,
            @RequestPart("file") MultipartFile file) throws IOException {

        //확장자 추출
        String extention = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString();

        storageService.upload("video/" + filename +"."+extention, file.getInputStream());

        BroadcastDto broadcastDto = BroadcastDto.builder()
                .sellerId(1) // 추후 jwt 토큰에서 가져오기
                .title(title)
                .content(filename)
                .build();

        Integer broadcastId = broadcastService.createVideo(broadcastDto);

        //입력 성공 시
        return HttpResponse.builder()
                .status(200)
                .message("저장 성공")
                .build();
    }


}
