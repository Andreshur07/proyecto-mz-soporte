package com.marz.soporte.controller;

import com.marz.soporte.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final String APPLICATION_NAME =
            "Sistema de Gestión Colaborativa de Solicitudes de Soporte";

    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("OK", APPLICATION_NAME));
    }
}

