package com.farmdora.farmdoraproduct.dto;

import com.farmdora.farmdoraproduct.entity.Broadcast;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastDto {
    private Integer id;
    private Integer sellerId;  // 엔티티의 연관관계 대신 ID만 사용
    private String title;
    private String desc;
    private String content;
    private boolean isBlind;
    private LocalDateTime createdAt;  // BaseTimeEntity에서 상속받은 필드
    private String thumbnailImage;
    private String StreamUrl;

    @Builder.Default
    private Integer page = 0;

    // Entity -> DTO 변환 메서드
    public static BroadcastDto fromEntity(Broadcast broadcast) {
        return BroadcastDto.builder()
                .id(broadcast.getId())
                .sellerId(broadcast.getSeller().getId())  // 연관된 Seller의 ID만 추출
                .title(broadcast.getTitle())
                .desc(broadcast.getDesc())
                .content(broadcast.getContent())
                .isBlind(broadcast.isBlind())
                .createdAt(broadcast.getCreatedDate())
                .build();
    }
}
