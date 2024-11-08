package org.example.gwansangspringaibackend.ai.provider.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ai")
public class AiProperties {
    private Provider provider;

    @Data
    public static class Provider {
        private String defaultProvider;
        private Anthropic anthropic;
        private OpenAi openai;
    }

    @Data
    public static class Anthropic {
        private String apiKey;
        private String baseUrl;
        private String model;
        private int maxRetries;
        private Chat chat;
    }

    @Data
    public static class Chat {
        private Options options;
    }

    @Data
    public static class Options {
        private String model;
        private int temperature;
        private int maxTokens;
    }

    @Data
    public static class OpenAi {
        private String apiKey;
        private String baseUrl;
        private String model;
        private int maxRetries;
    }
}
