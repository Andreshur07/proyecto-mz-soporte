package com.marz.soporte.service;

import com.marz.soporte.dto.IndicadoresResponse;
import com.marz.soporte.entity.EstadoSolicitud;
import com.marz.soporte.entity.Prioridad;
import com.marz.soporte.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.EnumMap;

@Service
public class IndicadorService {
    private final SolicitudRepository solicitudes;
    public IndicadorService(SolicitudRepository solicitudes) { this.solicitudes = solicitudes; }

    @Transactional(readOnly = true)
    public IndicadoresResponse obtener() {
        var estados = new EnumMap<EstadoSolicitud, Long>(EstadoSolicitud.class);
        var prioridades = new EnumMap<Prioridad, Long>(Prioridad.class);
        for (var estado : EstadoSolicitud.values()) estados.put(estado, 0L);
        for (var prioridad : Prioridad.values()) prioridades.put(prioridad, 0L);
        long sinAsignar = 0;
        var todas = solicitudes.findAll();
        for (var solicitud : todas) {
            estados.compute(solicitud.getEstado(), (k, v) -> v + 1);
            prioridades.compute(solicitud.getPrioridad(), (k, v) -> v + 1);
            if (solicitud.getAgenteAsignado() == null) sinAsignar++;
        }
        return new IndicadoresResponse(todas.size(), estados, prioridades, sinAsignar);
    }
}
