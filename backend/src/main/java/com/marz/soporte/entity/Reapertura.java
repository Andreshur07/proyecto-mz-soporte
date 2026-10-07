package com.marz.soporte.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reaperturas")
public class Reapertura {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;
    @Column(nullable = false, length = 1000)
    private String motivo;
    @Column(nullable = false, updatable = false)
    private Instant fechaCreacion;
    @PrePersist void asignarFecha(){if(fechaCreacion==null)fechaCreacion=Instant.now();}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Solicitud getSolicitud(){return solicitud;} public void setSolicitud(Solicitud solicitud){this.solicitud=solicitud;}
    public Usuario getSolicitante(){return solicitante;} public void setSolicitante(Usuario solicitante){this.solicitante=solicitante;}
    public String getMotivo(){return motivo;} public void setMotivo(String motivo){this.motivo=motivo;}
    public Instant getFechaCreacion(){return fechaCreacion;} public void setFechaCreacion(Instant fechaCreacion){this.fechaCreacion=fechaCreacion;}
}
