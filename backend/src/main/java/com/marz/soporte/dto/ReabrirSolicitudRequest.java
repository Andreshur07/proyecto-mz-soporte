package com.marz.soporte.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record ReabrirSolicitudRequest(@NotBlank @Size(max=1000) String motivo){}
