import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { AsignacionSolicitud, CambioEstado, CambioPrioridad, CrearSolicitud, ReaperturaSolicitud, Solicitud, UsuarioResumen } from '../models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class SolicitudesService {
  constructor(private http: HttpClient) {}
  crear(data: CrearSolicitud): Observable<Solicitud> { return this.http.post<Solicitud>(`${API_URL}/api/solicitudes`, data); }
  mias(): Observable<Solicitud[]> { return this.http.get<Solicitud[]>(`${API_URL}/api/solicitudes/mias`); }
  asignadas(): Observable<Solicitud[]> { return this.http.get<Solicitud[]>(`${API_URL}/api/solicitudes/asignadas`); }
  detalle(id: number): Observable<Solicitud> { return this.http.get<Solicitud>(`${API_URL}/api/solicitudes/${id}`); }
  todas(): Observable<Solicitud[]> { return this.http.get<Solicitud[]>(`${API_URL}/api/solicitudes`); }
  agentes(): Observable<UsuarioResumen[]> { return this.http.get<UsuarioResumen[]>(`${API_URL}/api/usuarios/agentes`); }
  cambiarPrioridad(id: number, data: CambioPrioridad): Observable<Solicitud> { return this.http.patch<Solicitud>(`${API_URL}/api/solicitudes/${id}/prioridad`, data); }
  asignar(id: number, data: AsignacionSolicitud): Observable<Solicitud> { return this.http.patch<Solicitud>(`${API_URL}/api/solicitudes/${id}/asignacion`, data); }
  cambiarEstado(id: number, data: CambioEstado): Observable<Solicitud> { return this.http.patch<Solicitud>(`${API_URL}/api/solicitudes/${id}/estado`, data); }
  confirmarCierre(id: number): Observable<Solicitud> { return this.http.patch<Solicitud>(`${API_URL}/api/solicitudes/${id}/confirmar-cierre`, {}); }
  reabrir(id: number, data: ReaperturaSolicitud): Observable<Solicitud> { return this.http.patch<Solicitud>(`${API_URL}/api/solicitudes/${id}/reabrir`, data); }
}
