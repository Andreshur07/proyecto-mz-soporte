package com.marz.soporte.dto;
import java.time.Instant;
public record ComentarioResponse(Long id,String contenido,Instant fechaCreacion,AutorComentarioResponse autor){}
