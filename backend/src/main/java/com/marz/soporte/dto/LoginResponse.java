package com.marz.soporte.dto;
public record LoginResponse(String token, String tipo, UsuarioResponse usuario) {}
