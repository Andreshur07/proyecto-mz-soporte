import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Auditoria, FiltroAuditoria } from '../../core/models/auditoria.model';
import { AuditoriaService } from '../../core/services/auditoria.service';
import { FeedbackComponent } from '../../shared/feedback.component';

@Component({selector:'app-auditoria',imports:[DatePipe,FormsModule,RouterLink,FeedbackComponent],template:`
 <div class="page-head"><div><p class="eyebrow">TRAZABILIDAD</p><h1>Auditoría</h1><p>Consulta el historial de cambios de las solicitudes.</p></div></div>
 @if(message()){<app-feedback type="error" [message]="message()"/>}
 <section class="card filters-card"><div class="filters-head"><div><strong>Filtros de auditoría</strong><span>Los registros más recientes aparecen primero</span></div></div><form class="filters-grid audit-filters" (ngSubmit)="applyFilters()">
  <label>ID solicitud<input name="solicitudId" type="number" min="1" [(ngModel)]="filters.solicitudId"></label><label>ID actor<input name="actorId" type="number" min="1" [(ngModel)]="filters.actorId"></label><label>Campo<input name="campo" [(ngModel)]="filters.campo" placeholder="Ej. estado"></label><label>Fecha desde<input name="fechaDesde" type="date" [(ngModel)]="filters.fechaDesde"></label><label>Fecha hasta<input name="fechaHasta" type="date" [(ngModel)]="filters.fechaHasta"></label><div class="filter-actions"><button class="button primary" [disabled]="loading()">Aplicar filtros</button><button class="button secondary" type="button" [disabled]="loading()" (click)="clearFilters()">Limpiar filtros</button></div>
 </form></section>
 @if(loading()){<div class="state-card"><span class="spinner"></span><h2>Cargando auditoría</h2></div>}
 @else if(error()){<div class="state-card"><span class="state-icon">!</span><h2>No pudimos cargar la auditoría</h2><button class="button secondary" (click)="applyFilters()">Reintentar</button></div>}
 @else if(!items().length){<div class="state-card"><span class="state-icon">✓</span><h2>No hay registros para los filtros seleccionados</h2></div>}
 @else{<section class="card table-card"><div class="table-summary"><strong>{{items().length}} registros</strong><span>Historial de cambios</span></div><div class="table-wrap"><table><thead><tr><th>Fecha</th><th>Solicitud</th><th>Actor</th><th>Rol</th><th>Campo</th><th>Valor anterior</th><th>Valor nuevo</th><th>Acción</th></tr></thead><tbody>@for(row of items();track row.id){<tr><td>{{row.fecha|date:'dd MMM yyyy, HH:mm'}}</td><td><strong>#{{row.solicitudId}}</strong></td><td><strong>{{row.actor.nombre}}</strong><br><small>{{row.actor.correo}}</small></td><td>{{row.actor.rol}}</td><td>{{row.campo}}</td><td>{{row.valorAnterior}}</td><td>{{row.valorNuevo}}</td><td><a class="text-link" [routerLink]="['/solicitudes',row.solicitudId]">Ver solicitud →</a></td></tr>}</tbody></table></div></section>}
`})
export class AuditoriaComponent implements OnInit{
 readonly items=signal<Auditoria[]>([]);readonly loading=signal(true);readonly error=signal(false);readonly message=signal('');filters:{solicitudId:number|null;actorId:number|null;campo:string;fechaDesde:string;fechaHasta:string}=this.emptyFilters();
 constructor(private api:AuditoriaService){}ngOnInit(){this.applyFilters();}
 applyFilters(){if(!this.validDates())return;this.loading.set(true);this.error.set(false);this.message.set('');this.api.consultar(this.normalized()).pipe(finalize(()=>this.loading.set(false))).subscribe({next:x=>this.items.set(x),error:e=>{this.error.set(true);this.message.set(e.error?.message||e.error?.mensaje||'No se pudo consultar la auditoría.');}});}
 clearFilters(){this.filters=this.emptyFilters();this.applyFilters();}
 private validDates(){if(this.filters.fechaDesde&&this.filters.fechaHasta&&this.filters.fechaDesde>this.filters.fechaHasta){this.message.set('La fecha desde no puede ser posterior a la fecha hasta.');return false;}return true;}
 private normalized():FiltroAuditoria{return Object.fromEntries(Object.entries(this.filters).filter(([,v])=>v!==''&&v!==null)) as FiltroAuditoria;}
 private emptyFilters(){return{solicitudId:null as number|null,actorId:null as number|null,campo:'',fechaDesde:'',fechaHasta:''};}
}
