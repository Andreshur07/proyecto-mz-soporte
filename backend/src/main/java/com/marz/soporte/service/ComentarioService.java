package com.marz.soporte.service;
import com.marz.soporte.dto.*;
import com.marz.soporte.entity.*;
import com.marz.soporte.exception.RecursoNoEncontradoException;
import com.marz.soporte.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
@Service
public class ComentarioService{
 private final ComentarioRepository comentarios;private final SolicitudRepository solicitudes;private final UsuarioRepository usuarios;
 public ComentarioService(ComentarioRepository comentarios,SolicitudRepository solicitudes,UsuarioRepository usuarios){this.comentarios=comentarios;this.solicitudes=solicitudes;this.usuarios=usuarios;}
 @Transactional public ComentarioResponse crear(Long solicitudId,String contenido,String correo){Solicitud s=solicitud(solicitudId);Usuario autor=usuario(correo);if(s.getAgenteAsignado()==null||!s.getAgenteAsignado().getId().equals(autor.getId()))throw new AccessDeniedException("Solo el agente asignado puede comentar");Comentario c=new Comentario();c.setSolicitud(s);c.setAutor(autor);c.setContenido(contenido.trim());c.setFechaCreacion(Instant.now());return respuesta(comentarios.save(c));}
 @Transactional(readOnly=true) public List<ComentarioResponse> listar(Long solicitudId,String correo){Solicitud s=solicitud(solicitudId);Usuario u=usuario(correo);boolean permitido=u.getRol()==Rol.COORDINADOR||u.getRol()==Rol.SOLICITANTE&&s.getSolicitante().getId().equals(u.getId())||u.getRol()==Rol.AGENTE&&s.getAgenteAsignado()!=null&&s.getAgenteAsignado().getId().equals(u.getId());if(!permitido)throw new AccessDeniedException("No tiene acceso a los comentarios");return comentarios.findBySolicitudIdOrderByFechaCreacionAsc(solicitudId).stream().map(this::respuesta).toList();}
 private Solicitud solicitud(Long id){return solicitudes.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Solicitud no encontrada"));}private Usuario usuario(String correo){return usuarios.findByCorreoIgnoreCase(correo).orElseThrow(()->new RecursoNoEncontradoException("Usuario no encontrado"));}
 private ComentarioResponse respuesta(Comentario c){return new ComentarioResponse(c.getId(),c.getContenido(),c.getFechaCreacion(),new AutorComentarioResponse(c.getAutor().getId(),c.getAutor().getNombre(),c.getAutor().getRol()));}
}
