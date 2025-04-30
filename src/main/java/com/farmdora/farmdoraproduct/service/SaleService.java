package com.farmdora.farmdoraproduct.service;

import com.farmdora.farmdoraproduct.dto.*;
import com.farmdora.farmdoraproduct.entity.*;
import com.farmdora.farmdoraproduct.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleFileRepository saleFileRepository;
    private final OptionRepository optionRepository;
    private final SellerRepository sellerRepository;
    private final SaleTypeRepository saleTypeRepository;

    private final StorageService storageService;

    // 생성자 주입
    public SaleService(SaleRepository saleRepository,
                       SaleFileRepository saleFileRepository,
                       OptionRepository optionRepository,
                       SellerRepository sellerRepository,
                       SaleTypeRepository saleTypeRepository,
                       StorageService storageService) {
        this.saleRepository = saleRepository;
        this.saleFileRepository = saleFileRepository;
        this.optionRepository = optionRepository;
        this.sellerRepository = sellerRepository;
        this.saleTypeRepository = saleTypeRepository;
        this.storageService = storageService;
    }

    public Integer createSale(SaleRequestDto requestDto) {
        // 1. Seller 엔티티 조회
        Seller seller = sellerRepository.findById(requestDto.getSellerId())
                .orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));

        // SaleType 조회
        SaleType saleType = saleTypeRepository.findById(requestDto.getTypeId())
                .orElseThrow(() -> new RuntimeException("옵션 타입을 찾을 수 없습니다."));

        // 2. Sale 엔티티 생성 및 저장
        Sale sale = Sale.builder()
                .seller(seller)
                .title(requestDto.getTitle())
                .type(saleType)
                .content(requestDto.getContent())
                .origin(requestDto.getOrigin())
                .isBlind(false)  // 초기값 설정
                .build();

        Sale savedSale = saleRepository.save(sale);

        // 3. SaleFile 엔티티들 생성 및 저장
        if (requestDto.getFiles() != null) {
            for (SaleFileDto fileDto : requestDto.getFiles()) {
                SaleFile saleFile = SaleFile.builder()
                        .sale(savedSale)
                        .saveFile(fileDto.getSaveFile())
                        .originFile(fileDto.getOriginFile())
                        .isMain(fileDto.isMain())
                        .build();

                saleFileRepository.save(saleFile);
            }
        }

        // 4. Option 엔티티들 생성 및 저장
        if (requestDto.getOptions() != null) {
            for (OptionDto optionDto : requestDto.getOptions()) {

                Option option = Option.builder()
                        .sale(savedSale)
                        .name(optionDto.getName())
                        .price(optionDto.getPrice())
                        .quantity(optionDto.getQuantity())
                        .isStop(false)  // 초기값 설정
                        .build();

                optionRepository.save(option);
            }
        }

        return savedSale.getId();
    }

    // 판매글 ID로 user_id 조회
    public Integer getUserIdBySaleId(Integer saleId) {
        return saleRepository.findUserIdBySaleId(saleId);
    }

    public void deleteSale(Integer saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 Sale ID입니다: " + saleId));

        List<SaleFile> saleFiles = saleFileRepository.findBySale(sale);

        //ObjectStorage의 파일 우선 제거
        for(SaleFile saleFile:saleFiles){
            storageService.delete("product/"+saleFile.getSaveFile());
        }
        //sale_file에 저장된 파일 정보 지우기
        saleFileRepository.deleteAll(saleFiles);
        //option에 저장된 정보 지우기
        List<Option> options = optionRepository.findBySale(sale);
        optionRepository.deleteAll(options);

        //sale에 저장된 정보 지우기
        saleRepository.delete(sale);
    }

    public SaleDetailDto getProductDetail(Integer productId) {
        Sale sale = saleRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 Sale ID입니다: " + productId));

        List<SaleFile> saleFiles = saleFileRepository.findBySale(sale);
        List<Option> options = optionRepository.findBySale(sale);
        List<OptionDto> optionDtos = new ArrayList<>();
        String mainImage = null;
        List<String> detailImages = new ArrayList<>();

        for(SaleFile saleFile: saleFiles){
            if(!saleFile.isMain()){
                mainImage = storageService.getObjectStorageImageUrl(saleFile.getSaveFile());
            }
            else{
                detailImages.add(storageService.getObjectStorageImageUrl(saleFile.getSaveFile()));
            }
        }

        for (Option option : options) {
            OptionDto optionDto = OptionDto.builder()
                    .name(option.getName())
                    .price(option.getPrice())
                    .quantity(option.getQuantity())
                    .build();
            optionDtos.add(optionDto);
        }
        SaleType saleType = sale.getType();
        SaleTypeDto saleTypeDto = SaleTypeDto.from(saleType);

        SaleDetailDto saleDetailDto = SaleDetailDto
                .builder()
                .id(sale.getId())
                .title(sale.getTitle())
                .content(sale.getContent())
                .origin(sale.getOrigin())
                .options(optionDtos)
                .bigCategory(saleTypeDto.getTypeBigName())
                .smallCategory(saleTypeDto.getTypeName())
                .mainImage(mainImage)
                .detailImages(detailImages)
                .build();

        return saleDetailDto;
    }

    public int updateSale(SaleRequestDto requestDto, List<MultipartFile> files) throws IOException {
        Integer saleId = requestDto.getSaleId();
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 Sale ID입니다: " + saleId));

        // saleID 기준으로 우선 파일과 옵션 제거 후 다시 입력받은 정보 주입
        List<SaleFile> saleFiles = saleFileRepository.findBySale(sale);
        //ObjectStorage의 파일 제거
        for(SaleFile saleFile:saleFiles){
            storageService.delete("product/"+saleFile.getSaveFile());
        }
        //sale_file db에 저장된 파일 정보 지우기
        saleFileRepository.deleteAll(saleFiles);
        //option에 저장된 정보 지우기
        List<Option> options = optionRepository.findBySale(sale);
        optionRepository.deleteAll(options);

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

        //Sale 수정(toBuilder) 이용하여 수정된 정보만 변경, 더티체킹 방식
        sale.setTitle(requestDto.getTitle());
        sale.setContent(requestDto.getContent());
        sale.setOrigin(requestDto.getOrigin());

        // SaleFile 엔티티들 생성 및 저장
        if (requestDto.getFiles() != null) {
            for (SaleFileDto fileDto : requestDto.getFiles()) {
                SaleFile saleFile = SaleFile.builder()
                        .sale(sale)
                        .saveFile(fileDto.getSaveFile())
                        .originFile(fileDto.getOriginFile())
                        .isMain(fileDto.isMain())
                        .build();

                saleFileRepository.save(saleFile);
            }
        }

        // Option 엔티티들 생성 및 저장
        if (requestDto.getOptions() != null) {
            for (OptionDto optionDto : requestDto.getOptions()) {
                // SaleType 조회
                SaleType saleType = saleTypeRepository.findById(requestDto.getTypeId())
                        .orElseThrow(() -> new RuntimeException("옵션 타입을 찾을 수 없습니다."));

                Option option = Option.builder()
                        .sale(sale)
                        .name(optionDto.getName())
                        .price(optionDto.getPrice())
                        .quantity(optionDto.getQuantity())
                        .isStop(false)  // 초기값 설정
                        .build();

                optionRepository.save(option);
            }
        }

        return 1;
    }

    public Integer updateStatus(Integer productId) {

        Sale sale = saleRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 Sale ID입니다: " + productId));

        sale.setBlind(!sale.isBlind());

        return 1;
    }
}