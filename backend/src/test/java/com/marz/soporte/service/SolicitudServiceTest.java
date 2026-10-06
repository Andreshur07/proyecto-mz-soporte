package com.marz.soporte.service;

import com.marz.soporte.audit.HistorialCambio;
import com.marz.soporte.dto.CrearSolicitudRequest;
import com.marz.soporte.entity.CategoriaSolicitud;
import com.marz.soporte.entity.EstadoSolicitud;
import com.marz.soporte.entity.Prioridad;
import com.marz.soporte.entity.Solicitud;
import com.marz.soporte.entity.Usuario;
import com.marz.soporte.exception.RecursoNoEncontradoException;
import com.marz.soporte.repository.HistorialCambioRepository;
import com.marz.soporte.repository.SolicitudRepository;
import com.marz.soporte.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitudServiceTest {
    private SolicitudRepository solicitudRepository;
    private UsuarioRepository usuarioRepository;
    private HistorialCambioRepository historialRepository;
    private SolicitudService service;
    private Usuario solicitante;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        historialRepository = mock(HistorialCambioRepository.class);
        service = new SolicitudService(solicitudRepository, usuarioRepository, historialRepository);
        solicitante = usuario(1L, "solicitante@marz.local");
    }

    @Test
    void crearAsignaNuevoMediaYPropietarioAutenticado() {
        when(usuarioRepository.findByCorreoIgnoreCase(solicitante.getCorreo())).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.save(any())).thenAnswer(invocation -> {
            Solicitud s = invocation.getArgument(0); s.setId(10L); return s;
        });
        var result = service.crear(new CrearSolicitudRequest("Error", "No inicia", CategoriaSolicitud.SOFTWARE),
                solicitante.getCorreo());
        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepository).save(captor.capture());
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.estado()).isEqualTo(EstadoSolicitud.NUEVO);
        assertThat(result.prioridad()).isEqualTo(Prioridad.MEDIA);
        assertThat(captor.getValue().getSolicitante()).isSameAs(solicitante);
    }

    @Test
    void miasConsultaSoloPorIdDelUsuarioAutenticado() {
        when(usuarioRepository.findByCorreoIgnoreCase(solicitante.getCorreo())).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.findBySolicitanteIdOrderByFechaCreacionDesc(1L))
                .thenReturn(List.of(solicitud(11L, solicitante, Prioridad.MEDIA)));
        var result = service.mias(solicitante.getCorreo());
        assertThat(result).extracting("id").containsExactly(11L);
        verify(solicitudRepository).findBySolicitanteIdOrderByFechaCreacionDesc(1L);
    }

    @Test
    void solicitudAjenaSeOcultaComoNoEncontrada() {
        Usuario otro = usuario(2L, "otro@marz.local");
        when(usuarioRepository.findByCorreoIgnoreCase(solicitante.getCorreo())).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.findById(20L)).thenReturn(Optional.of(solicitud(20L, otro, Prioridad.MEDIA)));
        assertThatThrownBy(() -> service.propia(20L, solicitante.getCorreo()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void coordinadorCambiaPrioridadYRegistraHistorialCompleto() {
        Usuario coordinador = usuario(3L, "coordinador@marz.local");
        Solicitud solicitud = solicitud(30L, solicitante, Prioridad.MEDIA);
        when(solicitudRepository.findById(30L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findByCorreoIgnoreCase(coordinador.getCorreo())).thenReturn(Optional.of(coordinador));
        var result = service.cambiarPrioridad(30L, Prioridad.ALTA, coordinador.getCorreo());
        ArgumentCaptor<HistorialCambio> captor = ArgumentCaptor.forClass(HistorialCambio.class);
        verify(historialRepository).save(captor.capture());
        HistorialCambio historial = captor.getValue();
        assertThat(result.prioridad()).isEqualTo(Prioridad.ALTA);
        assertThat(historial.getActor()).isSameAs(coordinador);
        assertThat(historial.getFecha()).isNotNull();
        assertThat(historial.getCampo()).isEqualTo("prioridad");
        assertThat(historial.getValorAnterior()).isEqualTo("MEDIA");
        assertThat(historial.getValorNuevo()).isEqualTo("ALTA");
    }

    @Test
    void priorizarSolicitudInexistenteDevuelveNoEncontrada() {
        when(solicitudRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.cambiarPrioridad(999L, Prioridad.ALTA, "coord@marz.local"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    private Usuario usuario(Long id, String correo) {
        Usuario u = new Usuario(); u.setId(id); u.setCorreo(correo); u.setNombre("Usuario"); return u;
    }
    private Solicitud solicitud(Long id, Usuario propietario, Prioridad prioridad) {
        Solicitud s = new Solicitud(); s.setId(id); s.setTitulo("Título"); s.setDescripcion("Descripción");
        s.setCategoria(CategoriaSolicitud.SOFTWARE); s.setFechaCreacion(Instant.now());
        s.setEstado(EstadoSolicitud.NUEVO); s.setPrioridad(prioridad); s.setSolicitante(propietario); return s;
    }
}
