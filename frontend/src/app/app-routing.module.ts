import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VehiculoListComponent } from './components/vehiculo-list/vehiculo-list.component';
import { EstanciaFormComponent } from './components/estancia-form/estancia-form.component';
import { VehiculoFormComponent } from './components/vehiculo-form/vehiculo-form.component';
import { PagoReportComponent } from './components/pago-report/pago-report.component';

const routes: Routes = [
  { path: '', redirectTo: 'vehiculos', pathMatch: 'full' },
  { path: 'vehiculos', component: VehiculoListComponent },
  { path: 'estancias', component: EstanciaFormComponent },
  { path: 'registro', component: VehiculoFormComponent },
  { path: 'pagos', component: PagoReportComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
