package com.marz.soporte.dto;
import com.marz.soporte.entity.CategoriaSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record CrearSolicitudRequest(
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank @Size(max = 2000) String descripcion,
        @NotNull CategoriaSolicitud categoria) {}
