package com.marz.soporte.controller;

import com.marz.soporte.dto.IndicadoresResponse;
import com.marz.soporte.service.IndicadorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/indicadores")
public class IndicadorController {
    private final IndicadorService service;
    public IndicadorController(IndicadorService service) { this.service = service; }
    @GetMapping @PreAuthorize("hasRole('COORDINADOR')")
    public IndicadoresResponse obtener() { return service.obtener(); }
}
