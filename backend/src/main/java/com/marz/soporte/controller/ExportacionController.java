package com.marz.soporte.controller;

import com.marz.soporte.dto.FiltroSolicitud;
import com.marz.soporte.entity.*;
import com.marz.soporte.service.CsvService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/solicitudes/exportar")
public class ExportacionController {
    private final CsvService service;
    public ExportacionController(CsvService service) { this.service = service; }
    @GetMapping @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<byte[]> exportar(@RequestParam(required=false) EstadoSolicitud estado,
            @RequestParam(required=false) Prioridad prioridad, @RequestParam(required=false) CategoriaSolicitud categoria,
            @RequestParam(required=false) Long agenteId, @RequestParam(required=false) LocalDate fechaDesde,
            @RequestParam(required=false) LocalDate fechaHasta) {
        var contenido = service.exportar(new FiltroSolicitud(estado, prioridad, categoria, agenteId, fechaDesde, fechaHasta));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"solicitudes.csv\"").body(contenido);
    }
}
