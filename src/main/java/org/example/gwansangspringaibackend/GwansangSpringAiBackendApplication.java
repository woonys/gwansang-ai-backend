package org.example.gwansangspringaibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GwansangSpringAiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GwansangSpringAiBackendApplication.class, args);
    }

}
