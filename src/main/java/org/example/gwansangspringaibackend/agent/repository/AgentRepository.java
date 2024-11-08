package org.example.gwansangspringaibackend.agent.repository;

import java.util.List;
import java.util.Optional;

import org.example.gwansangspringaibackend.agent.domain.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByName(String name);

    List<Agent> findAllByIsActiveTrue();
}
