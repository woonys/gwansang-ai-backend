package org.example.gwansangspringaibackend.agent.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentCreateRequest {
    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @Size(max = 500)
    private String description;

    @Builder
    public AgentCreateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
