package com.marz.soporte.service;
import com.marz.soporte.audit.HistorialCambio;
import com.marz.soporte.dto.CambiarPrioridadRequest;
import com.marz.soporte.entity.*;
import com.marz.soporte.exception.*;
import com.marz.soporte.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;
import java.time.*;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Sprint2SolicitudServiceTest{
 private SolicitudRepository solicitudes;private UsuarioRepository usuarios;private HistorialCambioRepository historiales;private ReaperturaRepository reaperturas;private SolicitudService service;
 private Usuario solicitante,agente,coordinador;private Solicitud solicitud;
 @BeforeEach void setUp(){solicitudes=mock(SolicitudRepository.class);usuarios=mock(UsuarioRepository.class);historiales=mock(HistorialCambioRepository.class);reaperturas=mock(ReaperturaRepository.class);service=new SolicitudService(solicitudes,usuarios,historiales,reaperturas);solicitante=usuario(1,"sol@x",Rol.SOLICITANTE,true);agente=usuario(2,"age@x",Rol.AGENTE,true);coordinador=usuario(3,"coo@x",Rol.COORDINADOR,true);solicitud=solicitud(10,EstadoSolicitud.NUEVO);when(solicitudes.findById(10L)).thenReturn(Optional.of(solicitud));when(usuarios.findByCorreoIgnoreCase(coordinador.getCorreo())).thenReturn(Optional.of(coordinador));when(usuarios.findByCorreoIgnoreCase(agente.getCorreo())).thenReturn(Optional.of(agente));when(usuarios.findByCorreoIgnoreCase(solicitante.getCorreo())).thenReturn(Optional.of(solicitante));}
 @Test void altaValidaPersisteDatosEHistorial(){var r=service.cambiarPrioridad(10L,new CambiarPrioridadRequest(Prioridad.ALTA,"Bloquea operación",LocalDate.now()),coordinador.getCorreo());assertThat(r.prioridad()).isEqualTo(Prioridad.ALTA);assertThat(r.justificacionPrioridadAlta()).isEqualTo("Bloquea operación");assertThat(r.fechaObjetivo()).isEqualTo(LocalDate.now());verify(historiales).save(any());}
 @Test void altaSinJustificacionRechazada(){assertThatThrownBy(()->service.cambiarPrioridad(10L,new CambiarPrioridadRequest(Prioridad.ALTA," ",LocalDate.now()),coordinador.getCorreo())).isInstanceOf(ReglaNegocioException.class);}
 @Test void altaSinFechaRechazada(){assertThatThrownBy(()->service.cambiarPrioridad(10L,new CambiarPrioridadRequest(Prioridad.ALTA,"Urgente",null),coordinador.getCorreo())).isInstanceOf(ReglaNegocioException.class);}
 @Test void altaConFechaPasadaRechazada(){assertThatThrownBy(()->service.cambiarPrioridad(10L,new CambiarPrioridadRequest(Prioridad.ALTA,"Urgente",LocalDate.now().minusDays(1)),coordinador.getCorreo())).isInstanceOf(ReglaNegocioException.class);}
 @Test void bajaNoExigeDatosYConservaEvidenciaAlta(){solicitud.setPrioridad(Prioridad.ALTA);solicitud.setJustificacionPrioridadAlta("Histórica");solicitud.setFechaObjetivo(LocalDate.now());var r=service.cambiarPrioridad(10L,new CambiarPrioridadRequest(Prioridad.BAJA,null,null),coordinador.getCorreo());assertThat(r.prioridad()).isEqualTo(Prioridad.BAJA);assertThat(r.justificacionPrioridadAlta()).isEqualTo("Histórica");}
 @Test void asignaAgenteActivoYAudita(){when(usuarios.findById(2L)).thenReturn(Optional.of(agente));var r=service.asignar(10L,2L,coordinador.getCorreo());assertThat(r.agenteAsignado().id()).isEqualTo(2);ArgumentCaptor<HistorialCambio> c=ArgumentCaptor.forClass(HistorialCambio.class);verify(historiales).save(c.capture());assertThat(c.getValue().getCampo()).isEqualTo("agenteAsignado");assertThat(c.getValue().getValorAnterior()).isEqualTo("Sin asignar");}
 @Test void reasignacionAuditaAnteriorYNuevo(){Usuario nuevo=usuario(4,"nuevo@x",Rol.AGENTE,true);solicitud.setAgenteAsignado(agente);when(usuarios.findById(4L)).thenReturn(Optional.of(nuevo));service.asignar(10L,4L,coordinador.getCorreo());ArgumentCaptor<HistorialCambio> c=ArgumentCaptor.forClass(HistorialCambio.class);verify(historiales).save(c.capture());assertThat(c.getValue().getValorAnterior()).contains("age@x");assertThat(c.getValue().getValorNuevo()).contains("nuevo@x");}
 @Test void rechazaAsignarRolNoAgente(){when(usuarios.findById(1L)).thenReturn(Optional.of(solicitante));assertThatThrownBy(()->service.asignar(10L,1L,coordinador.getCorreo())).isInstanceOf(ReglaNegocioException.class);}
 @Test void rechazaAgenteInactivo(){Usuario inactivo=usuario(5,"off@x",Rol.AGENTE,false);when(usuarios.findById(5L)).thenReturn(Optional.of(inactivo));assertThatThrownBy(()->service.asignar(10L,5L,coordinador.getCorreo())).isInstanceOf(ReglaNegocioException.class);}
 @Test void nuevoAEnProcesoPermitido(){solicitud.setAgenteAsignado(agente);assertThat(service.cambiarEstado(10L,EstadoSolicitud.EN_PROCESO,agente.getCorreo()).estado()).isEqualTo(EstadoSolicitud.EN_PROCESO);verify(historiales).save(any());}
 @Test void enProcesoAResueltoPermitido(){solicitud.setAgenteAsignado(agente);solicitud.setEstado(EstadoSolicitud.EN_PROCESO);assertThat(service.cambiarEstado(10L,EstadoSolicitud.RESUELTO,agente.getCorreo()).estado()).isEqualTo(EstadoSolicitud.RESUELTO);}
 @Test void reabiertoAEnProcesoPermitido(){solicitud.setAgenteAsignado(agente);solicitud.setEstado(EstadoSolicitud.REABIERTO);assertThat(service.cambiarEstado(10L,EstadoSolicitud.EN_PROCESO,agente.getCorreo()).estado()).isEqualTo(EstadoSolicitud.EN_PROCESO);}
 @Test void nuevoAResueltoRechazado(){solicitud.setAgenteAsignado(agente);assertThatThrownBy(()->service.cambiarEstado(10L,EstadoSolicitud.RESUELTO,agente.getCorreo())).isInstanceOf(ReglaNegocioException.class);}
 @Test void agenteNoAsignadoRechazado(){assertThatThrownBy(()->service.cambiarEstado(10L,EstadoSolicitud.EN_PROCESO,agente.getCorreo())).isInstanceOf(AccessDeniedException.class);}
 @Test void propietarioCierraResuelta(){solicitud.setEstado(EstadoSolicitud.RESUELTO);assertThat(service.confirmarCierre(10L,solicitante.getCorreo()).estado()).isEqualTo(EstadoSolicitud.CERRADO);verify(historiales).save(any());}
 @Test void propietarioReabreYPreservaMotivo(){solicitud.setEstado(EstadoSolicitud.RESUELTO);assertThat(service.reabrir(10L,"Sigue fallando",solicitante.getCorreo()).estado()).isEqualTo(EstadoSolicitud.REABIERTO);ArgumentCaptor<Reapertura> c=ArgumentCaptor.forClass(Reapertura.class);verify(reaperturas).save(c.capture());assertThat(c.getValue().getMotivo()).isEqualTo("Sigue fallando");verify(historiales).save(any());}
 @Test void noCierraEstadoIncorrecto(){assertThatThrownBy(()->service.confirmarCierre(10L,solicitante.getCorreo())).isInstanceOf(ReglaNegocioException.class);}
 @Test void otroSolicitanteNoPuedeCerrar(){Usuario otro=usuario(6,"otro@x",Rol.SOLICITANTE,true);when(usuarios.findByCorreoIgnoreCase(otro.getCorreo())).thenReturn(Optional.of(otro));solicitud.setEstado(EstadoSolicitud.RESUELTO);assertThatThrownBy(()->service.confirmarCierre(10L,otro.getCorreo())).isInstanceOf(RecursoNoEncontradoException.class);}
 private Usuario usuario(long id,String correo,Rol rol,boolean activo){Usuario u=new Usuario();u.setId(id);u.setNombre("U"+id);u.setCorreo(correo);u.setRol(rol);u.setActivo(activo);return u;}
 private Solicitud solicitud(long id,EstadoSolicitud estado){Solicitud s=new Solicitud();s.setId(id);s.setTitulo("T");s.setDescripcion("D");s.setCategoria(CategoriaSolicitud.SOFTWARE);s.setFechaCreacion(Instant.now());s.setEstado(estado);s.setPrioridad(Prioridad.MEDIA);s.setSolicitante(solicitante);return s;}
}
