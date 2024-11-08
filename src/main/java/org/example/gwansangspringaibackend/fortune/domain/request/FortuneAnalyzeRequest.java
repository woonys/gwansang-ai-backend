package org.example.gwansangspringaibackend.fortune.domain.request;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FortuneAnalyzeRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long promptId;

    private String provider;  // AI 제공자 선택용

    @URL
    private String imageUrl;

    private Map<String, Object> additionalParams = new HashMap<>();  // AI 제공자별 추가 파라미터

    @Builder
    public FortuneAnalyzeRequest(Long userId, Long promptId, String provider,
                                 String imageUrl,
                                 Map<String, Object> additionalParams) {
        this.userId = userId;
        this.promptId = promptId;
        this.provider = provider;
        this.imageUrl = imageUrl;
        if (additionalParams != null) {
            this.additionalParams = additionalParams;
        }
    }
}