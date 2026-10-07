package com.marz.soporte.controller;
import com.marz.soporte.dto.*;
import com.marz.soporte.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/solicitudes/{solicitudId}/comentarios")
public class ComentarioController{
 private final ComentarioService service;public ComentarioController(ComentarioService service){this.service=service;}
 @PostMapping @PreAuthorize("hasRole('AGENTE')") public ResponseEntity<ComentarioResponse> crear(@PathVariable Long solicitudId,@Valid @RequestBody CrearComentarioRequest r,Authentication a){return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(solicitudId,r.contenido(),a.getName()));}
 @GetMapping @PreAuthorize("hasAnyRole('SOLICITANTE','AGENTE','COORDINADOR','AUDITOR')") public List<ComentarioResponse> listar(@PathVariable Long solicitudId,Authentication a){return service.listar(solicitudId,a.getName());}
}
