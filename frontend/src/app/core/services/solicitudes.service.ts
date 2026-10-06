import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { CrearSolicitud, Prioridad, Solicitud } from '../models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class SolicitudesService {
  constructor(private http: HttpClient) {}
  crear(data: CrearSolicitud): Observable<Solicitud> { return this.http.post<Solicitud>(`${API_URL}/api/solicitudes`, data); }
  mias(): Observable<Solicitud[]> { return this.http.get<Solicitud[]>(`${API_URL}/api/solicitudes/mias`); }
  detalle(id: number): Observable<Solicitud> { return this.http.get<Solicitud>(`${API_URL}/api/solicitudes/${id}`); }
  todas(): Observable<Solicitud[]> { return this.http.get<Solicitud[]>(`${API_URL}/api/solicitudes`); }
  cambiarPrioridad(id: number, prioridad: Prioridad): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${API_URL}/api/solicitudes/${id}/prioridad`, { prioridad });
  }
}
