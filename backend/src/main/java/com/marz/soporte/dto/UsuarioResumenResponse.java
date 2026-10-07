package com.marz.soporte.dto;
import com.marz.soporte.entity.Rol;
public record UsuarioResumenResponse(Long id,String nombre,String correo,Rol rol){}
