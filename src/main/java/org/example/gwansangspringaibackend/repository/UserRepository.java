package org.example.gwansangspringaibackend.repository;

import java.util.Optional;

import org.example.gwansangspringaibackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.introspect.AnnotationCollector;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
