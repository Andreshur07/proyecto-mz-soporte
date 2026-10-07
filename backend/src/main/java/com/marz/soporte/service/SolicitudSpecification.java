package com.marz.soporte.service;

import com.marz.soporte.dto.FiltroSolicitud;
import com.marz.soporte.entity.Solicitud;
import com.marz.soporte.exception.ReglaNegocioException;
import org.springframework.data.jpa.domain.Specification;
import java.time.ZoneOffset;

final class SolicitudSpecification {
    private SolicitudSpecification() { }

    static Specification<Solicitud> conFiltros(FiltroSolicitud filtro) {
        validarRango(filtro.fechaDesde(), filtro.fechaHasta());
        Specification<Solicitud> spec = Specification.unrestricted();
        if (filtro.estado() != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("estado"), filtro.estado()));
        if (filtro.prioridad() != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("prioridad"), filtro.prioridad()));
        if (filtro.categoria() != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("categoria"), filtro.categoria()));
        if (filtro.agenteId() != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("agenteAsignado").get("id"), filtro.agenteId()));
        if (filtro.fechaDesde() != null) {
            var desde = filtro.fechaDesde().atStartOfDay(ZoneOffset.UTC).toInstant();
            spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("fechaCreacion"), desde));
        }
        if (filtro.fechaHasta() != null) {
            var limiteExclusivo = filtro.fechaHasta().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            spec = spec.and((r, q, cb) -> cb.lessThan(r.get("fechaCreacion"), limiteExclusivo));
        }
        return spec;
    }

    static void validarRango(java.time.LocalDate desde, java.time.LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta))
            throw new ReglaNegocioException("fechaDesde no puede ser posterior a fechaHasta");
    }
}
