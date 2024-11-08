package org.example.gwansangspringaibackend.user.domain;

import java.time.LocalDateTime;

import org.example.gwansangspringaibackend.common.BaseTimeEntity;
import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private int point;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private LocalDateTime lastLoginAt;

    @Builder
    public User(String email, String nickname, int point, Provider provider, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.point = point;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void updateProfile(String nickname) {
        if (StringUtils.hasText(nickname)) {
            this.nickname = nickname;
        }
    }

    //포인트 충전
    public void addPoint(int point) {
        this.point += point;
    }

    //관상 볼 때마다 포인트 차감
    public void subtractPoint(int point) {
        this.point -= point;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
