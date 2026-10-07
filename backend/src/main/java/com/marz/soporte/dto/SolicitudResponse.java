package com.marz.soporte.dto;
import com.marz.soporte.entity.*;
import java.time.Instant;
import java.time.LocalDate;
public record SolicitudResponse(Long id,String titulo,String descripcion,CategoriaSolicitud categoria,
        Instant fechaCreacion,EstadoSolicitud estado,Prioridad prioridad,
        String justificacionPrioridadAlta,LocalDate fechaObjetivo,
        UsuarioResumenResponse solicitante,UsuarioResumenResponse agenteAsignado){
    public SolicitudResponse(Long id,String titulo,String descripcion,CategoriaSolicitud categoria,
            Instant fechaCreacion,EstadoSolicitud estado,Prioridad prioridad){
        this(id,titulo,descripcion,categoria,fechaCreacion,estado,prioridad,null,null,null,null);
    }
}
