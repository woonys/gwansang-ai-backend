package org.example.gwansangspringaibackend.domain.response;

import java.time.LocalDateTime;

import org.example.gwansangspringaibackend.domain.Fortune;
import org.example.gwansangspringaibackend.domain.FortuneStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FortuneResponse {
    private final Long id;
    private final Long userId;
    private final Long promptId;
    private final String imageUrl;
    private final String result;
    private final String instaId;
    private final FortuneStatus status;
    private final LocalDateTime createdAt;

    @Builder
    private FortuneResponse(Long id, Long userId, Long promptId, String imageUrl,
                            String result, String instaId, FortuneStatus status,
                            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.promptId = promptId;
        this.imageUrl = imageUrl;
        this.result = result;
        this.instaId = instaId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static FortuneResponse from(Fortune fortune) {
        return FortuneResponse.builder()
                              .id(fortune.getId())
                              .userId(fortune.getUserId())
                              .promptId(fortune.getPromptId())
                              .imageUrl(fortune.getImageUrl())
                              .result(fortune.getResult())
                              .instaId(fortune.getInstaId())
                              .status(fortune.getStatus())
                              .createdAt(fortune.getCreatedDate())
                              .build();
    }
}
