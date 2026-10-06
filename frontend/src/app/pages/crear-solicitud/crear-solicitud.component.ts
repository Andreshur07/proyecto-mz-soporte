import { Component, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Categoria } from '../../core/models/solicitud.model';
import { SolicitudesService } from '../../core/services/solicitudes.service';
import { FeedbackComponent } from '../../shared/feedback.component';

@Component({ selector:'app-crear-solicitud', imports:[ReactiveFormsModule,RouterLink,FeedbackComponent], template:`
  <div class="page-head"><div><p class="eyebrow">SOLICITUDES</p><h1>Nueva solicitud</h1><p>Cuéntanos qué sucede para que podamos ayudarte.</p></div><a class="button secondary" routerLink="/mis-solicitudes">Volver</a></div>
  <section class="card form-card"><form [formGroup]="form" (ngSubmit)="submit()" novalidate>
    @if(error()){<app-feedback type="error" [message]="error()"/>}
    <div class="field"><label for="titulo">Título</label><input id="titulo" formControlName="titulo" maxlength="150" placeholder="Resume el problema en una frase"/><span class="field-hint">{{form.controls.titulo.value.length}} / 150</span>@if(invalid('titulo')){<small class="field-error">Escribe un título (máximo 150 caracteres).</small>}</div>
    <div class="field"><label for="categoria">Categoría</label><select id="categoria" formControlName="categoria"><option value="" disabled>Selecciona una categoría</option>@for(c of categorias;track c.value){<option [value]="c.value">{{c.label}}</option>}</select>@if(invalid('categoria')){<small class="field-error">Selecciona una categoría.</small>}</div>
    <div class="field"><label for="descripcion">Descripción</label><textarea id="descripcion" formControlName="descripcion" maxlength="2000" rows="7" placeholder="Describe el problema, cuándo ocurre y cualquier detalle útil"></textarea><span class="field-hint">{{form.controls.descripcion.value.length}} / 2000</span>@if(invalid('descripcion')){<small class="field-error">Escribe una descripción (máximo 2000 caracteres).</small>}</div>
    <div class="form-note"><strong>Al crearla</strong><span>La solicitud iniciará con estado NUEVO y prioridad MEDIA.</span></div>
    <div class="form-actions"><a class="button secondary" routerLink="/mis-solicitudes">Cancelar</a><button class="button primary" [disabled]="loading()">{{loading()?'Creando…':'Crear solicitud'}}</button></div>
  </form></section>` })
export class CrearSolicitudComponent {
  readonly loading=signal(false); readonly error=signal('');
  readonly categorias:{value:Categoria,label:string}[]=[{value:'HARDWARE',label:'Hardware'},{value:'SOFTWARE',label:'Software'},{value:'ACCESO',label:'Acceso'},{value:'RED',label:'Red'},{value:'OTRO',label:'Otro'}];
  readonly form;
  constructor(fb:FormBuilder,private api:SolicitudesService,private router:Router){this.form=fb.nonNullable.group({titulo:['',[Validators.required,Validators.maxLength(150)]],descripcion:['',[Validators.required,Validators.maxLength(2000)]],categoria:['' as Categoria,Validators.required]});}
  invalid(name:keyof typeof this.form.controls){const c=this.form.controls[name];return c.invalid&&c.touched;}
  submit(){if(this.form.invalid){this.form.markAllAsTouched();return;}this.loading.set(true);this.error.set('');this.api.crear(this.form.getRawValue()).pipe(finalize(()=>this.loading.set(false))).subscribe({next:s=>void this.router.navigate(['/mis-solicitudes'],{state:{success:`Solicitud #${s.id} creada con estado NUEVO y prioridad MEDIA.`}}),error:()=>this.error.set('No pudimos crear la solicitud. Revisa los datos e intenta nuevamente.')});}
}
