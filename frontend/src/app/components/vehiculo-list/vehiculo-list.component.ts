import { Component, OnInit } from '@angular/core';
import { ParkingService, Vehiculo } from '../../services/parking.service';

@Component({
  selector: 'app-vehiculo-list',
  template: `
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Vehículos Registrados</h5>
        <span class="badge text-bg-primary">{{ vehiculosFiltrados.length }}</span>
      </div>
      <div class="card-body">
        <div class="mb-3">
          <input
            type="text"
            class="form-control"
            placeholder="Buscar por placa..."
            (keyup)="filtrar($event)">
        </div>

        <div class="table-responsive">
          <table class="table table-striped table-hover align-middle">
            <thead>
              <tr>
                <th scope="col">Placa</th>
                <th scope="col">Tipo</th>
                <th scope="col" class="text-end">QR</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let v of vehiculosFiltrados">
                <td class="fw-bold">{{ v.placa }}</td>
                <td>
                  <span class="badge rounded-pill"
                        [ngClass]="{
                          'text-bg-success': v.tipo === 'OFICIAL',
                          'text-bg-primary': v.tipo === 'RESIDENTE',
                          'text-bg-warning': v.tipo === 'NO_RESIDENTE'
                        }">
                    {{ v.tipo }}
                  </span>
                </td>
                <td class="text-end">
                  <button type="button"
                          class="btn btn-sm btn-outline-secondary"
                          title="Descargar QR para el parabrisas"
                          (click)="descargarQr(v.placa)">
                    <i class="bi bi-qr-code"></i>
                  </button>
                </td>
              </tr>
              <tr *ngIf="vehiculosFiltrados.length === 0">
                <td colspan="3" class="text-center text-muted">
                  No hay vehículos registrados.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class VehiculoListComponent implements OnInit {

  vehiculos: Vehiculo[] = [];
  vehiculosFiltrados: Vehiculo[] = [];

  constructor(private parkingService: ParkingService) { }

  ngOnInit(): void {
    this.cargarVehiculos();
  }

  cargarVehiculos(): void {
    this.parkingService.obtenerVehiculos().subscribe({
      next: (data) => {
        this.vehiculos = data;
        this.vehiculosFiltrados = data;
      },
      error: (err) => console.error('Error cargando vehículos', err)
    });
  }

  filtrar(event: Event): void {
    const valor = (event.target as HTMLInputElement).value.toLowerCase();
    this.vehiculosFiltrados = this.vehiculos.filter(v =>
      v.placa.toLowerCase().includes(valor)
    );
  }

  descargarQr(placa: string): void {
    fetch(`/neo/qr/${placa}`)
      .then(r => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.blob();
      })
      .then(blob => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${placa}-qr.png`;
        a.click();
        URL.revokeObjectURL(url);
      })
      .catch(err => console.error('Error generando QR', err));
  }
}