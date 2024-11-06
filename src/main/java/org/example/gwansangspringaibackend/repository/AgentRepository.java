package org.example.gwansangspringaibackend.repository;

import java.util.List;
import java.util.Optional;

import org.example.gwansangspringaibackend.domain.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByName(String name);
    List<Agent> findAllByIsActiveTrue();
}
