package com.cheobs.math_engine.application.config;

import com.cheobs.math_engine.domain.port.input.LayoutUseCase;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import com.cheobs.math_engine.domain.service.LayoutService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LayoutConfig {

    @Bean
    public LayoutUseCase layoutUseCase(LayoutPort layoutPort) {
        return new LayoutService(layoutPort);
    }

}
