import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { API_URL } from '../config/api.config';
import { IndicadoresService } from './indicadores.service';
import { AuditoriaService } from './auditoria.service';

describe('Servicios Sprint 3',()=>{
 let http:HttpTestingController;
 beforeEach(()=>{TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting()]});http=TestBed.inject(HttpTestingController);});afterEach(()=>http.verify());
 it('consulta los indicadores',()=>{TestBed.inject(IndicadoresService).obtener().subscribe();const req=http.expectOne(`${API_URL}/api/indicadores`);expect(req.request.method).toBe('GET');req.flush({totalSolicitudes:0,porEstado:{NUEVO:0,EN_PROCESO:0,RESUELTO:0,CERRADO:0,REABIERTO:0},porPrioridad:{BAJA:0,MEDIA:0,ALTA:0},sinAsignar:0});});
 it('genera filtros de auditoría y omite vacíos',()=>{TestBed.inject(AuditoriaService).consultar({solicitudId:2,actorId:3,campo:'',fechaDesde:'2026-01-01'}).subscribe();const req=http.expectOne(r=>r.url===`${API_URL}/api/auditoria`);expect(req.request.params.get('solicitudId')).toBe('2');expect(req.request.params.get('actorId')).toBe('3');expect(req.request.params.get('fechaDesde')).toBe('2026-01-01');expect(req.request.params.has('campo')).toBe(false);req.flush([]);});
});
