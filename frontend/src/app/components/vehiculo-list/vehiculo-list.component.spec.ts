import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { VehiculoListComponent } from './vehiculo-list.component';
import { ParkingService, Vehiculo } from '../../services/parking.service';

describe('VehiculoListComponent', () => {
  let component: VehiculoListComponent;
  let fixture: ComponentFixture<VehiculoListComponent>;
  let serviceSpy: jasmine.SpyObj<ParkingService>;

  const vehiculos: Vehiculo[] = [
    { placa: 'OFF123', tipo: 'OFICIAL' },
    { placa: 'RES001', tipo: 'RESIDENTE' },
    { placa: 'NR001', tipo: 'NO_RESIDENTE' }
  ];

  beforeEach(async () => {
    serviceSpy = jasmine.createSpyObj('ParkingService', ['obtenerVehiculos']);
    serviceSpy.obtenerVehiculos.and.returnValue(of(vehiculos));

    await TestBed.configureTestingModule({
      declarations: [VehiculoListComponent],
      providers: [{ provide: ParkingService, useValue: serviceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(VehiculoListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('carga y muestra todos los vehículos al iniciar', () => {
    expect(component.vehiculos.length).toBe(3);
    const filas = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(filas.length).toBe(3);
    const texto = fixture.nativeElement.textContent;
    expect(texto).toContain('OFF123');
    expect(texto).toContain('RESIDENTE');
    expect(texto).toContain('NO_RESIDENTE');
  });

  it('filtra la lista por placa', () => {
    component.filtrar({ target: { value: 'nr' } } as unknown as Event);
    expect(component.vehiculosFiltrados.map(v => v.placa)).toEqual(['NR001']);

    component.filtrar({ target: { value: 'nada-que-ver' } } as unknown as Event);
    expect(component.vehiculosFiltrados.length).toBe(0);
  });

  it('muestra mensaje cuando no hay vehículos', () => {
    serviceSpy.obtenerVehiculos.and.returnValue(of([]));
    component.cargarVehiculos();
    fixture.detectChanges();

    const cuerpo = fixture.nativeElement.querySelector('tbody');
    expect(cuerpo.textContent).toContain('No hay vehículos registrados');
  });
});