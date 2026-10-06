package com.marz.soporte.dto;
import com.marz.soporte.entity.Rol;
public record UsuarioResponse(Long id, String nombre, String correo, Rol rol) {}
