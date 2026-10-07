package com.marz.soporte.service;
import com.marz.soporte.dto.UsuarioResumenResponse;
import com.marz.soporte.entity.Rol;
import com.marz.soporte.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class UsuarioService{
 private final UsuarioRepository usuarios;public UsuarioService(UsuarioRepository usuarios){this.usuarios=usuarios;}
 @Transactional(readOnly=true) public List<UsuarioResumenResponse> agentesActivos(){return usuarios.findByRolAndActivoTrueOrderByNombreAsc(Rol.AGENTE).stream().map(u->new UsuarioResumenResponse(u.getId(),u.getNombre(),u.getCorreo(),u.getRol())).toList();}
}
