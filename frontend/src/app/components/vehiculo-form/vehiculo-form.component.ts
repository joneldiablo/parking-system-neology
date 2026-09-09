import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ParkingService } from '../../services/parking.service';

@Component({
  selector: 'app-vehiculo-form',
  template: `
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">Registrar Vehículo</h5>
          </div>
          <div class="card-body">
            <form [formGroup]="form" (ngSubmit)="registrar()">
              <div class="mb-3">
                <label for="placa" class="form-label">Placa del vehículo</label>
                <input
                  id="placa"
                  type="text"
                  class="form-control"
                  formControlName="placa"
                  placeholder="Ex. ABC123"
                  maxlength="10">
                <div *ngIf="form.get('placa')?.invalid && form.get('placa')?.touched"
                     class="text-danger small mt-1">
                  Placa inválida (3-10 caracteres alfanuméricos).
                </div>
              </div>

              <div class="mb-3">
                <label for="tipo" class="form-label">Tipo de vehículo</label>
                <select id="tipo" class="form-select" formControlName="tipo">
                  <option value="">Selecciona un tipo...</option>
                  <option value="oficiales">Oficial</option>
                  <option value="residentes">Residente</option>
                  <option value="no-residentes">No Residente</option>
                </select>
              </div>

              <button type="submit" class="btn btn-primary w-100"
                      [disabled]="form.invalid">
                Registrar Vehículo
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class VehiculoFormComponent {

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private parkingService: ParkingService
  ) {
    this.form = this.fb.group({
      placa: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9]{3,10}$/)]],
      tipo: ['', Validators.required]
    });
  }

  registrar(): void {
    if (this.form.invalid) return;
    const { placa, tipo } = this.form.value;
    this.parkingService.registrarVehiculo(tipo, placa.toUpperCase()).subscribe({
      next: () => {
        alert('Vehículo registrado exitosamente');
        this.form.reset();
      },
      error: (err) => {
        alert(err.error?.mensaje || 'Error al registrar vehículo');
      }
    });
  }
}