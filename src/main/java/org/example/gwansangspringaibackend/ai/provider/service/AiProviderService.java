package org.example.gwansangspringaibackend.ai.provider.service;

import org.example.gwansangspringaibackend.common.Image;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.codec.ServerSentEvent;

import reactor.core.publisher.Flux;

public interface AiProviderService {
    ChatResponse generateFortune(String prompt, Image image);

    Flux<ChatResponse> generateFortuneStreaming(String basePrompt, Image image);

    Flux<ServerSentEvent<String>> generateFortuneSSE(String basePrompt, Image image);

    boolean supportsImageAnalysis();
    String getProviderName();
}
