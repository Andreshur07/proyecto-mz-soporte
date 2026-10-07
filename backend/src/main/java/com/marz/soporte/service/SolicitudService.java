package com.marz.soporte.service;

import com.marz.soporte.audit.HistorialCambio;
import com.marz.soporte.dto.*;
import com.marz.soporte.entity.*;
import com.marz.soporte.exception.RecursoNoEncontradoException;
import com.marz.soporte.exception.ReglaNegocioException;
import com.marz.soporte.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class SolicitudService {
    private final SolicitudRepository solicitudes;
    private final UsuarioRepository usuarios;
    private final HistorialCambioRepository historiales;
    private final ReaperturaRepository reaperturas;
    public SolicitudService(SolicitudRepository solicitudes,UsuarioRepository usuarios,
            HistorialCambioRepository historiales,ReaperturaRepository reaperturas){
        this.solicitudes=solicitudes;this.usuarios=usuarios;this.historiales=historiales;this.reaperturas=reaperturas;
    }

    @Transactional
    public SolicitudResponse crear(CrearSolicitudRequest request,String correo){
        Usuario propietario=usuario(correo); Solicitud s=new Solicitud();
        s.setTitulo(request.titulo().trim());s.setDescripcion(request.descripcion().trim());s.setCategoria(request.categoria());
        s.setEstado(EstadoSolicitud.NUEVO);s.setPrioridad(Prioridad.MEDIA);s.setFechaCreacion(Instant.now());s.setSolicitante(propietario);
        return respuesta(solicitudes.save(s));
    }
    @Transactional(readOnly=true)
    public List<SolicitudResponse> mias(String correo){Usuario u=usuario(correo);return solicitudes.findBySolicitanteIdOrderByFechaCreacionDesc(u.getId()).stream().map(this::respuesta).toList();}
    @Transactional(readOnly=true)
    public List<SolicitudResponse> listar(){return solicitudes.findAllByOrderByFechaCreacionDesc().stream().map(this::respuesta).toList();}
    @Transactional(readOnly=true)
    public List<SolicitudResponse> listar(FiltroSolicitud filtro){
        return buscar(filtro).stream().map(this::respuesta).toList();
    }
    @Transactional(readOnly=true)
    public List<Solicitud> buscar(FiltroSolicitud filtro){
        validarAgente(filtro.agenteId());
        return solicitudes.findAll(SolicitudSpecification.conFiltros(filtro), Sort.by(Sort.Direction.DESC,"fechaCreacion"));
    }
    @Transactional(readOnly=true)
    public List<SolicitudResponse> asignadas(String correo){Usuario u=usuario(correo);return solicitudes.findByAgenteAsignadoIdOrderByFechaCreacionDesc(u.getId()).stream().map(this::respuesta).toList();}
    @Transactional(readOnly=true)
    public SolicitudResponse detalle(Long id,String correo){
        Usuario u=usuario(correo);Solicitud s=solicitud(id);
        if(u.getRol()==Rol.COORDINADOR||u.getRol()==Rol.AUDITOR)return respuesta(s);
        if(u.getRol()==Rol.SOLICITANTE&&s.getSolicitante().getId().equals(u.getId()))return respuesta(s);
        if(u.getRol()==Rol.AGENTE&&s.getAgenteAsignado()!=null&&s.getAgenteAsignado().getId().equals(u.getId()))return respuesta(s);
        if(u.getRol()==Rol.SOLICITANTE)throw new RecursoNoEncontradoException("Solicitud no encontrada");
        throw new AccessDeniedException("No tiene acceso a esta solicitud");
    }
    public SolicitudResponse propia(Long id,String correo){return detalle(id,correo);}

    @Transactional
    public SolicitudResponse cambiarPrioridad(Long id,CambiarPrioridadRequest request,String correoActor){
        Solicitud s=solicitud(id);Usuario actor=usuario(correoActor);validarPrioridadAlta(request);
        Prioridad anterior=s.getPrioridad();s.setPrioridad(request.prioridad());
        if(request.prioridad()==Prioridad.ALTA){s.setJustificacionPrioridadAlta(request.justificacion().trim());s.setFechaObjetivo(request.fechaObjetivo());}
        solicitudes.save(s);registrar(s,actor,"prioridad",anterior.name(),request.prioridad().name());return respuesta(s);
    }
    private void validarPrioridadAlta(CambiarPrioridadRequest request){
        if(request.prioridad()!=Prioridad.ALTA)return;
        if(request.justificacion()==null||request.justificacion().isBlank())throw new ReglaNegocioException("La justificación es obligatoria para prioridad ALTA");
        if(request.fechaObjetivo()==null)throw new ReglaNegocioException("La fecha objetivo es obligatoria para prioridad ALTA");
        if(request.fechaObjetivo().isBefore(LocalDate.now()))throw new ReglaNegocioException("La fecha objetivo no puede ser anterior a la fecha actual");
    }
    @Transactional
    public SolicitudResponse asignar(Long id,Long agenteId,String correoActor){
        Solicitud s=solicitud(id);Usuario actor=usuario(correoActor);Usuario agente=usuarios.findById(agenteId).orElseThrow(()->new RecursoNoEncontradoException("Usuario no encontrado"));
        if(agente.getRol()!=Rol.AGENTE)throw new ReglaNegocioException("El usuario seleccionado no tiene rol AGENTE");
        if(!agente.isActivo())throw new ReglaNegocioException("El agente seleccionado está inactivo");
        String anterior=s.getAgenteAsignado()==null?"Sin asignar":identidad(s.getAgenteAsignado());s.setAgenteAsignado(agente);solicitudes.save(s);
        registrar(s,actor,"agenteAsignado",anterior,identidad(agente));return respuesta(s);
    }
    @Transactional
    public SolicitudResponse cambiarEstado(Long id,EstadoSolicitud nuevo,String correoAgente){
        Solicitud s=solicitud(id);Usuario agente=usuario(correoAgente);validarAgenteAsignado(s,agente);
        EstadoSolicitud anterior=s.getEstado();boolean valida=(anterior==EstadoSolicitud.NUEVO||anterior==EstadoSolicitud.REABIERTO)&&nuevo==EstadoSolicitud.EN_PROCESO
                ||anterior==EstadoSolicitud.EN_PROCESO&&nuevo==EstadoSolicitud.RESUELTO;
        if(!valida)throw new ReglaNegocioException("Transición de estado no permitida: "+anterior+" -> "+nuevo);
        s.setEstado(nuevo);solicitudes.save(s);registrar(s,agente,"estado",anterior.name(),nuevo.name());return respuesta(s);
    }
    @Transactional
    public SolicitudResponse confirmarCierre(Long id,String correo){
        Solicitud s=solicitudPropia(id,correo);if(s.getEstado()!=EstadoSolicitud.RESUELTO)throw new ReglaNegocioException("Solo se puede cerrar una solicitud RESUELTA");
        Usuario actor=usuario(correo);s.setEstado(EstadoSolicitud.CERRADO);solicitudes.save(s);registrar(s,actor,"estado","RESUELTO","CERRADO");return respuesta(s);
    }
    @Transactional
    public SolicitudResponse reabrir(Long id,String motivo,String correo){
        Solicitud s=solicitudPropia(id,correo);if(s.getEstado()!=EstadoSolicitud.RESUELTO)throw new ReglaNegocioException("Solo se puede reabrir una solicitud RESUELTA");
        Usuario actor=usuario(correo);Reapertura r=new Reapertura();r.setSolicitud(s);r.setSolicitante(actor);r.setMotivo(motivo.trim());r.setFechaCreacion(Instant.now());
        s.setEstado(EstadoSolicitud.REABIERTO);reaperturas.save(r);solicitudes.save(s);registrar(s,actor,"estado","RESUELTO","REABIERTO");return respuesta(s);
    }
    private Solicitud solicitudPropia(Long id,String correo){Usuario u=usuario(correo);return solicitudes.findById(id).filter(s->s.getSolicitante().getId().equals(u.getId())).orElseThrow(()->new RecursoNoEncontradoException("Solicitud no encontrada"));}
    private void validarAgenteAsignado(Solicitud s,Usuario agente){if(s.getAgenteAsignado()==null||!s.getAgenteAsignado().getId().equals(agente.getId()))throw new AccessDeniedException("Solo el agente asignado puede realizar esta operación");}
    private Solicitud solicitud(Long id){return solicitudes.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Solicitud no encontrada"));}
    private void validarAgente(Long agenteId){
        if(agenteId==null)return;
        Usuario agente=usuarios.findById(agenteId).orElseThrow(()->new RecursoNoEncontradoException("Agente no encontrado"));
        if(agente.getRol()!=Rol.AGENTE)throw new ReglaNegocioException("El identificador no corresponde a un agente");
    }
    private Usuario usuario(String correo){return usuarios.findByCorreoIgnoreCase(correo).orElseThrow(()->new RecursoNoEncontradoException("Usuario no encontrado"));}
    private void registrar(Solicitud s,Usuario actor,String campo,String anterior,String nuevo){HistorialCambio h=new HistorialCambio();h.setSolicitud(s);h.setActor(actor);h.setFecha(Instant.now());h.setCampo(campo);h.setValorAnterior(anterior);h.setValorNuevo(nuevo);historiales.save(h);}
    private String identidad(Usuario u){return u.getId()+" - "+u.getCorreo();}
    private UsuarioResumenResponse usuarioResumen(Usuario u){return u==null?null:new UsuarioResumenResponse(u.getId(),u.getNombre(),u.getCorreo(),u.getRol());}
    private SolicitudResponse respuesta(Solicitud s){return new SolicitudResponse(s.getId(),s.getTitulo(),s.getDescripcion(),s.getCategoria(),s.getFechaCreacion(),s.getEstado(),s.getPrioridad(),s.getJustificacionPrioridadAlta(),s.getFechaObjetivo(),usuarioResumen(s.getSolicitante()),usuarioResumen(s.getAgenteAsignado()));}
}
