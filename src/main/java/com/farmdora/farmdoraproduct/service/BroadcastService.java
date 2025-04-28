package com.farmdora.farmdoraproduct.service;

import com.farmdora.farmdoraproduct.dto.BroadcastDto;
import com.farmdora.farmdoraproduct.dto.PageResponseDto;
import com.farmdora.farmdoraproduct.entity.Broadcast;
import com.farmdora.farmdoraproduct.entity.SaleFile;
import com.farmdora.farmdoraproduct.entity.Seller;
import com.farmdora.farmdoraproduct.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BroadcastService {

    private final SaleRepository saleRepository;
    private final SaleFileRepository saleFileRepository;
    private final OptionRepository optionRepository;
    private final SellerRepository sellerRepository;
    private final SaleTypeRepository saleTypeRepository;
    private final BroadcastRepository broadcastRepository;

    private final StorageService storageService;

    // 생성자 주입
    public BroadcastService(SaleRepository saleRepository,
                            SaleFileRepository saleFileRepository,
                            OptionRepository optionRepository,
                            SellerRepository sellerRepository,
                            SaleTypeRepository saleTypeRepository,
                            StorageService storageService,
                            BroadcastRepository broadcastRepository) {
        this.saleRepository = saleRepository;
        this.saleFileRepository = saleFileRepository;
        this.optionRepository = optionRepository;
        this.sellerRepository = sellerRepository;
        this.saleTypeRepository = saleTypeRepository;
        this.storageService = storageService;
        this.broadcastRepository = broadcastRepository;
    }

    public Integer createVideo(BroadcastDto broadcastDto) {
        // 1. Seller 엔티티 조회
        Seller seller = sellerRepository.findById(broadcastDto.getSellerId())
                .orElseThrow(() -> new EntityNotFoundException("판매자 ID: " + broadcastDto.getSellerId() + "를 찾을 수 없습니다."));

        // 2. Broadcast 엔티티 생성 및 저장
        Broadcast broadcast = Broadcast.builder()
                .seller(seller)
                .title(broadcastDto.getTitle())
                .desc(broadcastDto.getDesc())
                .content(broadcastDto.getContent())
                .isBlind(false)  // 초기값 설정
                .build();

        Broadcast savedBroadcast= broadcastRepository.save(broadcast);

        return savedBroadcast.getId();  // 생성된 방송의 ID 반환;
    }

    // 모든 방송 조회
    public PageResponseDto<BroadcastDto> getAllBroadcasts(Pageable pageable) {
        Page<Broadcast> broadcastPage = broadcastRepository.findAll(pageable);

        List<BroadcastDto> broadcastDtoList = broadcastPage.getContent().stream()
                .map(BroadcastDto::fromEntity)
                .peek(dto -> {
                    dto.setThumbnailImage(storageService.getThumbnailUrl(dto.getContent()));
                    dto.setStreamUrl(storageService.getStreamUrl(dto.getContent()));
                })
                .collect(Collectors.toList());

        return new PageResponseDto<>(broadcastDtoList, broadcastPage);
    }

    // ID로 방송 조회
    public BroadcastDto getBroadcastById(Integer id) {
        Broadcast broadcast = broadcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("방송을 찾을 수 없습니다. ID: " + id));
        return BroadcastDto.fromEntity(broadcast);
    }

    // Seller ID로 방송 목록 조회 (키워드 검색 및 정렬 기능 추가)
    public PageResponseDto<BroadcastDto> getBroadcastsBySellerId(
            Integer sellerId,
            String keyword,
            String sortBy,
            Pageable pageable) {

        // 정렬 설정
        Pageable pageableWithSort;
        if ("OLDEST".equals(sortBy)) {
            pageableWithSort = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "createdDate") // created_date 컬럼에 매핑되는 필드명
            );
        } else { // "LATEST"가 기본값
            pageableWithSort = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdDate") // created_date 컬럼에 매핑되는 필드명
            );
        }

        Page<Broadcast> broadcastPage;

        // 키워드 유무에 따른 쿼리 실행
        if (keyword != null && !keyword.trim().isEmpty()) {
            broadcastPage = broadcastRepository.searchBySellerId(sellerId, keyword, pageableWithSort);
        } else {
            broadcastPage = broadcastRepository.findBySellerId(sellerId, pageableWithSort);
        }

        List<BroadcastDto> broadcastDtoList = broadcastPage.getContent().stream()
                .map(BroadcastDto::fromEntity)
                .peek(dto -> {
                    dto.setThumbnailImage(storageService.getThumbnailUrl(dto.getContent()));
                    dto.setStreamUrl(storageService.getStreamUrl(dto.getContent()));
                })
                .collect(Collectors.toList());

        return new PageResponseDto<>(broadcastDtoList, broadcastPage);
    }
    // 방송 삭제
    @Transactional
    public void deleteBroadcast(Integer id) {
        Broadcast broadcast = broadcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("방송을 찾을 수 없습니다. ID: " + id));

        //오리지널 저장파일 제거
        storageService.delete("video/"+broadcast.getContent());
        //확장자 제외 파일이름 추출
        String filename = FilenameUtils.getBaseName(broadcast.getContent());
        //인코딩 파일 제거
        storageService.delete("encoding/farmdora/"+filename+"_AVC_FHD_1Pass_30fps.mp4");
        //썸네일 제거
        for(int i =1; i<11; i++) {
            if(i==10){ //썸네일 10개 모두 삭제
                storageService.delete("thumbnail/farmdora/" + filename + "_"+i+".jpg");
                continue;
            }
            storageService.delete("thumbnail/farmdora/" + filename + "_0"+i+".jpg");
        }
        broadcastRepository.deleteById(id);
    }
}