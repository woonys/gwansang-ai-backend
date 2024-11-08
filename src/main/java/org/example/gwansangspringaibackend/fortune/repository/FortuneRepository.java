package org.example.gwansangspringaibackend.fortune.repository;

import org.example.gwansangspringaibackend.fortune.domain.Fortune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FortuneRepository extends JpaRepository<Fortune, Long> {
}
