import { Rol } from './auth.model';

export interface ActorAuditoria { id: number; nombre: string; correo: string; rol: Rol; }
export interface Auditoria {
  id: number; solicitudId: number; actor: ActorAuditoria; fecha: string;
  campo: string; valorAnterior: string; valorNuevo: string;
}
export interface FiltroAuditoria {
  solicitudId?: number; actorId?: number; campo?: string; fechaDesde?: string; fechaHasta?: string;
}
