package org.example.gwansangspringaibackend.user.repository;

import java.util.Optional;

import org.example.gwansangspringaibackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
