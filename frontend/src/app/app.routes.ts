import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { homeGuard } from './core/guards/home.guard';
import { LayoutComponent } from './layout/layout.component';
import { LoginComponent } from './pages/login/login.component';
import { MisSolicitudesComponent } from './pages/mis-solicitudes/mis-solicitudes.component';
import { CrearSolicitudComponent } from './pages/crear-solicitud/crear-solicitud.component';
import { DetalleSolicitudComponent } from './pages/detalle-solicitud/detalle-solicitud.component';
import { SolicitudesCoordinadorComponent } from './pages/solicitudes-coordinador/solicitudes-coordinador.component';
import { SolicitudesAsignadasComponent } from './pages/solicitudes-asignadas/solicitudes-asignadas.component';
import { MessagePageComponent } from './pages/message-page/message-page.component';

export const routes: Routes = [
 {path:'login',component:LoginComponent},
 {path:'',component:LayoutComponent,canActivate:[authGuard],children:[
  {path:'mis-solicitudes',component:MisSolicitudesComponent,canActivate:[roleGuard],data:{roles:['SOLICITANTE']}},
  {path:'solicitudes/nueva',component:CrearSolicitudComponent,canActivate:[roleGuard],data:{roles:['SOLICITANTE']}},
  {path:'solicitudes/:id',component:DetalleSolicitudComponent,canActivate:[roleGuard],data:{roles:['SOLICITANTE','AGENTE','COORDINADOR']}},
  {path:'solicitudes',component:SolicitudesCoordinadorComponent,canActivate:[roleGuard],data:{roles:['COORDINADOR']}},
  {path:'solicitudes-asignadas',component:SolicitudesAsignadasComponent,canActivate:[roleGuard],data:{roles:['AGENTE']}},
  {path:'proximamente',component:MessagePageComponent,canActivate:[roleGuard],data:{roles:['AUDITOR']}},
  {path:'sin-acceso',component:MessagePageComponent},
  {path:'',pathMatch:'full',component:MessagePageComponent,canActivate:[homeGuard]}
 ]},
 {path:'**',redirectTo:''}
];
