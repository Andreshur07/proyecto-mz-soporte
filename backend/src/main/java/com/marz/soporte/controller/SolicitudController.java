package com.marz.soporte.controller;

import com.marz.soporte.dto.CambiarPrioridadRequest;
import com.marz.soporte.dto.CrearSolicitudRequest;
import com.marz.soporte.dto.SolicitudResponse;
import com.marz.soporte.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    private final SolicitudService solicitudService;
    public SolicitudController(SolicitudService solicitudService) { this.solicitudService = solicitudService; }

    @PostMapping
    @PreAuthorize("hasRole('SOLICITANTE')")
    public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody CrearSolicitudRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitudService.crear(request, authentication.getName()));
    }

    @GetMapping("/mias")
    @PreAuthorize("hasRole('SOLICITANTE')")
    public List<SolicitudResponse> mias(Authentication authentication) {
        return solicitudService.mias(authentication.getName());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SOLICITANTE')")
    public SolicitudResponse propia(@PathVariable Long id, Authentication authentication) {
        return solicitudService.propia(id, authentication.getName());
    }

    @GetMapping
    @PreAuthorize("hasRole('COORDINADOR')")
    public List<SolicitudResponse> listar() { return solicitudService.listar(); }

    @PatchMapping("/{id}/prioridad")
    @PreAuthorize("hasRole('COORDINADOR')")
    public SolicitudResponse cambiarPrioridad(@PathVariable Long id,
                                              @Valid @RequestBody CambiarPrioridadRequest request,
                                              Authentication authentication) {
        return solicitudService.cambiarPrioridad(id, request.prioridad(), authentication.getName());
    }
}
