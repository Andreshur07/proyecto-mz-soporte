package com.marz.soporte.dto;

import java.time.LocalDate;

public record FiltroAuditoria(Long solicitudId, Long actorId, String campo,
        LocalDate fechaDesde, LocalDate fechaHasta) {
}
