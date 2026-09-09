import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { EstanciaFormComponent } from './estancia-form.component';
import { ParkingService } from '../../services/parking.service';

describe('EstanciaFormComponent', () => {
  let component: EstanciaFormComponent;
  let fixture: ComponentFixture<EstanciaFormComponent>;
  let serviceSpy: jasmine.SpyObj<ParkingService>;

  beforeEach(async () => {
    serviceSpy = jasmine.createSpyObj('ParkingService', [
      'registrarEntrada',
      'registrarSalida',
      'obtenerEstanciasPorPlaca'
    ]);
    serviceSpy.registrarEntrada.and.returnValue(of({}));
    serviceSpy.registrarSalida.and.returnValue(of({ costoTotal: 10 }));
    serviceSpy.obtenerEstanciasPorPlaca.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      declarations: [EstanciaFormComponent],
      imports: [ReactiveFormsModule],
      providers: [{ provide: ParkingService, useValue: serviceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(EstanciaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('rechaza una placa inválida (menos de 3 caracteres)', () => {
    component.form.setValue({ placa: 'AB' });
    expect(component.form.invalid).toBeTrue();
  });

  it('acepta una placa válida alfanumérica', () => {
    component.form.setValue({ placa: 'ABc123' });
    expect(component.form.valid).toBeTrue();
  });

  it('registra entrada enviando la placa en mayúsculas', () => {
    component.form.setValue({ placa: 'abc123' });
    component.registrarEntrada();

    expect(serviceSpy.registrarEntrada).toHaveBeenCalledWith('ABC123');
  });

  it('registra salida y muestra el costo', () => {
    spyOn(window, 'alert');
    component.form.setValue({ placa: 'ABC123' });
    component.registrarSalida();

    expect(serviceSpy.registrarSalida).toHaveBeenCalledWith('ABC123');
    expect(window.alert).toHaveBeenCalledWith('Salida registrada. Costo: $10');
  });

  it('no llama al servicio si el formulario es inválido', () => {
    component.form.setValue({ placa: 'AB' });
    component.registrarEntrada();

    expect(serviceSpy.registrarEntrada).not.toHaveBeenCalled();
  });

  it('muestra el mensaje del backend cuando la entrada falla', () => {
    spyOn(window, 'alert');
    serviceSpy.registrarEntrada.and.returnValue(
      throwError(() => ({ error: { mensaje: 'Vehículo no registrado: XYZ' } }))
    );
    component.form.setValue({ placa: 'XYZ' });
    component.registrarEntrada();

    expect(window.alert).toHaveBeenCalledWith('Vehículo no registrado: XYZ');
  });
});