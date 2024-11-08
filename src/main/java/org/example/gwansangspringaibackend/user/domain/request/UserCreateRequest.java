package org.example.gwansangspringaibackend.user.domain.request;

import org.example.gwansangspringaibackend.user.domain.Provider;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreateRequest {
    @NotNull
    @Email
    private String email;

    @Size(min = 2, max = 50)
    private String nickname;

    @NotNull
    private Provider provider;

    private String providerId;

    @Builder
    public UserCreateRequest(String email, String nickname, Provider provider, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
    }
}
