import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';
import { Auditoria, FiltroAuditoria } from '../models/auditoria.model';

@Injectable({providedIn:'root'})
export class AuditoriaService {
  constructor(private http:HttpClient) {}
  consultar(filtros:FiltroAuditoria={}):Observable<Auditoria[]>{return this.http.get<Auditoria[]>(`${API_URL}/api/auditoria`,{params:this.params(filtros)});}
  private params(filtros:FiltroAuditoria):HttpParams{let params=new HttpParams();for(const [key,value] of Object.entries(filtros))if(value!==undefined&&value!==null&&value!=='')params=params.set(key,String(value));return params;}
}
