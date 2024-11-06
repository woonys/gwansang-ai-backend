package org.example.gwansangspringaibackend.domain.response;

import java.time.LocalDateTime;

import org.example.gwansangspringaibackend.domain.Provider;
import org.example.gwansangspringaibackend.domain.Role;
import org.example.gwansangspringaibackend.domain.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final Provider provider;
    private final Role role;
    private final LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;

    @Builder
    private UserResponse(Long id, String email, String nickname, Provider provider,
                         Role role, LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.role = role;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }

    public static UserResponse from(User user) {
        return UserResponse.builder()
                           .id(user.getId())
                           .email(user.getEmail())
                           .nickname(user.getNickname())
                           .provider(user.getProvider())
                           .role(user.getRole())
                           .lastLoginAt(user.getLastLoginAt())
                           .createdAt(user.getCreatedDate())
                           .build();
    }
}
