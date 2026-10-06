import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../config/api.config';
import { LoginRequest, Rol, Sesion } from '../models/auth.model';

export const SESSION_KEY = 'marz_sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly sessionState = signal<Sesion | null>(this.readSession());
  readonly session = this.sessionState.asReadonly();
  readonly usuario = computed(() => this.sessionState()?.usuario ?? null);
  readonly autenticado = computed(() => !!this.sessionState()?.token);

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<Sesion> {
    return this.http.post<Sesion>(`${API_URL}/api/auth/login`, credentials).pipe(tap((session) => this.saveSession(session)));
  }

  hasRole(roles: Rol[]): boolean { return !!this.usuario() && roles.includes(this.usuario()!.rol); }
  token(): string | null { return this.sessionState()?.token ?? null; }
  homeForRole(role = this.usuario()?.rol): string {
    if (role === 'SOLICITANTE') return '/mis-solicitudes';
    if (role === 'COORDINADOR') return '/solicitudes';
    return '/proximamente';
  }
  logout(redirect = true): void {
    localStorage.removeItem(SESSION_KEY);
    this.sessionState.set(null);
    if (redirect) void this.router.navigate(['/login']);
  }
  private saveSession(session: Sesion): void {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    this.sessionState.set(session);
  }
  private readSession(): Sesion | null {
    try {
      const raw = localStorage.getItem(SESSION_KEY);
      return raw ? JSON.parse(raw) as Sesion : null;
    } catch { localStorage.removeItem(SESSION_KEY); return null; }
  }
}
