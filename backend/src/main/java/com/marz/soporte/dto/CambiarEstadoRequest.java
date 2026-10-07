package com.marz.soporte.dto;
import com.marz.soporte.entity.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;
public record CambiarEstadoRequest(@NotNull EstadoSolicitud estado){}
