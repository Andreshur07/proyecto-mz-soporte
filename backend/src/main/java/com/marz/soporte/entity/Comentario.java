package com.marz.soporte.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comentarios")
public class Comentario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;
    @Column(nullable = false, length = 2000)
    private String contenido;
    @Column(nullable = false, updatable = false)
    private Instant fechaCreacion;
    @PrePersist void asignarFecha() { if (fechaCreacion == null) fechaCreacion = Instant.now(); }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Solicitud getSolicitud(){return solicitud;} public void setSolicitud(Solicitud solicitud){this.solicitud=solicitud;}
    public Usuario getAutor(){return autor;} public void setAutor(Usuario autor){this.autor=autor;}
    public String getContenido(){return contenido;} public void setContenido(String contenido){this.contenido=contenido;}
    public Instant getFechaCreacion(){return fechaCreacion;} public void setFechaCreacion(Instant fechaCreacion){this.fechaCreacion=fechaCreacion;}
}
