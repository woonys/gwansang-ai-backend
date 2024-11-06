package org.example.gwansangspringaibackend.domain;

import java.time.LocalDateTime;

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
@Table(name = "fortune_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FortuneHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fortuneId;

    @Column(columnDefinition = "TEXT")
    private String apiResponse;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    private LocalDateTime processedAt;

    @Builder
    public FortuneHistory(Long fortuneId, String apiResponse, String errorMessage, ProcessStatus status) {
        this.fortuneId = fortuneId;
        this.apiResponse = apiResponse;
        this.errorMessage = errorMessage;
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}
