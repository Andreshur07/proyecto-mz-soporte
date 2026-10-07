import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Comentario, CrearComentario } from '../models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class ComentariosService {
  constructor(private http: HttpClient) {}
  listar(solicitudId: number): Observable<Comentario[]> { return this.http.get<Comentario[]>(`${API_URL}/api/solicitudes/${solicitudId}/comentarios`); }
  crear(solicitudId: number, data: CrearComentario): Observable<Comentario> { return this.http.post<Comentario>(`${API_URL}/api/solicitudes/${solicitudId}/comentarios`, data); }
}
