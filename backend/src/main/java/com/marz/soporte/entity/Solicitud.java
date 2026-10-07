package com.marz.soporte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaSolicitud categoria;

    @Column(nullable = false, updatable = false)
    private Instant fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridad prioridad;

    @Column(length = 500)
    private String justificacionPrioridadAlta;

    private LocalDate fechaObjetivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_asignado_id")
    private Usuario agenteAsignado;

    @PrePersist
    void asignarValoresIniciales() {
        if (fechaCreacion == null) fechaCreacion = Instant.now();
        if (estado == null) estado = EstadoSolicitud.NUEVO;
        if (prioridad == null) prioridad = Prioridad.MEDIA;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public CategoriaSolicitud getCategoria() { return categoria; }
    public void setCategoria(CategoriaSolicitud categoria) { this.categoria = categoria; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Instant fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }
    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }
    public String getJustificacionPrioridadAlta() { return justificacionPrioridadAlta; }
    public void setJustificacionPrioridadAlta(String justificacionPrioridadAlta) { this.justificacionPrioridadAlta = justificacionPrioridadAlta; }
    public LocalDate getFechaObjetivo() { return fechaObjetivo; }
    public void setFechaObjetivo(LocalDate fechaObjetivo) { this.fechaObjetivo = fechaObjetivo; }
    public Usuario getAgenteAsignado() { return agenteAsignado; }
    public void setAgenteAsignado(Usuario agenteAsignado) { this.agenteAsignado = agenteAsignado; }
}
