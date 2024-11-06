package org.example.gwansangspringaibackend.domain.response;

import java.time.LocalDateTime;

import org.example.gwansangspringaibackend.domain.Agent;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AgentResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;

    @Builder
    private AgentResponse(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static AgentResponse from(Agent agent) {
        return AgentResponse.builder()
                            .id(agent.getId())
                            .name(agent.getName())
                            .description(agent.getDescription())
                            .createdAt(agent.getCreatedDate())
                            .build();
    }
}