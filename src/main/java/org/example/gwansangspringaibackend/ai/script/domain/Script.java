package org.example.gwansangspringaibackend.ai.script.domain;

import org.example.gwansangspringaibackend.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fortune_scripts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Script extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long agentId;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String template;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean isActive = true;

    @Builder
    public Script(Long agentId, String type, String template, String description) {
        this.agentId = agentId;
        this.type = type;
        this.template = template;
        this.description = description;
    }
}
