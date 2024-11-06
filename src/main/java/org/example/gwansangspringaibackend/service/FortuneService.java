package org.example.gwansangspringaibackend.service;

import org.example.gwansangspringaibackend.repository.FortuneRepository;
import org.example.gwansangspringaibackend.repository.PromptRepository;
import org.example.gwansangspringaibackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FortuneService {
    private final FortuneRepository fortuneRepository;
    private final UserRepository userRepository;
    private final PromptRepository promptRepository;


}
