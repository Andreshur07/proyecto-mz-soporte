package com.marz.soporte.repository;
import com.marz.soporte.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ComentarioRepository extends JpaRepository<Comentario,Long>{
    List<Comentario> findBySolicitudIdOrderByFechaCreacionAsc(Long solicitudId);
}
