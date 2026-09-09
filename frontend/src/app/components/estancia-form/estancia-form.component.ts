import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParkingService } from '../../services/parking.service';

@Component({
  selector: 'app-estancia-form',
  template: `
    <div class="card">
      <div class="card-header">
        <h5 class="mb-0">Gestionar Estancias</h5>
      </div>
      <div class="card-body">
        <form [formGroup]="form" class="row g-3 align-items-end" (ngSubmit)="registrarSalida()">
          <div class="col-md-4">
            <label for="placa" class="form-label">Placa del vehículo</label>
            <input
              id="placa"
              type="text"
              class="form-control"
              formControlName="placa"
              placeholder="Ex. ABC123"
              maxlength="10"
              (keyup.enter)="registrarEntrada()">
            <div *ngIf="form.get('placa')?.invalid && form.get('placa')?.touched"
                 class="text-danger small mt-1">
              Placa inválida (3-10 caracteres alfanuméricos).
            </div>
          </div>

          <div class="col-md-4">
            <button type="button" class="btn btn-primary w-100"
                    (click)="registrarEntrada()" [disabled]="form.invalid">
              Registrar Entrada
            </button>
          </div>
          <div class="col-md-4">
            <button type="submit" class="btn btn-danger w-100"
                    [disabled]="form.invalid">
              Registrar Salida
            </button>
          </div>
        </form>

        <div *ngIf="estancias.length > 0" class="mt-4">
          <h6 class="fw-bold">Estancias del vehículo</h6>
          <div class="table-responsive">
            <table class="table table-sm table-striped align-middle">
              <thead>
                <tr>
                  <th scope="col">ID</th>
                  <th scope="col">Entrada</th>
                  <th scope="col">Salida</th>
                  <th scope="col" class="text-end">Costo</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let e of estancias">
                  <td>{{ e.id }}</td>
                  <td>{{ e.fechaEntrada | date:'medium' }}</td>
                  <td>
                    <ng-container *ngIf="e.fechaSalida; else activa">
                      {{ e.fechaSalida | date:'medium' }}
                    </ng-container>
                    <ng-template #activa>
                      <span class="badge text-bg-success">En estancia</span>
                    </ng-template>
                  </td>
                  <td class="text-end">{{ e.costoTotal ? (e.costoTotal | currency) : '—' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class EstanciaFormComponent {

  form: FormGroup;
  estancias: any[] = [];

  constructor(
    private fb: FormBuilder,
    private parkingService: ParkingService
  ) {
    this.form = this.fb.group({
      placa: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9]{3,10}$/)]]
    });
  }

  registrarEntrada(): void {
    if (this.form.invalid) return;
    const placa = this.form.value.placa.toUpperCase();
    this.parkingService.registrarEntrada(placa).subscribe({
      next: () => {
        alert('Entrada registrada exitosamente');
        this.cargarEstancias(placa);
      },
      error: (err) => {
        alert(err.error?.mensaje || 'Error al registrar entrada');
      }
    });
  }

  registrarSalida(): void {
    if (this.form.invalid) return;
    const placa = this.form.value.placa.toUpperCase();
    this.parkingService.registrarSalida(placa).subscribe({
      next: (res) => {
        alert(`Salida registrada. Costo: $${res.costoTotal}`);
        this.cargarEstancias(placa);
      },
      error: (err) => {
        alert(err.error?.mensaje || 'Error al registrar salida');
      }
    });
  }

  cargarEstancias(placa: string): void {
    this.parkingService.obtenerEstanciasPorPlaca(placa).subscribe({
      next: (data) => this.estancias = data,
      error: (err) => console.error('Error cargando estancias', err)
    });
  }
}