package org.example.gwansangspringaibackend.fortune.domain;

import org.example.gwansangspringaibackend.common.BaseTimeEntity;

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
@Table(name = "fortunes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fortune extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long promptId;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String result;

    private String aiProviderName;

    @Enumerated(EnumType.STRING)
    private FortuneStatus status = FortuneStatus.PENDING;

    @Builder
    public Fortune(Long userId, Long promptId, String imageUrl, String result, String aiProviderName) {
        this.userId = userId;
        this.promptId = promptId;
        this.imageUrl = imageUrl;
        this.result = result;
        this.aiProviderName = aiProviderName;
    }

    public void updateResult(String result) {
        this.result = result;
        this.status = FortuneStatus.COMPLETED;
    }
}