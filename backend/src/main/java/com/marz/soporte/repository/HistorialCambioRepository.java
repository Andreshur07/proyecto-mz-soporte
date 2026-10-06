package com.marz.soporte.repository;

import com.marz.soporte.audit.HistorialCambio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialCambioRepository extends JpaRepository<HistorialCambio, Long> {
    List<HistorialCambio> findBySolicitudIdOrderByFechaDesc(Long solicitudId);
}
