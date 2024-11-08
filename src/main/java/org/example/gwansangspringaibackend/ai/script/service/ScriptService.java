package org.example.gwansangspringaibackend.ai.script.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.gwansangspringaibackend.ai.script.domain.Script;
import org.example.gwansangspringaibackend.ai.script.repository.ScriptRepository;
import org.example.gwansangspringaibackend.common.exception.NotFoundException;
import org.example.gwansangspringaibackend.fortune.domain.request.FortuneScriptCreateRequest;
import org.example.gwansangspringaibackend.ai.script.domain.response.FortuneScriptResponse;
import org.example.gwansangspringaibackend.agent.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ScriptService {
    private final ScriptRepository promptRepository;
    private final AgentRepository agentRepository;

    public List<FortuneScriptResponse> getScriptsByAgent(Long agentId) {
        validateAgent(agentId);
        return promptRepository.findByAgentIdAndIsActiveTrue(agentId).stream()
                               .map(FortuneScriptResponse::from)
                               .collect(Collectors.toList());
    }

    @Transactional
    public FortuneScriptResponse createScript(FortuneScriptCreateRequest request) {
        validateAgent(request.getAgentId());

        Script script = Script.builder()
                              .agentId(request.getAgentId())
                              .template(request.getTemplate())
                              .description(request.getDescription())
                              .build();

        return FortuneScriptResponse.from(promptRepository.save(script));
    }

    private void validateAgent(Long agentId) {
        if (!agentRepository.existsById(agentId)) {
            throw new NotFoundException("Agent not found: " + agentId);
        }
    }
}