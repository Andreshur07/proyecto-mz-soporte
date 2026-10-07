import { DetalleSolicitudComponent } from './detalle-solicitud.component';
import { Solicitud } from '../../core/models/solicitud.model';

describe('DetalleSolicitudComponent',()=>{
 const base:Solicitud={id:1,titulo:'Caso',descripcion:'Detalle',categoria:'RED',fechaCreacion:'2026-10-06',estado:'NUEVO',prioridad:'MEDIA'};
 function component(role:'AGENTE'|'SOLICITANTE'|'COORDINADOR'){
  const route={snapshot:{paramMap:{get:()=> '1'}}};const auth={usuario:()=>({rol:role})};
  return new DetalleSolicitudComponent(route as never,{} as never,{} as never,auth as never);
 }
 it('solo ofrece transiciones operativas válidas al agente',()=>{const c=component('AGENTE');c.item.set(base);expect(c.nextState()).toBe('EN_PROCESO');c.item.set({...base,estado:'EN_PROCESO'});expect(c.nextState()).toBe('RESUELTO');c.item.set({...base,estado:'RESUELTO'});expect(c.nextState()).toBeNull();});
 it('muestra cierre y reapertura solo al solicitante con estado RESUELTO',()=>{const requester=component('SOLICITANTE');requester.item.set({...base,estado:'RESUELTO'});expect(requester.canResolveAsRequester()).toBe(true);requester.item.set(base);expect(requester.canResolveAsRequester()).toBe(false);const agent=component('AGENTE');agent.item.set({...base,estado:'RESUELTO'});expect(agent.canResolveAsRequester()).toBe(false);});
});
