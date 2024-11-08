package org.example.gwansangspringaibackend.fortune.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FortuneScriptCreateRequest {
    @NotNull
    private Long agentId;

    @NotNull
    @Size(min = 10)
    private String template;

    @Size(max = 500)
    private String description;

    @Builder
    public FortuneScriptCreateRequest(Long agentId, String template, String description) {
        this.agentId = agentId;
        this.template = template;
        this.description = description;
    }
}
