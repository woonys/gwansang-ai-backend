package org.example.gwansangspringaibackend.domain.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateRequest {
    @Size(min = 2, max = 50)
    private String nickname;

    @Builder
    public UserUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
}
