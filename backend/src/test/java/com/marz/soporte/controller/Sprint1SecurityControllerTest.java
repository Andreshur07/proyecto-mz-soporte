package com.marz.soporte.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marz.soporte.dto.LoginResponse;
import com.marz.soporte.dto.SolicitudResponse;
import com.marz.soporte.dto.UsuarioResponse;
import com.marz.soporte.entity.CategoriaSolicitud;
import com.marz.soporte.entity.EstadoSolicitud;
import com.marz.soporte.entity.Prioridad;
import com.marz.soporte.entity.Rol;
import com.marz.soporte.exception.GlobalExceptionHandler;
import com.marz.soporte.exception.RecursoNoEncontradoException;
import com.marz.soporte.security.JwtAccessDeniedHandler;
import com.marz.soporte.security.JwtAuthenticationEntryPoint;
import com.marz.soporte.security.JwtAuthenticationFilter;
import com.marz.soporte.security.JwtService;
import com.marz.soporte.security.SecurityConfig;
import com.marz.soporte.service.AuthService;
import com.marz.soporte.service.SolicitudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({SolicitudController.class, AuthController.class, HealthController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class, GlobalExceptionHandler.class})
class Sprint1SecurityControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean SolicitudService solicitudService;
    @MockitoBean AuthService authService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailsService userDetailsService;

    @Test
    void healthPermanecePublico() throws Exception {
        mockMvc.perform(get("/api/health")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    void endpointProtegidoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Error\",\"descripcion\":\"Detalle\",\"categoria\":\"SOFTWARE\"}"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void loginEsPublicoYDevuelveJwt() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse("jwt-prueba", "Bearer",
                new UsuarioResponse(1L, "Solicitante", "solicitante@marz.local", Rol.SOLICITANTE)));
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"solicitante@marz.local\",\"password\":\"Marz2026!\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").value("jwt-prueba"));
    }

    @Test
    @WithMockUser(username = "solicitante@marz.local", roles = "SOLICITANTE")
    void solicitanteCreaSolicitudValidaCon201() throws Exception {
        when(solicitudService.crear(any(), eq("solicitante@marz.local"))).thenReturn(respuesta(10L, Prioridad.MEDIA));
        mockMvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Error\",\"descripcion\":\"Detalle\",\"categoria\":\"SOFTWARE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("NUEVO"))
                .andExpect(jsonPath("$.prioridad").value("MEDIA"));
    }

    @Test
    @WithMockUser(roles = "SOLICITANTE")
    void crearSinDatosObligatoriosDevuelve400() throws Exception {
        mockMvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "AGENTE")
    void otroRolNoPuedeCrearSolicitud() throws Exception {
        mockMvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Error\",\"descripcion\":\"Detalle\",\"categoria\":\"SOFTWARE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "solicitante@marz.local", roles = "SOLICITANTE")
    void solicitanteConsultaSusSolicitudes() throws Exception {
        when(solicitudService.mias("solicitante@marz.local")).thenReturn(List.of(respuesta(12L, Prioridad.MEDIA)));
        mockMvc.perform(get("/api/solicitudes/mias")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(12));
    }

    @Test
    @WithMockUser(username = "solicitante@marz.local", roles = "SOLICITANTE")
    void accesoASolicitudAjenaSeRechazaComo404() throws Exception {
        when(solicitudService.propia(99L, "solicitante@marz.local"))
                .thenThrow(new RecursoNoEncontradoException("Solicitud no encontrada"));
        mockMvc.perform(get("/api/solicitudes/99")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "coordinador@marz.local", roles = "COORDINADOR")
    void coordinadorListaSolicitudes() throws Exception {
        when(solicitudService.listar()).thenReturn(List.of(respuesta(10L, Prioridad.MEDIA)));
        mockMvc.perform(get("/api/solicitudes")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    @WithMockUser(username = "coordinador@marz.local", roles = "COORDINADOR")
    void coordinadorCambiaPrioridad() throws Exception {
        when(solicitudService.cambiarPrioridad(10L, Prioridad.ALTA, "coordinador@marz.local"))
                .thenReturn(respuesta(10L, Prioridad.ALTA));
        mockMvc.perform(patch("/api/solicitudes/10/prioridad").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prioridad\":\"ALTA\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.prioridad").value("ALTA"));
    }

    @Test
    @WithMockUser(roles = "SOLICITANTE")
    void solicitanteNoPuedePriorizar() throws Exception { assertPriorizarForbidden(); }
    @Test
    @WithMockUser(roles = "AGENTE")
    void agenteNoPuedePriorizar() throws Exception { assertPriorizarForbidden(); }
    @Test
    @WithMockUser(roles = "AUDITOR")
    void auditorNoPuedePriorizar() throws Exception { assertPriorizarForbidden(); }

    @Test
    @WithMockUser(username = "coordinador@marz.local", roles = "COORDINADOR")
    void priorizarSolicitudInexistenteDevuelve404() throws Exception {
        when(solicitudService.cambiarPrioridad(999L, Prioridad.ALTA, "coordinador@marz.local"))
                .thenThrow(new RecursoNoEncontradoException("Solicitud no encontrada"));
        mockMvc.perform(patch("/api/solicitudes/999/prioridad").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prioridad\":\"ALTA\"}"))
                .andExpect(status().isNotFound());
    }

    private void assertPriorizarForbidden() throws Exception {
        mockMvc.perform(patch("/api/solicitudes/10/prioridad").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prioridad\":\"ALTA\"}"))
                .andExpect(status().isForbidden());
    }
    private SolicitudResponse respuesta(Long id, Prioridad prioridad) {
        return new SolicitudResponse(id, "Error", "Detalle", CategoriaSolicitud.SOFTWARE,
                Instant.now(), EstadoSolicitud.NUEVO, prioridad);
    }
}
