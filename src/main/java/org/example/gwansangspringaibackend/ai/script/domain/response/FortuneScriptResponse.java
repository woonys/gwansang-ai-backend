package org.example.gwansangspringaibackend.ai.script.domain.response;

import java.time.LocalDateTime;

import org.example.gwansangspringaibackend.ai.script.domain.Script;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FortuneScriptResponse {
    private final Long id;
    private final Long agentId;
    private final String template;
    private final String description;
    private final LocalDateTime createdAt;

    @Builder
    private FortuneScriptResponse(Long id, Long agentId, String template,
                                  String description, LocalDateTime createdAt) {
        this.id = id;
        this.agentId = agentId;
        this.template = template;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static FortuneScriptResponse from(Script script) {
        return FortuneScriptResponse.builder()
                                    .id(script.getId())
                                    .agentId(script.getAgentId())
                                    .template(script.getTemplate())
                                    .description(script.getDescription())
                                    .createdAt(script.getCreatedDate())
                                    .build();
    }
}
