package com.farmdora.farmdoraproduct.dto;

import com.farmdora.farmdoraproduct.entity.Seller;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMainDto {
    private Integer id;
    private Integer sellerId;
    private String sellerName;
    private String title;
    private String content;
    private String desc;
    private boolean isBlind;
    private LocalDateTime createdDate;
    private String thumbnailImage;
    private String streamUrl;

    // 생성자 추가 (seller 엔티티에서 필요한 정보만 추출)
    public BroadcastMainDto(Integer id, Seller seller, String title, String content, String desc,
                            boolean isBlind, LocalDateTime createdDate) {
        this.id = id;
        this.sellerId = seller.getId();
        this.sellerName = seller.getName();
        this.title = title;
        this.content = content;
        this.desc = desc;
        this.isBlind = isBlind;
        this.createdDate = createdDate;
    }
}
