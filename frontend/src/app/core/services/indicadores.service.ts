import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Indicadores } from '../models/indicadores.model';

@Injectable({providedIn:'root'})
export class IndicadoresService {
  constructor(private http:HttpClient) {}
  obtener():Observable<Indicadores>{return this.http.get<Indicadores>(`${API_URL}/api/indicadores`);}
}
