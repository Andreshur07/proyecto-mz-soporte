import { Estado, Prioridad } from './solicitud.model';

export interface Indicadores {
  totalSolicitudes: number;
  porEstado: Record<Estado, number>;
  porPrioridad: Record<Prioridad, number>;
  sinAsignar: number;
}
