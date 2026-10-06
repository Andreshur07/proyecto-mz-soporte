import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { SolicitudesService } from './solicitudes.service';
import { API_URL } from '../config/api.config';
import { Solicitud } from '../models/solicitud.model';

describe('SolicitudesService', () => {
 let service:SolicitudesService;let http:HttpTestingController;
 const solicitud:Solicitud={id:7,titulo:'Error de acceso',descripcion:'No puedo ingresar',categoria:'ACCESO',fechaCreacion:'2026-10-06T10:00:00Z',estado:'NUEVO',prioridad:'MEDIA'};
 beforeEach(()=>{TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting()]});service=TestBed.inject(SolicitudesService);http=TestBed.inject(HttpTestingController);});afterEach(()=>http.verify());
 it('crea una solicitud enviando solo los campos permitidos',()=>{const body={titulo:'Error de acceso',descripcion:'No puedo ingresar',categoria:'ACCESO' as const};let result:Solicitud|undefined;service.crear(body).subscribe(x=>result=x);const req=http.expectOne(`${API_URL}/api/solicitudes`);expect(req.request.method).toBe('POST');expect(req.request.body).toEqual(body);req.flush(solicitud);expect(result).toEqual(solicitud);});
 it('carga Mis solicitudes',()=>{let result:Solicitud[]=[];service.mias().subscribe(x=>result=x);const req=http.expectOne(`${API_URL}/api/solicitudes/mias`);expect(req.request.method).toBe('GET');req.flush([solicitud]);expect(result).toEqual([solicitud]);});
 it('cambia la prioridad para el coordinador',()=>{service.cambiarPrioridad(7,'ALTA').subscribe();const req=http.expectOne(`${API_URL}/api/solicitudes/7/prioridad`);expect(req.request.method).toBe('PATCH');expect(req.request.body).toEqual({prioridad:'ALTA'});req.flush({...solicitud,prioridad:'ALTA'});});
});
