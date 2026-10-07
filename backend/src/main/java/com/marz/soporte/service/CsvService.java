package com.marz.soporte.service;

import com.marz.soporte.dto.FiltroSolicitud;
import com.marz.soporte.entity.Solicitud;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CsvService {
    private static final List<String> CABECERAS = List.of("ID","Titulo","Descripcion","Categoria","FechaCreacion",
            "Estado","Prioridad","Solicitante","SolicitanteCorreo","AgenteAsignado","AgenteCorreo",
            "JustificacionPrioridadAlta","FechaObjetivo");
    private final SolicitudService solicitudes;
    public CsvService(SolicitudService solicitudes) { this.solicitudes = solicitudes; }

    @Transactional(readOnly = true)
    public byte[] exportar(FiltroSolicitud filtro) {
        var csv = new StringBuilder("\uFEFF").append(String.join(",", CABECERAS)).append("\r\n");
        for (var s : solicitudes.buscar(filtro)) {
            var agente = s.getAgenteAsignado();
            agregarFila(csv, List.of(valor(s.getId()), valor(s.getTitulo()), valor(s.getDescripcion()), valor(s.getCategoria()),
                    valor(s.getFechaCreacion()), valor(s.getEstado()), valor(s.getPrioridad()), valor(s.getSolicitante().getNombre()),
                    valor(s.getSolicitante().getCorreo()), valor(agente == null ? null : agente.getNombre()),
                    valor(agente == null ? null : agente.getCorreo()), valor(s.getJustificacionPrioridadAlta()), valor(s.getFechaObjetivo())));
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
    private void agregarFila(StringBuilder csv, List<String> valores) { csv.append(String.join(",", valores)).append("\r\n"); }
    private String valor(Object valor) {
        if (valor == null) return "";
        String texto = valor.toString();
        if (texto.indexOf(',') >= 0 || texto.indexOf('"') >= 0 || texto.indexOf('\n') >= 0 || texto.indexOf('\r') >= 0)
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        return texto;
    }
}
