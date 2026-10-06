import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
@Component({selector:'app-message-page',imports:[RouterLink],template:`<div class="state-card tall"><span class="state-icon">{{forbidden?'!':'◷'}}</span><h1>{{forbidden?'Acceso no permitido':'Funciones próximamente'}}</h1><p>{{forbidden?'Tu rol no tiene permiso para acceder a esta sección.':copy}}</p><a class="button primary" [routerLink]="auth.homeForRole()">Volver al inicio</a></div>`})
export class MessagePageComponent{readonly auth=inject(AuthService);readonly forbidden=inject(Router).url.includes('sin-acceso');get copy(){return this.auth.usuario()?.rol==='AGENTE'?'Las funciones para agentes estarán disponibles en el siguiente incremento.':'Las funciones para auditoría estarán disponibles en un incremento posterior.';}}
