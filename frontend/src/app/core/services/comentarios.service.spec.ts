import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { API_URL } from '../config/api.config';
import { ComentariosService } from './comentarios.service';

describe('ComentariosService',()=>{
 let service:ComentariosService;let http:HttpTestingController;
 beforeEach(()=>{TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting()]});service=TestBed.inject(ComentariosService);http=TestBed.inject(HttpTestingController);});afterEach(()=>http.verify());
 it('lista comentarios conservando el orden del backend',()=>{let ids:number[]=[];service.listar(4).subscribe(xs=>ids=xs.map(x=>x.id));const req=http.expectOne(`${API_URL}/api/solicitudes/4/comentarios`);expect(req.request.method).toBe('GET');req.flush([{id:1,contenido:'A',fechaCreacion:'2026-01-01',autor:{id:2,nombre:'Ana'}},{id:2,contenido:'B',fechaCreacion:'2026-01-02',autor:{id:2,nombre:'Ana'}}]);expect(ids).toEqual([1,2]);});
 it('crea un comentario con el DTO esperado',()=>{service.crear(4,{contenido:'Avance'}).subscribe();const req=http.expectOne(`${API_URL}/api/solicitudes/4/comentarios`);expect(req.request.method).toBe('POST');expect(req.request.body).toEqual({contenido:'Avance'});req.flush({id:1,contenido:'Avance',fechaCreacion:'2026-01-01',autor:{id:2,nombre:'Ana'}});});
});
