import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Solicitud } from '../../core/models/solicitud.model';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { StatusBadgeComponent } from '../../shared/status-badge.component';

@Component({selector:'app-detalle-solicitud',imports:[DatePipe,RouterLink,StatusBadgeComponent],template:`
  <div class="page-head"><div><p class="eyebrow">MIS SOLICITUDES</p><h1>Detalle de solicitud</h1><p>Información registrada para esta solicitud.</p></div><a class="button secondary" routerLink="/mis-solicitudes">← Volver al listado</a></div>
  @if(loading()){<div class="state-card"><span class="spinner"></span><h2>Cargando detalle</h2></div>}
  @else if(notFound()){<div class="state-card"><span class="state-icon">?</span><h2>Solicitud no encontrada</h2><p>No existe o no tienes permiso para consultarla.</p><a class="button primary" routerLink="/mis-solicitudes">Ir a mis solicitudes</a></div>}
  @else if(error()){<div class="state-card"><span class="state-icon">!</span><h2>No pudimos cargar la solicitud</h2><p>Intenta nuevamente más tarde.</p></div>}
  @else if(item();as s){<section class="card detail-card"><div class="detail-title"><div><span class="id">SOLICITUD #{{s.id}}</span><h2>{{s.titulo}}</h2></div><div class="badges"><app-status-badge [value]="s.estado"/><app-status-badge [value]="s.prioridad"/></div></div><div class="detail-grid"><div><span>Categoría</span><strong>{{label(s.categoria)}}</strong></div><div><span>Fecha de creación</span><strong>{{s.fechaCreacion|date:'dd MMM yyyy, HH:mm'}}</strong></div><div><span>Estado</span><strong>{{s.estado}}</strong></div><div><span>Prioridad</span><strong>{{s.prioridad}}</strong></div></div><div class="description"><span>Descripción</span><p>{{s.descripcion}}</p></div></section>}`})
export class DetalleSolicitudComponent implements OnInit{readonly item=signal<Solicitud|null>(null);readonly loading=signal(true);readonly notFound=signal(false);readonly error=signal(false);constructor(private route:ActivatedRoute,private api:SolicitudesService){}ngOnInit(){const id=Number(this.route.snapshot.paramMap.get('id'));if(!Number.isInteger(id)||id<1){this.notFound.set(true);this.loading.set(false);return;}this.api.detalle(id).subscribe({next:x=>{this.item.set(x);this.loading.set(false);},error:e=>{e.status===404?this.notFound.set(true):this.error.set(true);this.loading.set(false);}});}label(v:string){return v.charAt(0)+v.slice(1).toLowerCase();}}
