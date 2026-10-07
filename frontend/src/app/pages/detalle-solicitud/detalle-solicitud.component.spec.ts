import { DetalleSolicitudComponent } from './detalle-solicitud.component';
import { Solicitud } from '../../core/models/solicitud.model';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { ComentariosService } from '../../core/services/comentarios.service';
import { AuthService } from '../../core/services/auth.service';
import { of } from 'rxjs';

describe('DetalleSolicitudComponent',()=>{
 const base:Solicitud={id:1,titulo:'Caso',descripcion:'Detalle',categoria:'RED',fechaCreacion:'2026-10-06',estado:'NUEVO',prioridad:'MEDIA'};
 function component(role:'AGENTE'|'SOLICITANTE'|'COORDINADOR'|'AUDITOR'){
  const route={snapshot:{paramMap:{get:()=> '1'}}};const auth={usuario:()=>({rol:role})};
  return new DetalleSolicitudComponent(route as never,{} as never,{} as never,auth as never);
 }
 it('solo ofrece transiciones operativas válidas al agente',()=>{const c=component('AGENTE');c.item.set(base);expect(c.nextState()).toBe('EN_PROCESO');c.item.set({...base,estado:'EN_PROCESO'});expect(c.nextState()).toBe('RESUELTO');c.item.set({...base,estado:'RESUELTO'});expect(c.nextState()).toBeNull();});
 it('muestra cierre y reapertura solo al solicitante con estado RESUELTO',()=>{const requester=component('SOLICITANTE');requester.item.set({...base,estado:'RESUELTO'});expect(requester.canResolveAsRequester()).toBe(true);requester.item.set(base);expect(requester.canResolveAsRequester()).toBe(false);const agent=component('AGENTE');agent.item.set({...base,estado:'RESUELTO'});expect(agent.canResolveAsRequester()).toBe(false);});
 it('mantiene al auditor en modo lectura y vuelve a auditoría',()=>{const auditor=component('AUDITOR');auditor.item.set({...base,estado:'RESUELTO'});expect(auditor.canResolveAsRequester()).toBe(false);expect(auditor.backLink()).toBe('/auditoria');});
 it('no renderiza acciones de escritura para el auditor',()=>{TestBed.configureTestingModule({imports:[DetalleSolicitudComponent],providers:[provideRouter([]),{provide:ActivatedRoute,useValue:{snapshot:{paramMap:{get:()=> '1'}}}},{provide:SolicitudesService,useValue:{detalle:()=>of({...base,estado:'RESUELTO'})}},{provide:ComentariosService,useValue:{listar:()=>of([])}},{provide:AuthService,useValue:{usuario:()=>({rol:'AUDITOR'})}}]});const fixture=TestBed.createComponent(DetalleSolicitudComponent);fixture.detectChanges();const text=fixture.nativeElement.textContent as string;expect(text).not.toContain('Confirmar cierre');expect(text).not.toContain('Publicar comentario');expect(text).not.toContain('Gestión de estado');});
});
