import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Vehiculo {
  placa: string;
  tipo: string;
}

export interface Estancia {
  id: number;
  placa: string;
  tipoVehiculo: string;
  fechaEntrada: string;
  fechaSalida: string | null;
  duracionMinutos: number;
  costoTotal: number;
}

export interface PagoResidente {
  placa: string;
  tiempoAcumuladoMinutos: number;
  montoTotal: number;
}

@Injectable({
  providedIn: 'root'
})
export class ParkingService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  registrarVehiculo(tipo: string, placa: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/vehiculos/${tipo}`, { placa, tipo });
  }

  registrarEntrada(placa: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/estancias/entrada`, { placa });
  }

  registrarSalida(placa: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/estancias/salida`, { placa });
  }

  obtenerVehiculos(): Observable<Vehiculo[]> {
    return this.http.get<Vehiculo[]>(`${this.apiUrl}/vehiculos`);
  }

  obtenerEstancias(): Observable<Estancia[]> {
    return this.http.get<Estancia[]>(`${this.apiUrl}/estancias`);
  }

  obtenerEstanciasPorPlaca(placa: string): Observable<Estancia[]> {
    return this.http.get<Estancia[]>(`${this.apiUrl}/estancias/${placa}`);
  }

  generarInformePagos(): Observable<PagoResidente[]> {
    return this.http.get<PagoResidente[]>(`${this.apiUrl}/residentes/pagos`);
  }

  reiniciarMes(): Observable<any> {
    return this.http.post(`${this.apiUrl}/mes/iniciar`, {});
  }
}
