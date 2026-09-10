import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { VehiculoListComponent } from './components/vehiculo-list/vehiculo-list.component';
import { EstanciaFormComponent } from './components/estancia-form/estancia-form.component';
import { VehiculoFormComponent } from './components/vehiculo-form/vehiculo-form.component';
import { PagoReportComponent } from './components/pago-report/pago-report.component';
import { LoginComponent } from './components/login/login.component';
import { UsuariosComponent } from './components/usuarios/usuarios.component';
import { AuthInterceptor } from './services/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    VehiculoListComponent,
    EstanciaFormComponent,
    VehiculoFormComponent,
    PagoReportComponent,
    LoginComponent,
    UsuariosComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }