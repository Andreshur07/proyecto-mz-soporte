package com.marz.soporte.repository;

import com.marz.soporte.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findBySolicitanteIdOrderByFechaCreacionDesc(Long solicitanteId);
    List<Solicitud> findAllByOrderByFechaCreacionDesc();
    List<Solicitud> findByAgenteAsignadoIdOrderByFechaCreacionDesc(Long agenteId);
}
