package com.marz.soporte.controller;

import com.marz.soporte.dto.*;
import com.marz.soporte.entity.*;
import com.marz.soporte.exception.*;
import com.marz.soporte.security.*;
import com.marz.soporte.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({SolicitudController.class, ComentarioController.class, IndicadorController.class,
        AuditoriaController.class, ExportacionController.class})
@Import({SecurityConfig.class,JwtAuthenticationFilter.class,JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class,GlobalExceptionHandler.class})
class Sprint3SecurityControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean SolicitudService solicitudes; @MockitoBean ComentarioService comentarios;
    @MockitoBean IndicadorService indicadores; @MockitoBean AuditoriaService auditoria;
    @MockitoBean CsvService csv; @MockitoBean JwtService jwt; @MockitoBean UserDetailsService details;

    @Test @WithMockUser(roles="COORDINADOR") void filtrosCombinadosLleganAlServicio() throws Exception {
        when(solicitudes.listar(any(FiltroSolicitud.class))).thenReturn(List.of(respuesta()));
        mvc.perform(get("/api/solicitudes?estado=EN_PROCESO&prioridad=ALTA&categoria=SOFTWARE&agenteId=2&fechaDesde=2026-01-01&fechaHasta=2026-12-31"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1));
    }
    @Test @WithMockUser(roles="COORDINADOR") void enumInvalidoDevuelve400() throws Exception {
        mvc.perform(get("/api/solicitudes?estado=INVALIDO")).andExpect(status().isBadRequest());
    }
    @ParameterizedTest @ValueSource(strings={"estado=NUEVO","prioridad=ALTA","categoria=RED","agenteId=2","fechaDesde=2026-01-01","fechaHasta=2026-12-31"})
    @WithMockUser(roles="COORDINADOR") void cadaFiltroEsAceptado(String parametro) throws Exception {
        when(solicitudes.listar(any(FiltroSolicitud.class))).thenReturn(List.of());
        mvc.perform(get("/api/solicitudes?"+parametro)).andExpect(status().isOk());
    }
    @Test @WithMockUser(roles="SOLICITANTE") void solicitanteNoUsaFiltros() throws Exception {
        mvc.perform(get("/api/solicitudes?estado=NUEVO")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(roles="COORDINADOR") void coordinadorObtieneIndicadores() throws Exception {
        when(indicadores.obtener()).thenReturn(new IndicadoresResponse(3,Map.of(EstadoSolicitud.NUEVO,2L),Map.of(Prioridad.MEDIA,3L),1));
        mvc.perform(get("/api/indicadores")).andExpect(status().isOk()).andExpect(jsonPath("$.totalSolicitudes").value(3)).andExpect(jsonPath("$.sinAsignar").value(1));
    }
    @Test @WithMockUser(roles="SOLICITANTE") void solicitanteNoObtieneIndicadores() throws Exception { mvc.perform(get("/api/indicadores")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AGENTE") void agenteNoObtieneIndicadores() throws Exception { mvc.perform(get("/api/indicadores")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUDITOR") void auditorNoObtieneIndicadores() throws Exception { mvc.perform(get("/api/indicadores")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(roles="AUDITOR") void auditorConsultaHistorial() throws Exception {
        when(auditoria.consultar(any())).thenReturn(List.of(new AuditoriaResponse(1L,2L,new ActorAuditoriaResponse(3L,"A","a@x",Rol.AGENTE),Instant.now(),"estado","NUEVO","EN_PROCESO")));
        mvc.perform(get("/api/auditoria?solicitudId=2&actorId=3&campo=estado&fechaDesde=2026-01-01&fechaHasta=2026-12-31"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].actor.password").doesNotExist());
    }
    @Test @WithMockUser(roles="COORDINADOR") void coordinadorNoConsultaAuditoria() throws Exception { mvc.perform(get("/api/auditoria")).andExpect(status().isForbidden()); }
    @Test @WithMockUser(username="aud@x",roles="AUDITOR") void auditorConsultaComentarios() throws Exception {
        when(comentarios.listar(1L,"aud@x")).thenReturn(List.of()); mvc.perform(get("/api/solicitudes/1/comentarios")).andExpect(status().isOk());
    }
    @Test @WithMockUser(roles="AUDITOR") void auditorNoComenta() throws Exception {
        mvc.perform(post("/api/solicitudes/1/comentarios").contentType("application/json").content("{\"contenido\":\"x\"}")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(roles="AUDITOR") void auditorNoAsigna() throws Exception {
        mvc.perform(patch("/api/solicitudes/1/asignacion").contentType("application/json").content("{\"agenteId\":2}")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(roles="AUDITOR") void auditorNoCambiaEstado() throws Exception {
        mvc.perform(patch("/api/solicitudes/1/estado").contentType("application/json").content("{\"estado\":\"EN_PROCESO\"}")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(roles="COORDINADOR") void coordinadorExportaCsv() throws Exception {
        when(csv.exportar(any())).thenReturn("\uFEFFID,Titulo\r\n1,Prueba\r\n".getBytes(StandardCharsets.UTF_8));
        mvc.perform(get("/api/solicitudes/exportar?prioridad=ALTA")).andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition","attachment; filename=\"solicitudes.csv\""))
                .andExpect(content().contentTypeCompatibleWith("text/csv"));
    }
    @Test @WithMockUser(roles="AGENTE") void agenteNoExporta() throws Exception { mvc.perform(get("/api/solicitudes/exportar")).andExpect(status().isForbidden()); }
    private SolicitudResponse respuesta(){return new SolicitudResponse(1L,"T","D",CategoriaSolicitud.SOFTWARE,Instant.now(),EstadoSolicitud.EN_PROCESO,Prioridad.ALTA);}
}
