import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { VehiculoListComponent } from './components/vehiculo-list/vehiculo-list.component';
import { EstanciaFormComponent } from './components/estancia-form/estancia-form.component';
import { VehiculoFormComponent } from './components/vehiculo-form/vehiculo-form.component';
import { PagoReportComponent } from './components/pago-report/pago-report.component';

@NgModule({
  declarations: [
    AppComponent,
    VehiculoListComponent,
    EstanciaFormComponent,
    VehiculoFormComponent,
    PagoReportComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }