package org.example.gwansangspringaibackend.fortune.service;

import org.example.gwansangspringaibackend.fortune.domain.Fortune;
import org.example.gwansangspringaibackend.common.Image;
import org.example.gwansangspringaibackend.common.ImageProcessor;
import org.example.gwansangspringaibackend.ai.script.domain.Script;
import org.example.gwansangspringaibackend.common.exception.NotFoundException;
import org.example.gwansangspringaibackend.fortune.domain.request.FortuneAnalyzeRequest;
import org.example.gwansangspringaibackend.fortune.domain.response.FortuneResponse;
import org.example.gwansangspringaibackend.fortune.repository.FortuneRepository;
import org.example.gwansangspringaibackend.ai.script.repository.ScriptRepository;
import org.example.gwansangspringaibackend.service.SlackService;
import org.example.gwansangspringaibackend.ai.provider.service.AiProviderFactory;
import org.example.gwansangspringaibackend.ai.provider.service.AiProviderService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FortuneService {
    private final FortuneRepository fortuneRepository;
    private final ScriptRepository scriptRepository;
    private final AiProviderFactory aiProviderFactory;
    private final SlackService slackService;
    private final ImageProcessor imageProcessor;

    @Value("${ai.provider.default:ANTHROPIC}")
    private String defaultProvider;

    public FortuneResponse analyzeFortune(FortuneAnalyzeRequest request) {
        try {
            // 프롬프트 조회
            Script script = scriptRepository.findById(request.getPromptId())
                                            .orElseThrow(() -> new NotFoundException("Prompt not found"));

            // 이미지 처리
            Image image = null;
            if (StringUtils.hasText(request.getImageUrl())) {
                image = imageProcessor.processForAiAnalysis(request.getImageUrl());
            }

            // AI 제공자 선택
            AiProviderService aiProvider = aiProviderFactory.getProvider(
                request.getProvider() != null ? request.getProvider() : defaultProvider
            );

            // AI 분석 실행
            ChatResponse chatResponse = aiProvider.generateFortune(
                script.getTemplate(),
                image
            );

            String result = chatResponse.getResult().getOutput().getContent();

            // Fortune 저장
            Fortune fortune = Fortune.builder()
                                     .userId(request.getUserId())
                                     .promptId(request.getPromptId())
                                     .imageUrl(request.getImageUrl())
                                     .result(result)
                                     .aiProviderName(aiProvider.getProviderName())
                                     .build();

            Fortune savedFortune = fortuneRepository.save(fortune);
            slackService.sendMonitoringMessage(savedFortune);

            return FortuneResponse.from(savedFortune);
        } catch (Exception e) {
            log.error("Fortune analysis failed", e);
            slackService.sendErrorMessage(e.getMessage(), "Fortune analysis");
            throw e;
        }
    }
}
