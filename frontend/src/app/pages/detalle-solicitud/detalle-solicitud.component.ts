import { Component, OnInit, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { Comentario, Estado, Solicitud } from '../../core/models/solicitud.model';
import { AuthService } from '../../core/services/auth.service';
import { ComentariosService } from '../../core/services/comentarios.service';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { StatusBadgeComponent } from '../../shared/status-badge.component';
import { FeedbackComponent } from '../../shared/feedback.component';

@Component({selector:'app-detalle-solicitud',imports:[DatePipe,FormsModule,RouterLink,StatusBadgeComponent,FeedbackComponent],template:`
  <div class="page-head"><div><p class="eyebrow">{{role()==='AGENTE'?'OPERACIÓN':role()==='COORDINADOR'?'COORDINACIÓN':'MIS SOLICITUDES'}}</p><h1>Detalle de solicitud</h1><p>Información y seguimiento del caso.</p></div><a class="button secondary" [routerLink]="backLink()">← Volver al listado</a></div>
  @if(message()){<app-feedback [type]="messageType()" [message]="message()"/>}
  @if(loading()){<div class="state-card"><span class="spinner"></span><h2>Cargando detalle</h2></div>}
  @else if(notFound()){<div class="state-card"><span class="state-icon">?</span><h2>Solicitud no encontrada</h2><p>No existe o no tienes permiso para consultarla.</p><a class="button primary" [routerLink]="backLink()">Volver</a></div>}
  @else if(error()){<div class="state-card"><span class="state-icon">!</span><h2>No pudimos cargar la solicitud</h2></div>}
  @else if(item();as s){
   <section class="card detail-card"><div class="detail-title"><div><span class="id">SOLICITUD #{{s.id}}</span><h2>{{s.titulo}}</h2></div><div class="badges"><app-status-badge [value]="s.estado"/><app-status-badge [value]="s.prioridad"/></div></div>
   <div class="detail-grid"><div><span>Categoría</span><strong>{{label(s.categoria)}}</strong></div><div><span>Fecha de creación</span><strong>{{s.fechaCreacion|date:'dd MMM yyyy, HH:mm'}}</strong></div><div><span>Solicitante</span><strong>{{s.solicitante?.nombre || 'No disponible'}}</strong></div><div><span>Agente asignado</span><strong>{{s.agenteAsignado?.nombre || 'Sin asignar'}}</strong></div></div>
   <div class="description"><span>Descripción</span><p>{{s.descripcion}}</p></div>
   @if(s.justificacionPrioridadAlta){<div class="high-priority-info"><h3>Antecedente de prioridad alta</h3><p>{{s.justificacionPrioridadAlta}}</p><strong>Fecha objetivo: {{s.fechaObjetivo|date:'dd MMM yyyy'}}</strong></div>}
   @if(role()==='AGENTE'&&nextState();as next){<div class="operation-panel"><div><span>Gestión de estado</span><strong>{{s.estado}} → {{next}}</strong></div><button class="button primary" [disabled]="actionBusy()" (click)="changeState(next)">{{next==='EN_PROCESO'?'Iniciar atención':'Marcar como resuelta'}}</button></div>}
   @if(canResolveAsRequester()){<div class="operation-panel"><div><span>Resolución pendiente de confirmación</span><strong>¿El caso quedó solucionado?</strong></div><div class="inline-actions"><button class="button primary" [disabled]="actionBusy()" (click)="close()">Confirmar cierre</button><button class="button secondary" [disabled]="actionBusy()" (click)="showReopen.set(true)">Reabrir solicitud</button></div></div>}
   </section>
   <section class="card comments-card"><div class="section-head"><div><p class="eyebrow">SEGUIMIENTO</p><h2>Comentarios</h2></div><span>{{comments().length}} {{comments().length===1?'comentario':'comentarios'}}</span></div>
   @if(commentsError()){<app-feedback type="error" message="No fue posible cargar los comentarios."/>}@else if(!comments().length){<p class="empty-copy">Aún no hay comentarios en esta solicitud.</p>}@else{<div class="comment-list">@for(c of comments();track c.id){<article class="comment"><div><strong>{{c.autor.nombre}}</strong>@if(c.autor.rol){<span class="author-role">{{c.autor.rol}}</span>}<time>{{c.fechaCreacion|date:'dd MMM yyyy, HH:mm'}}</time></div><p>{{c.contenido}}</p></article>}</div>}
   @if(role()==='AGENTE'){<form class="comment-form" (ngSubmit)="publish()"><div class="field"><label for="contenido">Nuevo comentario</label><textarea id="contenido" name="contenido" rows="4" maxlength="2000" required [(ngModel)]="commentText" placeholder="Escribe una actualización clara para el seguimiento"></textarea><span class="field-hint">{{commentText.length}} / 2000</span>@if(commentSubmitted()&&!commentText.trim()){<span class="field-error">El comentario es obligatorio.</span>}</div><button class="button primary" [disabled]="commentBusy()">{{commentBusy()?'Publicando…':'Publicar comentario'}}</button></form>}
   </section>
  }
  @if(showReopen()){<div class="modal-backdrop"><section class="card modal" role="dialog" aria-modal="true"><h2>Reabrir solicitud</h2><p>Describe por qué la solución no resolvió el caso.</p><form (ngSubmit)="reopen()"><div class="field"><label for="motivo">Motivo</label><textarea id="motivo" name="motivo" rows="5" maxlength="1000" required [(ngModel)]="reopenReason"></textarea><span class="field-hint">{{reopenReason.length}} / 1000</span>@if(reopenSubmitted()&&!reopenReason.trim()){<span class="field-error">El motivo es obligatorio.</span>}</div><div class="form-actions"><button type="button" class="button secondary" [disabled]="actionBusy()" (click)="showReopen.set(false)">Cancelar</button><button class="button primary" [disabled]="actionBusy()">Reabrir</button></div></form></section></div>}
`})
export class DetalleSolicitudComponent implements OnInit{
 readonly item=signal<Solicitud|null>(null);readonly comments=signal<Comentario[]>([]);readonly loading=signal(true);readonly notFound=signal(false);readonly error=signal(false);readonly commentsError=signal(false);readonly message=signal('');readonly messageType=signal<'success'|'error'>('success');readonly actionBusy=signal(false);readonly commentBusy=signal(false);readonly commentSubmitted=signal(false);readonly showReopen=signal(false);readonly reopenSubmitted=signal(false);commentText='';reopenReason='';private id=0;
 readonly role=computed(()=>this.auth.usuario()?.rol);readonly nextState=computed<Estado|null>(()=>{const state=this.item()?.estado;if(state==='NUEVO'||state==='REABIERTO')return'EN_PROCESO';if(state==='EN_PROCESO')return'RESUELTO';return null;});
 readonly canResolveAsRequester=computed(()=>this.role()==='SOLICITANTE'&&this.item()?.estado==='RESUELTO');
 constructor(private route:ActivatedRoute,private api:SolicitudesService,private commentsApi:ComentariosService,private auth:AuthService){}
 ngOnInit(){this.id=Number(this.route.snapshot.paramMap.get('id'));if(!Number.isInteger(this.id)||this.id<1){this.notFound.set(true);this.loading.set(false);return;}forkJoin({item:this.api.detalle(this.id),comments:this.commentsApi.listar(this.id)}).subscribe({next:r=>{this.item.set(r.item);this.comments.set(r.comments);this.loading.set(false);},error:e=>{e.status===404||e.status===403?this.notFound.set(true):this.error.set(true);this.loading.set(false);}});}
 backLink(){return this.role()==='AGENTE'?'/solicitudes-asignadas':this.role()==='COORDINADOR'?'/solicitudes':'/mis-solicitudes';}
 changeState(estado:Estado){if(this.actionBusy())return;this.actionBusy.set(true);this.api.cambiarEstado(this.id,{estado}).pipe(finalize(()=>this.actionBusy.set(false))).subscribe({next:x=>{this.item.set(x);this.success(`El estado cambió a ${estado}.`);},error:e=>this.fail(e,'No se pudo cambiar el estado.')});}
 close(){if(this.actionBusy())return;this.actionBusy.set(true);this.api.confirmarCierre(this.id).pipe(finalize(()=>this.actionBusy.set(false))).subscribe({next:x=>{this.item.set(x);this.success('La solicitud se cerró correctamente.');},error:e=>this.fail(e,'No se pudo confirmar el cierre.')});}
 reopen(){this.reopenSubmitted.set(true);if(this.actionBusy()||!this.reopenReason.trim()||this.reopenReason.length>1000)return;this.actionBusy.set(true);this.api.reabrir(this.id,{motivo:this.reopenReason.trim()}).pipe(finalize(()=>this.actionBusy.set(false))).subscribe({next:x=>{this.item.set(x);this.showReopen.set(false);this.reopenReason='';this.success('La solicitud fue reabierta.');},error:e=>this.fail(e,'No se pudo reabrir la solicitud.')});}
 publish(){this.commentSubmitted.set(true);if(this.commentBusy()||!this.commentText.trim()||this.commentText.length>2000)return;this.commentBusy.set(true);this.commentsApi.crear(this.id,{contenido:this.commentText.trim()}).pipe(finalize(()=>this.commentBusy.set(false))).subscribe({next:c=>{this.comments.update(xs=>[...xs,c]);this.commentText='';this.commentSubmitted.set(false);this.success('Comentario publicado.');},error:e=>this.fail(e,'No se pudo publicar el comentario.')});}
 label(v:string){return v.charAt(0)+v.slice(1).toLowerCase();}private success(text:string){this.messageType.set('success');this.message.set(text);}private fail(e:HttpErrorResponse,fallback:string){this.messageType.set('error');this.message.set(e.error?.message||e.error?.mensaje||fallback);}
}
