package org.example.gwansangspringaibackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.gwansangspringaibackend.domain.Prompt;
import org.example.gwansangspringaibackend.domain.exception.NotFoundException;
import org.example.gwansangspringaibackend.domain.request.PromptCreateRequest;
import org.example.gwansangspringaibackend.domain.response.PromptResponse;
import org.example.gwansangspringaibackend.repository.AgentRepository;
import org.example.gwansangspringaibackend.repository.PromptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PromptService {
    private final PromptRepository promptRepository;
    private final AgentRepository agentRepository;

    public List<PromptResponse> getPromptsByAgent(Long agentId) {
        validateAgent(agentId);
        return promptRepository.findByAgentIdAndIsActiveTrue(agentId).stream()
                               .map(PromptResponse::from)
                               .collect(Collectors.toList());
    }

    @Transactional
    public PromptResponse createPrompt(PromptCreateRequest request) {
        validateAgent(request.getAgentId());

        Prompt prompt = Prompt.builder()
                              .agentId(request.getAgentId())
                              .template(request.getTemplate())
                              .description(request.getDescription())
                              .build();

        return PromptResponse.from(promptRepository.save(prompt));
    }

    private void validateAgent(Long agentId) {
        if (!agentRepository.existsById(agentId)) {
            throw new NotFoundException("Agent not found: " + agentId);
        }
    }
}