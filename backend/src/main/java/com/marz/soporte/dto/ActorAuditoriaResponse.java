package com.marz.soporte.dto;

import com.marz.soporte.entity.Rol;

public record ActorAuditoriaResponse(Long id, String nombre, String correo, Rol rol) {
}
