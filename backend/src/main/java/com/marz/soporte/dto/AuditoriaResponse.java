package com.marz.soporte.dto;

import java.time.Instant;

public record AuditoriaResponse(Long id, Long solicitudId, ActorAuditoriaResponse actor,
        Instant fecha, String campo, String valorAnterior, String valorNuevo) {
}
