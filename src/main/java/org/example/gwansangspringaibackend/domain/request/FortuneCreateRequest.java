package org.example.gwansangspringaibackend.domain.request;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FortuneCreateRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long promptId;

    @URL
    private String imageUrl;

    private String instaId;

    @Builder
    public FortuneCreateRequest(Long userId, Long promptId, String imageUrl, String instaId) {
        this.userId = userId;
        this.promptId = promptId;
        this.imageUrl = imageUrl;
        this.instaId = instaId;
    }
}
