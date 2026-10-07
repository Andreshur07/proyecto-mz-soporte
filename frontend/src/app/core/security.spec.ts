import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';
import { authInterceptor } from './interceptors/auth.interceptor';
import { AuthService, SESSION_KEY } from './services/auth.service';
import { API_URL } from './config/api.config';

describe('Seguridad frontend',()=>{
 beforeEach(()=>localStorage.clear());
 it('el interceptor agrega el token Bearer',()=>{localStorage.setItem(SESSION_KEY,JSON.stringify({token:'abc',tipo:'Bearer',usuario:{id:1,nombre:'Cora',correo:'c@x.co',rol:'COORDINADOR'}}));TestBed.configureTestingModule({providers:[provideHttpClient(withInterceptors([authInterceptor])),provideHttpClientTesting(),provideRouter([])]});TestBed.inject(HttpClient).get(`${API_URL}/api/solicitudes`).subscribe();const req=TestBed.inject(HttpTestingController).expectOne(`${API_URL}/api/solicitudes`);expect(req.request.headers.get('Authorization')).toBe('Bearer abc');req.flush([]);});
 it('el guard rechaza al usuario no autenticado',()=>{TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting(),provideRouter([])]});const result=TestBed.runInInjectionContext(()=>authGuard({} as never,{} as never));expect(result).toEqual(TestBed.inject(Router).createUrlTree(['/login']));});
 it('el control de rol rechaza una ruta ajena',()=>{localStorage.setItem(SESSION_KEY,JSON.stringify({token:'abc',tipo:'Bearer',usuario:{id:1,nombre:'Soli',correo:'s@x.co',rol:'SOLICITANTE'}}));TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting(),provideRouter([]),AuthService]});const route={data:{roles:['COORDINADOR']}} as never;const result=TestBed.runInInjectionContext(()=>roleGuard(route,{} as never));expect(result).toEqual(TestBed.inject(Router).createUrlTree(['/sin-acceso']));});
 it('permite al auditor su ruta y lo dirige a auditoría',()=>{localStorage.setItem(SESSION_KEY,JSON.stringify({token:'abc',tipo:'Bearer',usuario:{id:4,nombre:'Aura',correo:'a@x.co',rol:'AUDITOR'}}));TestBed.configureTestingModule({providers:[provideHttpClient(),provideHttpClientTesting(),provideRouter([]),AuthService]});const auth=TestBed.inject(AuthService);const route={data:{roles:['AUDITOR']}} as never;expect(TestBed.runInInjectionContext(()=>roleGuard(route,{} as never))).toBe(true);expect(auth.homeForRole()).toBe('/auditoria');});
});
