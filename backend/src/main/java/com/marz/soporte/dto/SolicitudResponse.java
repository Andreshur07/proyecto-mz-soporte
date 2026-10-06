package com.marz.soporte.dto;
import com.marz.soporte.entity.CategoriaSolicitud;
import com.marz.soporte.entity.EstadoSolicitud;
import com.marz.soporte.entity.Prioridad;
import java.time.Instant;
public record SolicitudResponse(Long id, String titulo, String descripcion,
        CategoriaSolicitud categoria, Instant fechaCreacion,
        EstadoSolicitud estado, Prioridad prioridad) {}
