package com.marz.soporte.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CrearComentarioRequest(@NotBlank @Size(max=2000) String contenido){}
