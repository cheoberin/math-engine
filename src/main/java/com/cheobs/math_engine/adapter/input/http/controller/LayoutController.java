package com.cheobs.math_engine.adapter.input.http.controller;

import com.cheobs.math_engine.adapter.input.http.api.LayoutApi;
import com.cheobs.math_engine.adapter.input.http.dto.layout.request.LayoutRequestDto;
import com.cheobs.math_engine.adapter.input.http.dto.layout.response.LayoutDetailsResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LayoutController implements LayoutApi {



    @Override
    public ResponseEntity<LayoutDetailsResponseDto> createLayout(LayoutRequestDto layoutRequestDto) {
        return null;
    }
}
