import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Solicitud } from '../../core/models/solicitud.model';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { StatusBadgeComponent } from '../../shared/status-badge.component';
import { FeedbackComponent } from '../../shared/feedback.component';

@Component({selector:'app-mis-solicitudes',imports:[DatePipe,RouterLink,StatusBadgeComponent,FeedbackComponent],template:`
  <div class="page-head"><div><p class="eyebrow">MI ESPACIO</p><h1>Mis solicitudes</h1><p>Consulta el estado de tus solicitudes de soporte.</p></div><a class="button primary" routerLink="/solicitudes/nueva">＋ Nueva solicitud</a></div>
  @if(success()){<app-feedback type="success" [message]="success()"/>}
  @if(loading()){<div class="state-card"><span class="spinner"></span><h2>Cargando solicitudes</h2><p>Estamos consultando tu información.</p></div>}
  @else if(error()){<div class="state-card"><span class="state-icon">!</span><h2>No pudimos cargar tus solicitudes</h2><p>{{error()}}</p><button class="button secondary" (click)="load()">Reintentar</button></div>}
  @else if(!items().length){<div class="state-card"><span class="state-icon">＋</span><h2>Aún no tienes solicitudes</h2><p>Crea tu primera solicitud para recibir ayuda del equipo.</p><a class="button primary" routerLink="/solicitudes/nueva">Crear solicitud</a></div>}
  @else {<section class="card table-card"><div class="table-summary"><strong>{{items().length}} {{items().length===1?'solicitud':'solicitudes'}}</strong><span>Mostrando todos los registros</span></div><div class="table-wrap"><table><thead><tr><th>ID</th><th>Título</th><th>Categoría</th><th>Fecha de creación</th><th>Estado</th><th>Prioridad</th><th><span class="sr-only">Acciones</span></th></tr></thead><tbody>@for(s of items();track s.id){<tr><td class="id">#{{s.id}}</td><td><strong>{{s.titulo}}</strong></td><td>{{label(s.categoria)}}</td><td>{{s.fechaCreacion|date:'dd MMM yyyy, HH:mm'}}</td><td><app-status-badge [value]="s.estado"/></td><td><app-status-badge [value]="s.prioridad"/></td><td><a class="text-link" [routerLink]="['/solicitudes',s.id]">Ver detalle →</a></td></tr>}</tbody></table></div></section>}`})
export class MisSolicitudesComponent implements OnInit{
 readonly items=signal<Solicitud[]>([]);readonly loading=signal(true);readonly error=signal('');readonly success=signal('');
 constructor(private api:SolicitudesService,router:Router){this.success.set((router.getCurrentNavigation()?.extras.state?.['success'] as string)||'');}
 ngOnInit(){this.load();} load(){this.loading.set(true);this.error.set('');this.api.mias().subscribe({next:x=>{this.items.set(x);this.loading.set(false);},error:()=>{this.error.set('Verifica tu conexión e intenta nuevamente.');this.loading.set(false);}});} label(v:string){return v.charAt(0)+v.slice(1).toLowerCase();}
}
