package com.marz.soporte.service;

import com.marz.soporte.audit.HistorialCambio;
import com.marz.soporte.dto.CrearSolicitudRequest;
import com.marz.soporte.dto.SolicitudResponse;
import com.marz.soporte.entity.EstadoSolicitud;
import com.marz.soporte.entity.Prioridad;
import com.marz.soporte.entity.Solicitud;
import com.marz.soporte.entity.Usuario;
import com.marz.soporte.exception.RecursoNoEncontradoException;
import com.marz.soporte.repository.HistorialCambioRepository;
import com.marz.soporte.repository.SolicitudRepository;
import com.marz.soporte.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialCambioRepository historialRepository;
    public SolicitudService(SolicitudRepository solicitudRepository, UsuarioRepository usuarioRepository,
                            HistorialCambioRepository historialRepository) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialRepository = historialRepository;
    }
    @Transactional
    public SolicitudResponse crear(CrearSolicitudRequest request, String correo) {
        Usuario usuario = usuario(correo);
        Solicitud solicitud = new Solicitud();
        solicitud.setTitulo(request.titulo().trim());
        solicitud.setDescripcion(request.descripcion().trim());
        solicitud.setCategoria(request.categoria());
        solicitud.setEstado(EstadoSolicitud.NUEVO);
        solicitud.setPrioridad(Prioridad.MEDIA);
        solicitud.setFechaCreacion(Instant.now());
        solicitud.setSolicitante(usuario);
        return respuesta(solicitudRepository.save(solicitud));
    }
    @Transactional(readOnly = true)
    public List<SolicitudResponse> mias(String correo) {
        Usuario usuario = usuario(correo);
        return solicitudRepository.findBySolicitanteIdOrderByFechaCreacionDesc(usuario.getId())
                .stream().map(this::respuesta).toList();
    }
    @Transactional(readOnly = true)
    public SolicitudResponse propia(Long id, String correo) {
        Usuario usuario = usuario(correo);
        Solicitud solicitud = solicitudRepository.findById(id)
                .filter(item -> item.getSolicitante().getId().equals(usuario.getId()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));
        return respuesta(solicitud);
    }
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listar() {
        return solicitudRepository.findAllByOrderByFechaCreacionDesc().stream().map(this::respuesta).toList();
    }
    @Transactional
    public SolicitudResponse cambiarPrioridad(Long id, Prioridad nuevaPrioridad, String correoActor) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));
        Usuario actor = usuario(correoActor);
        Prioridad anterior = solicitud.getPrioridad();
        solicitud.setPrioridad(nuevaPrioridad);
        solicitudRepository.save(solicitud);
        HistorialCambio historial = new HistorialCambio();
        historial.setSolicitud(solicitud);
        historial.setActor(actor);
        historial.setFecha(Instant.now());
        historial.setCampo("prioridad");
        historial.setValorAnterior(anterior.name());
        historial.setValorNuevo(nuevaPrioridad.name());
        historialRepository.save(historial);
        return respuesta(solicitud);
    }
    private Usuario usuario(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }
    private SolicitudResponse respuesta(Solicitud solicitud) {
        return new SolicitudResponse(solicitud.getId(), solicitud.getTitulo(), solicitud.getDescripcion(),
                solicitud.getCategoria(), solicitud.getFechaCreacion(), solicitud.getEstado(), solicitud.getPrioridad());
    }
}
