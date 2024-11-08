package org.example.gwansangspringaibackend.common;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.example.gwansangspringaibackend.common.exception.ImageProcessingException;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Image {
    private final byte[] data;
    private final String contentType;
    private final String originalUrl;

    @Builder
    private Image(byte[] data, String contentType, String originalUrl) {
        this.data = data;
        this.contentType = contentType;
        this.originalUrl = originalUrl;
    }

    public static Image from(String imageUrl) {
        try {
            WebClient webClient = WebClient.create();
            byte[] imageData = webClient.get()
                                        .uri(imageUrl)
                                        .retrieve()
                                        .bodyToMono(byte[].class)
                                        .block();

            String contentType = webClient.head()
                                          .uri(imageUrl)
                                          .retrieve()
                                          .toBodilessEntity()
                                          .block()
                                          .getHeaders()
                                          .getContentType()
                                          .toString();

            validateContentType(contentType);

            return Image.builder()
                        .data(imageData)
                        .contentType(contentType)
                        .originalUrl(imageUrl)
                        .build();
        } catch (Exception e) {
            throw new ImageProcessingException("Failed to create image from URL: " + imageUrl, e);
        }
    }

    public String toBase64() {
        return Base64.getEncoder().encodeToString(this.data);
    }

    public Image resize(int targetWidth) {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(this.data));
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int targetHeight = (targetWidth * originalHeight) / originalWidth;

            BufferedImage resizedImage = new BufferedImage(
                targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, getImageFormat(), baos);

            return Image.builder()
                        .data(baos.toByteArray())
                        .contentType(this.contentType)
                        .originalUrl(this.originalUrl)
                        .build();
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to resize image", e);
        }
    }

    public long getSize() {
        return this.data.length;
    }

    public boolean exceedsMaxSize(long maxSizeInBytes) {
        return getSize() > maxSizeInBytes;
    }

    private String getImageFormat() {
        return switch (this.contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> throw new ImageProcessingException("Unsupported image format: " + this.contentType);
        };
    }

    private static void validateContentType(String contentType) {
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImageProcessingException("Invalid content type: " + contentType);
        }
    }
}
