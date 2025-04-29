package com.farmdora.farmdoraproduct.service;

import com.farmdora.farmdoraproduct.dto.BroadcastDto;
import com.farmdora.farmdoraproduct.dto.BroadcastMainDto;
import com.farmdora.farmdoraproduct.dto.PageResponseDto;
import com.farmdora.farmdoraproduct.entity.Broadcast;
import com.farmdora.farmdoraproduct.entity.Sale;
import com.farmdora.farmdoraproduct.entity.Seller;
import com.farmdora.farmdoraproduct.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.service.spi.ServiceException;
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

    // 모든 방송 조회 // 관리자 및 메인페이지 용
    public PageResponseDto<BroadcastDto> getAllBroadcasts
    (String keyword, String sortBy, Pageable pageable) {

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
            broadcastPage = broadcastRepository.findByKeyword(keyword, pageableWithSort);
        } else {
            broadcastPage = broadcastRepository.findAll(pageableWithSort);
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

    // ID로 방송 조회
    public BroadcastDto getBroadcastById(Integer id) {
        Broadcast broadcast = broadcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다. ID: " + id));
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
                .orElseThrow(() -> new RuntimeException("동영상을 찾을 수 없습니다. ID: " + id));

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

    //관리자, 판매자 공통 사용
    public Integer updateStatus(Integer videoId) {

        Broadcast broadcast = broadcastRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 동영상 ID입니다: " + videoId));

        broadcast.setBlind(!broadcast.isBlind());

        return 1;
    }

    // 블라인드 처리되지 않은 동영상 조회
    public PageResponseDto<BroadcastMainDto> findAllByIsBlindFalse(Pageable pageable) {
        try {
            // 직접 리포지토리에서 결과 가져오기
            Page<BroadcastMainDto> broadcastPage = broadcastRepository.findAllNotBlindedAsDto(pageable);

            // 각 DTO에 thumbnailImage와 streamUrl 설정
            List<BroadcastMainDto> broadcastListDtoMain = broadcastPage.getContent().stream()
                    .peek(dto -> {
                        try {
                            dto.setThumbnailImage(storageService.getThumbnailUrl(dto.getContent()));
                            dto.setStreamUrl(storageService.getStreamUrl(dto.getContent()));
                        } catch (Exception e) {
                            // 기본값 설정
                            dto.setThumbnailImage("error_thumbnail_url");
                            dto.setStreamUrl("error_stream_url");
                        }
                    })
                    .collect(Collectors.toList());

            return new PageResponseDto<>(broadcastListDtoMain, broadcastPage);
        } catch (Exception e) {
            // 전체 처리 중 오류 발생 시 처리
            throw new ServiceException("방송 목록을 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    public BroadcastMainDto getVideoDetail(int id) {

        Broadcast broadcast = broadcastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 동영상 ID입니다: " + id));

        Seller seller = broadcast.getSeller(); // 판매자 정보 조회

        BroadcastMainDto dto = BroadcastMainDto.builder()
                .id(broadcast.getId())
                .sellerId(seller.getId())
                .sellerName(seller.getName())  // Seller 엔티티의 name 필드 사용
                .title(broadcast.getTitle())
                .content(broadcast.getContent())
                .desc(broadcast.getDesc())
                .isBlind(broadcast.isBlind())
                .createdDate(broadcast.getCreatedDate())  // BaseTimeEntity에서 상속받은 필드
                .build();

        dto.setThumbnailImage(storageService.getThumbnailUrl(broadcast.getContent()));
        dto.setStreamUrl(storageService.getStreamUrl(broadcast.getContent()));

        return dto;
    }
}