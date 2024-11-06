package org.example.gwansangspringaibackend.repository;

import java.util.List;
import java.util.Optional;

import org.example.gwansangspringaibackend.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findByAgentIdAndIsActiveTrue(Long agentId);
    Optional<Prompt> findByAgentId(Long agentId);
}
