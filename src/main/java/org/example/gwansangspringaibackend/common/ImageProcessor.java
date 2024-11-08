package org.example.gwansangspringaibackend.common;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImageProcessor {
    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    private static final int DEFAULT_TARGET_WIDTH = 800;

    public Image processForAiAnalysis(String imageUrl) {
        Image image = Image.from(imageUrl);

        if (image.exceedsMaxSize(MAX_SIZE_BYTES)) {
            log.info("Resizing large image: {} bytes", image.getSize());
            image = image.resize(DEFAULT_TARGET_WIDTH);
        }

        return image;
    }
}

