package org.example.gwansangspringaibackend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.gwansangspringaibackend.domain.Role;
import org.example.gwansangspringaibackend.domain.User;
import org.example.gwansangspringaibackend.domain.exception.DuplicateException;
import org.example.gwansangspringaibackend.domain.exception.NotFoundException;
import org.example.gwansangspringaibackend.domain.request.UserCreateRequest;
import org.example.gwansangspringaibackend.domain.request.UserUpdateRequest;
import org.example.gwansangspringaibackend.domain.response.UserResponse;
import org.example.gwansangspringaibackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NotFoundException("User not found"));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NotFoundException("User not found"));

        user.updateProfile(request.getNickname());

        return UserResponse.from(user);
    }

    @Transactional
    public void updateLastLogin(Long id) {
        userRepository.findById(id)
                      .ifPresent(User::updateLastLoginAt);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateException("Email already exists: " + email);
        }
    }
}
