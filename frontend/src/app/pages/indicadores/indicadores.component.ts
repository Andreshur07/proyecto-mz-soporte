import { Component, OnInit, signal } from '@angular/core';
import { Indicadores } from '../../core/models/indicadores.model';
import { IndicadoresService } from '../../core/services/indicadores.service';

@Component({selector:'app-indicadores',template:`
 <div class="page-head"><div><p class="eyebrow">COORDINACIÓN</p><h1>Indicadores de gestión</h1><p>Resumen general del estado de las solicitudes.</p></div></div>
 @if(loading()){<div class="state-card"><span class="spinner"></span><h2>Cargando indicadores</h2></div>}
 @else if(error()){<div class="state-card"><span class="state-icon">!</span><h2>No pudimos cargar los indicadores</h2><button class="button secondary" (click)="load()">Reintentar</button></div>}
 @else if(data();as d){
  <section class="metric-grid" aria-label="Resumen de solicitudes">
   @for(card of cards(d);track card.label){<article class="card metric-card"><span>{{card.label}}</span><strong>{{card.value}}</strong></article>}
  </section>
  <section class="card priority-panel"><div class="section-head"><div><p class="eyebrow">PRIORIDAD</p><h2>Solicitudes por prioridad</h2></div></div>
   <div class="priority-metrics">@for(item of priorities(d);track item.label){<div><span>{{item.label}}</span><strong>{{item.value}}</strong><div class="bar"><i [style.width.%]="percent(item.value,d.totalSolicitudes)"></i></div></div>}</div>
  </section>
 }
`})
export class IndicadoresComponent implements OnInit{
 readonly data=signal<Indicadores|null>(null);readonly loading=signal(true);readonly error=signal(false);
 constructor(private api:IndicadoresService){}ngOnInit(){this.load();}
 load(){this.loading.set(true);this.error.set(false);this.api.obtener().subscribe({next:x=>{this.data.set(x);this.loading.set(false);},error:()=>{this.error.set(true);this.loading.set(false);}});}
 cards(d:Indicadores){return[{label:'Total solicitudes',value:d.totalSolicitudes},{label:'Nuevas',value:d.porEstado.NUEVO??0},{label:'En proceso',value:d.porEstado.EN_PROCESO??0},{label:'Resueltas',value:d.porEstado.RESUELTO??0},{label:'Cerradas',value:d.porEstado.CERRADO??0},{label:'Reabiertas',value:d.porEstado.REABIERTO??0},{label:'Sin asignar',value:d.sinAsignar}];}
 priorities(d:Indicadores){return[{label:'Baja',value:d.porPrioridad.BAJA??0},{label:'Media',value:d.porPrioridad.MEDIA??0},{label:'Alta',value:d.porPrioridad.ALTA??0}];}
 percent(value:number,total:number){return total>0?Math.round(value*100/total):0;}
}
