export type Categoria = 'HARDWARE' | 'SOFTWARE' | 'ACCESO' | 'RED' | 'OTRO';
export type Prioridad = 'BAJA' | 'MEDIA' | 'ALTA';
export type Estado = 'NUEVO';
export interface Solicitud { id: number; titulo: string; descripcion: string; categoria: Categoria; fechaCreacion: string; estado: Estado; prioridad: Prioridad; }
export interface CrearSolicitud { titulo: string; descripcion: string; categoria: Categoria; }
