import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-layout', imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <header class="topbar">
        <a class="brand" [routerLink]="auth.homeForRole()" (click)="menuOpen.set(false)">
          <span class="brand-mark" aria-hidden="true">GS</span><span><strong>Gestión de Soporte</strong><small>Centro de solicitudes</small></span>
        </a>
        <button class="menu-button" type="button" (click)="menuOpen.set(!menuOpen())" [attr.aria-expanded]="menuOpen()" aria-label="Abrir navegación">☰</button>
        <div class="account">
          <span class="avatar">{{ initials() }}</span><span class="account-copy"><strong>{{ auth.usuario()?.nombre }}</strong><small>{{ roleLabel() }}</small></span>
          <button class="button secondary compact" type="button" (click)="auth.logout()">Cerrar sesión</button>
        </div>
      </header>
      <div class="workspace">
        <aside class="sidebar" [class.open]="menuOpen()">
          <p class="nav-label">MENÚ PRINCIPAL</p>
          <nav aria-label="Navegación principal">
            @if (auth.usuario()?.rol === 'SOLICITANTE') {
              <a routerLink="/mis-solicitudes" routerLinkActive="active" (click)="menuOpen.set(false)">▣ <span>Mis solicitudes</span></a>
              <a routerLink="/solicitudes/nueva" routerLinkActive="active" (click)="menuOpen.set(false)">＋ <span>Nueva solicitud</span></a>
            }
            @if (auth.usuario()?.rol === 'COORDINADOR') {
              <a routerLink="/solicitudes" routerLinkActive="active" (click)="menuOpen.set(false)">▤ <span>Gestión de solicitudes</span></a>
              <a routerLink="/indicadores" routerLinkActive="active" (click)="menuOpen.set(false)">▦ <span>Indicadores</span></a>
            }
            @if (auth.usuario()?.rol === 'AGENTE') {
              <a routerLink="/solicitudes-asignadas" routerLinkActive="active" (click)="menuOpen.set(false)">▤ <span>Mis solicitudes asignadas</span></a>
            }
            @if (auth.usuario()?.rol === 'AUDITOR') {
              <a routerLink="/auditoria" routerLinkActive="active" (click)="menuOpen.set(false)">◷ <span>Auditoría</span></a>
            }
          </nav>
        </aside>
        @if (menuOpen()) { <button class="backdrop" aria-label="Cerrar navegación" (click)="menuOpen.set(false)"></button> }
        <main class="content"><router-outlet /></main>
      </div>
    </div>`
})
export class LayoutComponent {
  readonly menuOpen = signal(false);
  constructor(readonly auth: AuthService) {}
  initials(): string { return (this.auth.usuario()?.nombre ?? '').split(' ').slice(0,2).map(x => x[0]).join('').toUpperCase(); }
  roleLabel(): string { return ({SOLICITANTE:'Solicitante', COORDINADOR:'Coordinador', AGENTE:'Agente', AUDITOR:'Auditor'} as Record<string,string>)[this.auth.usuario()?.rol ?? ''] ?? ''; }
}
