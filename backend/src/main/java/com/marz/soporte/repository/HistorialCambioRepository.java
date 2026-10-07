package com.marz.soporte.repository;

import com.marz.soporte.audit.HistorialCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface HistorialCambioRepository extends JpaRepository<HistorialCambio, Long>, JpaSpecificationExecutor<HistorialCambio> {
    List<HistorialCambio> findBySolicitudIdOrderByFechaDesc(Long solicitudId);
}
