import { of } from 'rxjs';
import { AuditoriaService } from '../../core/services/auditoria.service';
import { AuditoriaComponent } from './auditoria.component';

describe('AuditoriaComponent',()=>{
 it('no consulta cuando el rango es inválido',()=>{const consultar=vi.fn().mockReturnValue(of([]));const component=new AuditoriaComponent({consultar} as unknown as AuditoriaService);component.filters.fechaDesde='2026-12-31';component.filters.fechaHasta='2026-01-01';component.applyFilters();expect(consultar).not.toHaveBeenCalled();expect(component.message()).toContain('fecha desde');});
 it('limpia filtros y consulta nuevamente',()=>{const consultar=vi.fn().mockReturnValue(of([]));const component=new AuditoriaComponent({consultar} as unknown as AuditoriaService);component.filters.campo='estado';component.clearFilters();expect(consultar).toHaveBeenCalledWith({});expect(component.filters.campo).toBe('');});
});
