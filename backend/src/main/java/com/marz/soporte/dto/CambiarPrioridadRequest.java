package com.marz.soporte.dto;
import com.marz.soporte.entity.Prioridad;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
public record CambiarPrioridadRequest(@NotNull Prioridad prioridad,
        @Size(max=500) String justificacion, LocalDate fechaObjetivo) {}
