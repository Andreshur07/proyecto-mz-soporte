import { of } from 'rxjs';
import { Solicitud } from '../../core/models/solicitud.model';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { SolicitudesCoordinadorComponent } from './solicitudes-coordinador.component';
import { vi } from 'vitest';

describe('SolicitudesCoordinadorComponent',()=>{
 const solicitud:Solicitud={id:1,titulo:'Caso',descripcion:'Detalle',categoria:'RED',fechaCreacion:'2026-10-06',estado:'NUEVO',prioridad:'MEDIA'};
 it('exige justificación y fecha antes de enviar prioridad ALTA',()=>{const api={cambiarPrioridad:vi.fn()} as unknown as SolicitudesService;const component=new SolicitudesCoordinadorComponent(api);component.highTarget.set(solicitud);component.saveHigh();expect(component.highSubmitted()).toBe(true);expect(api.cambiarPrioridad).not.toHaveBeenCalled();});
 it('asigna un agente y actualiza la fila confirmada',()=>{const confirmada={...solicitud,agenteAsignado:{id:2,nombre:'Ana'}};const assign=vi.fn().mockReturnValue(of(confirmada));const api={asignar:assign} as unknown as SolicitudesService;const component=new SolicitudesCoordinadorComponent(api);component.items.set([solicitud]);component.assign(solicitud,2);expect(assign).toHaveBeenCalledWith(1,{agenteId:2});expect(component.items()[0].agenteAsignado?.nombre).toBe('Ana');});
});
