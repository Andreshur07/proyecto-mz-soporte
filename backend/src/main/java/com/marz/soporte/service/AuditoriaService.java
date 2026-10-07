package com.marz.soporte.service;

import com.marz.soporte.audit.HistorialCambio;
import com.marz.soporte.dto.*;
import com.marz.soporte.repository.HistorialCambioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AuditoriaService {
    private final HistorialCambioRepository historiales;
    public AuditoriaService(HistorialCambioRepository historiales) { this.historiales = historiales; }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> consultar(FiltroAuditoria filtro) {
        SolicitudSpecification.validarRango(filtro.fechaDesde(), filtro.fechaHasta());
        Specification<HistorialCambio> spec = Specification.unrestricted();
        if (filtro.solicitudId() != null) spec = spec.and((r,q,cb)->cb.equal(r.get("solicitud").get("id"), filtro.solicitudId()));
        if (filtro.actorId() != null) spec = spec.and((r,q,cb)->cb.equal(r.get("actor").get("id"), filtro.actorId()));
        if (filtro.campo() != null && !filtro.campo().isBlank()) spec = spec.and((r,q,cb)->cb.equal(cb.lower(r.get("campo")), filtro.campo().trim().toLowerCase()));
        if (filtro.fechaDesde() != null) {
            var desde = filtro.fechaDesde().atStartOfDay(ZoneOffset.UTC).toInstant();
            spec = spec.and((r,q,cb)->cb.greaterThanOrEqualTo(r.get("fecha"), desde));
        }
        if (filtro.fechaHasta() != null) {
            var hasta = filtro.fechaHasta().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            spec = spec.and((r,q,cb)->cb.lessThan(r.get("fecha"), hasta));
        }
        return historiales.findAll(spec, Sort.by(Sort.Direction.DESC, "fecha")).stream().map(this::respuesta).toList();
    }

    private AuditoriaResponse respuesta(HistorialCambio h) {
        var u = h.getActor();
        return new AuditoriaResponse(h.getId(), h.getSolicitud().getId(),
                new ActorAuditoriaResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol()),
                h.getFecha(), h.getCampo(), h.getValorAnterior(), h.getValorNuevo());
    }
}
