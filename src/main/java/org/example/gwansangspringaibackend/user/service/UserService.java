package org.example.gwansangspringaibackend.user.service;

import org.example.gwansangspringaibackend.user.domain.User;
import org.example.gwansangspringaibackend.common.exception.DuplicateException;
import org.example.gwansangspringaibackend.common.exception.NotFoundException;
import org.example.gwansangspringaibackend.user.domain.request.UserUpdateRequest;
import org.example.gwansangspringaibackend.user.domain.response.UserResponse;
import org.example.gwansangspringaibackend.user.repository.UserRepository;
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
