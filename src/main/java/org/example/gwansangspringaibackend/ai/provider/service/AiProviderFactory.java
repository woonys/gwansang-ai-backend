package org.example.gwansangspringaibackend.ai.provider.service;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AiProviderFactory {
    private final List<AiProviderService> providers;

    public AiProviderService getProvider(String providerName) {
        return providers.stream()
                        .filter(provider -> provider.getProviderName().equals(providerName.toUpperCase()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unsupported AI provider: " + providerName));
    }
}
