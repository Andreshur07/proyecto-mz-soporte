package com.marz.soporte.dto;
import com.marz.soporte.entity.Prioridad;
import jakarta.validation.constraints.NotNull;
public record CambiarPrioridadRequest(@NotNull Prioridad prioridad) {}
