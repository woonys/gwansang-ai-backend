package org.example.gwansangspringaibackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.gwansangspringaibackend.domain.Agent;
import org.example.gwansangspringaibackend.domain.exception.DuplicateException;
import org.example.gwansangspringaibackend.domain.request.AgentCreateRequest;
import org.example.gwansangspringaibackend.domain.response.AgentResponse;
import org.example.gwansangspringaibackend.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AgentService {
    private final AgentRepository agentRepository;

    public List<AgentResponse> getAllActiveAgents() {
        return agentRepository.findAllByIsActiveTrue().stream()
                              .map(AgentResponse::from)
                              .collect(Collectors.toList());
    }

    @Transactional
    public AgentResponse createAgent(AgentCreateRequest request) {
        validateDuplicateName(request.getName());

        Agent agent = Agent.builder()
                           .name(request.getName())
                           .description(request.getDescription())
                           .build();

        return AgentResponse.from(agentRepository.save(agent));
    }

    private void validateDuplicateName(String name) {
        if (agentRepository.findByName(name).isPresent()) {
            throw new DuplicateException("Agent name already exists: " + name);
        }
    }
}
