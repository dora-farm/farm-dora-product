package com.farmdora.farmdoraproduct.service;

import com.farmdora.farmdoraproduct.dto.BroadcastDto;
import com.farmdora.farmdoraproduct.entity.Broadcast;
import com.farmdora.farmdoraproduct.entity.Seller;
import com.farmdora.farmdoraproduct.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .content(broadcastDto.getContent())
                .isBlind(false)  // 초기값 설정
                .build();

        Broadcast savedBroadcast= broadcastRepository.save(broadcast);

        return savedBroadcast.getId();  // 생성된 방송의 ID 반환;
    }
}