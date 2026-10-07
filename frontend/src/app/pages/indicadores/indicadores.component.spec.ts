import { of } from 'rxjs';
import { IndicadoresService } from '../../core/services/indicadores.service';
import { IndicadoresComponent } from './indicadores.component';

describe('IndicadoresComponent',()=>{
 it('representa correctamente valores cero',()=>{const api={obtener:()=>of({totalSolicitudes:0,porEstado:{NUEVO:0,EN_PROCESO:0,RESUELTO:0,CERRADO:0,REABIERTO:0},porPrioridad:{BAJA:0,MEDIA:0,ALTA:0},sinAsignar:0})} as IndicadoresService;const component=new IndicadoresComponent(api);component.ngOnInit();expect(component.cards(component.data()!)).toHaveLength(7);expect(component.cards(component.data()!).every(x=>x.value===0)).toBe(true);expect(component.percent(0,0)).toBe(0);});
});
