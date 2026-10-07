package com.marz.soporte.dto;

import com.marz.soporte.entity.EstadoSolicitud;
import com.marz.soporte.entity.Prioridad;
import java.util.Map;

public record IndicadoresResponse(long totalSolicitudes,
        Map<EstadoSolicitud, Long> porEstado, Map<Prioridad, Long> porPrioridad, long sinAsignar) {
}
