package com.marz.soporte.dto;

import com.marz.soporte.entity.CategoriaSolicitud;
import com.marz.soporte.entity.EstadoSolicitud;
import com.marz.soporte.entity.Prioridad;
import java.time.LocalDate;

public record FiltroSolicitud(EstadoSolicitud estado, Prioridad prioridad,
        CategoriaSolicitud categoria, Long agenteId, LocalDate fechaDesde, LocalDate fechaHasta) {
}
