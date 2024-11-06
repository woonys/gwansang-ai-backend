package org.example.gwansangspringaibackend.domain.response;

import java.time.LocalDateTime;

import org.example.gwansangspringaibackend.domain.Prompt;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PromptResponse {
    private final Long id;
    private final Long agentId;
    private final String template;
    private final String description;
    private final LocalDateTime createdAt;

    @Builder
    private PromptResponse(Long id, Long agentId, String template,
                           String description, LocalDateTime createdAt) {
        this.id = id;
        this.agentId = agentId;
        this.template = template;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static PromptResponse from(Prompt prompt) {
        return PromptResponse.builder()
                             .id(prompt.getId())
                             .agentId(prompt.getAgentId())
                             .template(prompt.getTemplate())
                             .description(prompt.getDescription())
                             .createdAt(prompt.getCreatedDate())
                             .build();
    }
}
