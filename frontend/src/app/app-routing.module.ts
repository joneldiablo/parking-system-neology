import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VehiculoListComponent } from './components/vehiculo-list/vehiculo-list.component';
import { EstanciaFormComponent } from './components/estancia-form/estancia-form.component';
import { VehiculoFormComponent } from './components/vehiculo-form/vehiculo-form.component';
import { PagoReportComponent } from './components/pago-report/pago-report.component';
import { LoginComponent } from './components/login/login.component';
import { UsuariosComponent } from './components/usuarios/usuarios.component';
import { AuthGuard } from './guards/auth.guard';
import { SuperAdminGuard } from './guards/superadmin.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'vehiculos', component: VehiculoListComponent, canActivate: [AuthGuard] },
  { path: 'estancias', component: EstanciaFormComponent, canActivate: [AuthGuard] },
  { path: 'registro', component: VehiculoFormComponent, canActivate: [AuthGuard] },
  { path: 'pagos', component: PagoReportComponent, canActivate: [AuthGuard] },
  { path: 'usuarios', component: UsuariosComponent, canActivate: [AuthGuard, SuperAdminGuard] },
  { path: '', redirectTo: 'vehiculos', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }