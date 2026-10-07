package com.marz.soporte.controller;

import com.marz.soporte.dto.*;
import com.marz.soporte.service.AuditoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {
    private final AuditoriaService service;
    public AuditoriaController(AuditoriaService service) { this.service = service; }
    @GetMapping @PreAuthorize("hasRole('AUDITOR')")
    public List<AuditoriaResponse> consultar(@RequestParam(required=false) Long solicitudId,
            @RequestParam(required=false) Long actorId, @RequestParam(required=false) String campo,
            @RequestParam(required=false) LocalDate fechaDesde, @RequestParam(required=false) LocalDate fechaHasta) {
        return service.consultar(new FiltroAuditoria(solicitudId, actorId, campo, fechaDesde, fechaHasta));
    }
}
