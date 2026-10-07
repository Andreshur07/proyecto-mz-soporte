package com.marz.soporte.service;

import com.marz.soporte.audit.HistorialCambio;
import com.marz.soporte.dto.*;
import com.marz.soporte.entity.*;
import com.marz.soporte.exception.ReglaNegocioException;
import com.marz.soporte.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.time.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class Sprint3ServiceTest {
    @Test void indicadoresIncluyenCerosYSinAsignar() {
        var repo=mock(SolicitudRepository.class); when(repo.findAll()).thenReturn(List.of(solicitud(1,EstadoSolicitud.NUEVO,Prioridad.ALTA,null),solicitud(2,EstadoSolicitud.CERRADO,Prioridad.BAJA,usuario(8,Rol.AGENTE))));
        var r=new IndicadorService(repo).obtener();
        assertThat(r.totalSolicitudes()).isEqualTo(2);assertThat(r.porEstado().get(EstadoSolicitud.NUEVO)).isEqualTo(1);assertThat(r.porEstado().get(EstadoSolicitud.RESUELTO)).isZero();assertThat(r.porPrioridad().get(Prioridad.ALTA)).isEqualTo(1);assertThat(r.sinAsignar()).isEqualTo(1);
    }
    @Test void csvEscapaComasComillasSaltosYNull() {
        var service=mock(SolicitudService.class);var s=solicitud(1,EstadoSolicitud.NUEVO,Prioridad.MEDIA,null);s.setTitulo("Uno, dos");s.setDescripcion("Dice \"hola\"\ny sigue");s.setJustificacionPrioridadAlta(null);when(service.buscar(any())).thenReturn(List.of(s));
        String csv=new String(new CsvService(service).exportar(new FiltroSolicitud(null,null,null,null,null,null)),StandardCharsets.UTF_8);
        assertThat(csv).startsWith("\uFEFFID,Titulo").contains("\"Uno, dos\"").contains("\"Dice \"\"hola\"\"\ny sigue\"").doesNotContain("password");
    }
    @Test void rangoSolicitudInvalidoSeRechaza() {assertThatThrownBy(()->SolicitudSpecification.conFiltros(new FiltroSolicitud(null,null,null,null,LocalDate.of(2026,2,2),LocalDate.of(2026,2,1)))).isInstanceOf(ReglaNegocioException.class);}
    @Test void auditoriaMapeaActorSeguroYOrdena() {
        var repo=mock(HistorialCambioRepository.class);var h=new HistorialCambio();h.setSolicitud(solicitud(4,EstadoSolicitud.NUEVO,Prioridad.MEDIA,null));h.setActor(usuario(3,Rol.COORDINADOR));h.setFecha(Instant.now());h.setCampo("prioridad");h.setValorAnterior("MEDIA");h.setValorNuevo("ALTA");when(repo.findAll(any(Specification.class),any(Sort.class))).thenReturn(List.of(h));
        var r=new AuditoriaService(repo).consultar(new FiltroAuditoria(4L,3L,"prioridad",null,null));assertThat(r).hasSize(1);assertThat(r.get(0).actor().rol()).isEqualTo(Rol.COORDINADOR);verify(repo).findAll(any(Specification.class),any(Sort.class));
    }
    @Test void rangoAuditoriaInvalidoSeRechaza() {var repo=mock(HistorialCambioRepository.class);assertThatThrownBy(()->new AuditoriaService(repo).consultar(new FiltroAuditoria(null,null,null,LocalDate.now(),LocalDate.now().minusDays(1)))).isInstanceOf(ReglaNegocioException.class);}
    private static Usuario usuario(long id,Rol rol){Usuario u=new Usuario();u.setId(id);u.setNombre("U"+id);u.setCorreo("u"+id+"@x");u.setRol(rol);u.setActivo(true);return u;}
    private static Solicitud solicitud(long id,EstadoSolicitud e,Prioridad p,Usuario agente){Solicitud s=new Solicitud();s.setId(id);s.setTitulo("T");s.setDescripcion("D");s.setCategoria(CategoriaSolicitud.SOFTWARE);s.setFechaCreacion(Instant.now());s.setEstado(e);s.setPrioridad(p);s.setSolicitante(usuario(1,Rol.SOLICITANTE));s.setAgenteAsignado(agente);return s;}
}
