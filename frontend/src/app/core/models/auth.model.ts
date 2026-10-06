export type Rol = 'SOLICITANTE' | 'AGENTE' | 'COORDINADOR' | 'AUDITOR';
export interface Usuario { id: number; nombre: string; correo: string; rol: Rol; }
export interface Sesion { token: string; tipo: string; usuario: Usuario; }
export interface LoginRequest { correo: string; password: string; }
