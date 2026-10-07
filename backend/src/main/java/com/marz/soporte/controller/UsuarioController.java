package com.marz.soporte.controller;
import com.marz.soporte.dto.UsuarioResumenResponse;
import com.marz.soporte.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/usuarios")
public class UsuarioController{
 private final UsuarioService service;public UsuarioController(UsuarioService service){this.service=service;}
 @GetMapping("/agentes") @PreAuthorize("hasRole('COORDINADOR')") public List<UsuarioResumenResponse> agentes(){return service.agentesActivos();}
}
