import { Rol } from './auth.model';

export type Categoria = 'HARDWARE' | 'SOFTWARE' | 'ACCESO' | 'RED' | 'OTRO';
export type Prioridad = 'BAJA' | 'MEDIA' | 'ALTA';
export type Estado = 'NUEVO' | 'EN_PROCESO' | 'RESUELTO' | 'CERRADO' | 'REABIERTO';

export interface UsuarioResumen { id: number; nombre: string; correo?: string; rol?: Rol; }
export interface Solicitud {
  id: number; titulo: string; descripcion: string; categoria: Categoria; fechaCreacion: string;
  estado: Estado; prioridad: Prioridad; justificacionPrioridadAlta?: string | null;
  fechaObjetivo?: string | null; solicitante?: UsuarioResumen | null; agenteAsignado?: UsuarioResumen | null;
}
export interface CrearSolicitud { titulo: string; descripcion: string; categoria: Categoria; }
export interface CambioPrioridad { prioridad: Prioridad; justificacion?: string; fechaObjetivo?: string; }
export interface AsignacionSolicitud { agenteId: number; }
export interface CambioEstado { estado: Estado; }
export interface ReaperturaSolicitud { motivo: string; }
export interface Comentario {
  id: number; contenido: string; fechaCreacion: string;
  autor: { id: number; nombre: string; rol?: Rol };
}
export interface CrearComentario { contenido: string; }
