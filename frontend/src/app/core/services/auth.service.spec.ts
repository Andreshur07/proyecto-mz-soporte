import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService, SESSION_KEY } from './auth.service';
import { API_URL } from '../config/api.config';
import { Sesion } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService; let http: HttpTestingController;
  const session: Sesion = { token:'jwt-prueba', tipo:'Bearer', usuario:{id:1,nombre:'Ana Solicitante',correo:'ana@marz.local',rol:'SOLICITANTE'} };
  beforeEach(() => { localStorage.clear(); TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting(),provideRouter([])]}); service=TestBed.inject(AuthService); http=TestBed.inject(HttpTestingController); });
  afterEach(() => http.verify());
  it('inicia sesión válidamente y almacena la sesión', () => {
    let result: Sesion|undefined; service.login({correo:'ana@marz.local',password:'clave'}).subscribe(x=>result=x);
    const req=http.expectOne(`${API_URL}/api/auth/login`); expect(req.request.method).toBe('POST'); req.flush(session);
    expect(result).toEqual(session); expect(JSON.parse(localStorage.getItem(SESSION_KEY)!)).toEqual(session); expect(service.autenticado()).toBe(true);
  });
  it('propaga el error de un login inválido y no almacena sesión', () => {
    let status=0; service.login({correo:'ana@marz.local',password:'mala'}).subscribe({error:e=>status=e.status});
    http.expectOne(`${API_URL}/api/auth/login`).flush({message:'Credenciales inválidas'},{status:401,statusText:'Unauthorized'});
    expect(status).toBe(401); expect(localStorage.getItem(SESSION_KEY)).toBeNull();
  });
});
