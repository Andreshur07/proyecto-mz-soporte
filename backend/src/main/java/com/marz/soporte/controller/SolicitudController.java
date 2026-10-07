package com.marz.soporte.controller;
import com.marz.soporte.dto.*;
import com.marz.soporte.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/solicitudes")
public class SolicitudController{
 private final SolicitudService service;public SolicitudController(SolicitudService service){this.service=service;}
 @PostMapping @PreAuthorize("hasRole('SOLICITANTE')") public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody CrearSolicitudRequest r,Authentication a){return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(r,a.getName()));}
 @GetMapping("/mias") @PreAuthorize("hasRole('SOLICITANTE')") public List<SolicitudResponse> mias(Authentication a){return service.mias(a.getName());}
 @GetMapping("/asignadas") @PreAuthorize("hasRole('AGENTE')") public List<SolicitudResponse> asignadas(Authentication a){return service.asignadas(a.getName());}
 @GetMapping @PreAuthorize("hasRole('COORDINADOR')") public List<SolicitudResponse> listar(){return service.listar();}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('SOLICITANTE','AGENTE','COORDINADOR')") public SolicitudResponse detalle(@PathVariable Long id,Authentication a){return service.detalle(id,a.getName());}
 @PatchMapping("/{id}/prioridad") @PreAuthorize("hasRole('COORDINADOR')") public SolicitudResponse prioridad(@PathVariable Long id,@Valid @RequestBody CambiarPrioridadRequest r,Authentication a){return service.cambiarPrioridad(id,r,a.getName());}
 @PatchMapping("/{id}/asignacion") @PreAuthorize("hasRole('COORDINADOR')") public SolicitudResponse asignar(@PathVariable Long id,@Valid @RequestBody AsignarSolicitudRequest r,Authentication a){return service.asignar(id,r.agenteId(),a.getName());}
 @PatchMapping("/{id}/estado") @PreAuthorize("hasRole('AGENTE')") public SolicitudResponse estado(@PathVariable Long id,@Valid @RequestBody CambiarEstadoRequest r,Authentication a){return service.cambiarEstado(id,r.estado(),a.getName());}
 @PatchMapping("/{id}/confirmar-cierre") @PreAuthorize("hasRole('SOLICITANTE')") public SolicitudResponse cerrar(@PathVariable Long id,Authentication a){return service.confirmarCierre(id,a.getName());}
 @PatchMapping("/{id}/reabrir") @PreAuthorize("hasRole('SOLICITANTE')") public SolicitudResponse reabrir(@PathVariable Long id,@Valid @RequestBody ReabrirSolicitudRequest r,Authentication a){return service.reabrir(id,r.motivo(),a.getName());}
}
