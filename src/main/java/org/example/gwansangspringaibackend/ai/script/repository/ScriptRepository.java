package org.example.gwansangspringaibackend.ai.script.repository;

import java.util.List;
import java.util.Optional;

import org.example.gwansangspringaibackend.ai.script.domain.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findByAgentIdAndIsActiveTrue(Long agentId);
    Optional<Script> findByAgentId(Long agentId);
}
