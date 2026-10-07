package com.marz.soporte.dto;
import jakarta.validation.constraints.NotNull;
public record AsignarSolicitudRequest(@NotNull Long agenteId){}
