import { Component, OnInit } from '@angular/core';
import { ParkingService, PagoResidente } from '../../services/parking.service';

@Component({
  selector: 'app-pago-report',
  template: `
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Informe de Pagos - Residentes</h5>
        <span class="badge text-bg-primary">{{ pagos.length }}</span>
      </div>
      <div class="card-body">
        <div class="d-flex gap-2 mb-3">
          <button class="btn btn-primary" (click)="cargarPagos()">
            Actualizar Reporte
          </button>
          <button class="btn btn-danger" (click)="reiniciarMes()">
            Reiniciar Mes
          </button>
        </div>

        <div class="table-responsive" *ngIf="pagos.length > 0">
          <table class="table table-striped table-hover align-middle">
            <thead>
              <tr>
                <th scope="col">Placa</th>
                <th scope="col">Tiempo Acumulado (min)</th>
                <th scope="col" class="text-end">Monto a Pagar</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let p of pagos">
                <td class="fw-bold">{{ p.placa }}</td>
                <td>{{ p.tiempoAcumuladoMinutos }}</td>
                <td class="text-end fw-semibold">{{ p.montoTotal | currency }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div *ngIf="pagos.length === 0" class="alert alert-info mb-0">
          No hay pagos registrados para residentes.
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class PagoReportComponent implements OnInit {

  pagos: PagoResidente[] = [];

  constructor(private parkingService: ParkingService) { }

  ngOnInit(): void {
    this.cargarPagos();
  }

  cargarPagos(): void {
    this.parkingService.generarInformePagos().subscribe({
      next: (data) => this.pagos = data,
      error: (err) => console.error('Error cargando pagos', err)
    });
  }

  reiniciarMes(): void {
    if (confirm('¿Estás seguro de reiniciar el mes? Esto eliminará todas las estancias y residentes.')) {
      this.parkingService.reiniciarMes().subscribe({
        next: () => {
          this.pagos = [];
          alert('Mes reiniciado exitosamente');
        },
        error: (err) => console.error('Error reiniciando mes', err)
      });
    }
  }
}