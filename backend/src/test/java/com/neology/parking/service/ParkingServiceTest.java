package com.neology.parking.service;

import com.neology.parking.dto.EstanciaDTO;
import com.neology.parking.dto.EstanciaResponseDTO;
import com.neology.parking.dto.PagoResidenteDTO;
import com.neology.parking.model.Estancia;
import com.neology.parking.model.Residente;
import com.neology.parking.model.Vehiculo;
import com.neology.parking.repository.EstanciaRepository;
import com.neology.parking.repository.ResidenteRepository;
import com.neology.parking.repository.VehiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock private VehiculoRepository vehiculoRepository;
    @Mock private EstanciaRepository estanciaRepository;
    @Mock private ResidenteRepository residenteRepository;

    @InjectMocks private ParkingService service;

    private Vehiculo vehiculo(String placa, Vehiculo.TipoVehiculo tipo) {
        return new Vehiculo(placa, tipo);
    }

    private Estancia estanciaActiva(String placa, Vehiculo.TipoVehiculo tipo, LocalDateTime entrada) {
        return new Estancia(1L, vehiculo(placa, tipo), entrada, null, null);
    }

    // ── Entrada ──────────────────────────────────────────────────────────────

    @Test
    void entrada_conVehiculoNoRegistrado_lanzaExcepcion() {
        when(vehiculoRepository.findById("ZZZ1")).thenReturn(Optional.empty());

        EstanciaDTO dto = new EstanciaDTO();
        dto.setPlaca("zzz1");

        assertThatThrownBy(() -> service.registrarEntrada(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Vehículo no registrado");

        verify(estanciaRepository, never()).save(any());
    }

    @Test
    void entrada_conEstanciaActivaExistente_lanzaExcepcion() {
        Vehiculo v = vehiculo("ABC1", Vehiculo.TipoVehiculo.NO_RESIDENTE);
        when(vehiculoRepository.findById("ABC1")).thenReturn(Optional.of(v));
        // Se simulé que nunca guardamos la preexistente la estancia activa
        when(estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(v))
                .thenReturn(Optional.of(estanciaActiva("ABC1", Vehiculo.TipoVehiculo.NO_RESIDENTE, LocalDateTime.now().minusHours(1))));

        EstanciaDTO dto = new EstanciaDTO();
        dto.setPlaca("ABC1");

        assertThatThrownBy(() -> service.registrarEntrada(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya tiene una estancia activa");

        verify(estanciaRepository, never()).save(any());
    }

    @Test
    void entrada_conVehiculoRegistrado_guardaEstancia() {
        Vehiculo v = vehiculo("NR001", Vehiculo.TipoVehiculo.NO_RESIDENTE);
        when(vehiculoRepository.findById("NR001")).thenReturn(Optional.of(v));
        when(estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(v)).thenReturn(Optional.empty());
        when(estanciaRepository.save(any(Estancia.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        EstanciaDTO dto = new EstanciaDTO();
        dto.setPlaca("nr001");

        EstanciaResponseDTO res = service.registrarEntrada(dto);

        ArgumentCaptor<Estancia> captor = ArgumentCaptor.forClass(Estancia.class);
        verify(estanciaRepository).save(captor.capture());

        Estancia guardada = captor.getValue();
        assertThat(guardada.getVehiculo().getPlaca()).isEqualTo("NR001");
        assertThat(guardada.getFechaEntrada()).isNotNull();
        assertThat(guardada.getFechaSalida()).isNull();

        assertThat(res.getPlaca()).isEqualTo("NR001");
        assertThat(res.getTipoVehiculo()).isEqualTo("NO_RESIDENTE");
        assertThat(res.getFechaSalida()).isNull();
    }

    // ── Salida ───────────────────────────────────────────────────────────────

    @Test
    void salida_sinEstanciaActiva_lanzaExcepcion() {
        Vehiculo v = vehiculo("ABC2", Vehiculo.TipoVehiculo.NO_RESIDENTE);
        when(vehiculoRepository.findById("ABC2")).thenReturn(Optional.of(v));
        when(estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(v)).thenReturn(Optional.empty());

        EstanciaDTO dto = new EstanciaDTO();
        dto.setPlaca("ABC2");

        assertThatThrownBy(() -> service.registrarSalida(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hay estancia activa");

        verify(estanciaRepository, never()).save(any());
    }

    @Test
    void salida_noResidente_calculaCostoPorMinuto() {
        Vehiculo v = vehiculo("NR001", Vehiculo.TipoVehiculo.NO_RESIDENTE);
        Estancia activa = estanciaActiva("NR001", Vehiculo.TipoVehiculo.NO_RESIDENTE,
                LocalDateTime.now().minusMinutes(90));
        when(vehiculoRepository.findById("NR001")).thenReturn(Optional.of(v));
        when(estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(v)).thenReturn(Optional.of(activa));
        when(estanciaRepository.save(any(Estancia.class))).thenAnswer(inv -> inv.getArgument(0));

        EstanciaDTO dto = new EstanciaDTO();
        dto.setPlaca("NR001");

        EstanciaResponseDTO res = service.registrarSalida(dto);

        assertThat(res.getCostoTotal()).isEqualTo(90 * 0.50);
        assertThat(res.getDuracionMinutos()).isEqualTo(90.0);
        assertThat(res.getFechaSalida()).isNotNull();
        assertThat(activa.getCostoTotal()).isEqualTo(90 * 0.50);
        verify(residenteRepository, never()).save(any());
    }

    @Test
    void salida_oficial_costoCero() {
        Vehiculo v = vehiculo("OFF1", Vehiculo.TipoVehiculo.OFICIAL);
        Estancia activa = estanciaActiva("OFF1", Vehiculo.TipoVehiculo.OFICIAL,
                LocalDateTime.now().minusHours(8));
        when(vehiculoRepository.findById("OFF1")).thenReturn(Optional.of(v));
        when(estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(v)).thenReturn(Optional.of(activa));
        when(estanciaRepository.save(any(Estancia.class))).thenAnswer(inv -> inv.getArgument(0));

        EstanciaDTO dto = new EstanciaDTO();
        dto.setPlaca("OFF1");

        EstanciaResponseDTO res = service.registrarSalida(dto);

        assertThat(res.getCostoTotal()).isZero();
        verify(residenteRepository, never()).save(any());
    }

    @Test
    void salida_residente_acumulaTiempoYSinCosto() {
        Vehiculo v = vehiculo("RES1", Vehiculo.TipoVehiculo.RESIDENTE);
        Estancia activa = estanciaActiva("RES1", Vehiculo.TipoVehiculo.RESIDENTE,
                LocalDateTime.now().minusMinutes(60));
        when(vehiculoRepository.findById("RES1")).thenReturn(Optional.of(v));
        when(estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(v)).thenReturn(Optional.of(activa));
        when(estanciaRepository.save(any(Estancia.class))).thenAnswer(inv -> inv.getArgument(0));
        when(residenteRepository.findById("RES1")).thenReturn(Optional.empty());

        EstanciaDTO dto = new EstanciaDTO();
        dto.setPlaca("RES1");

        EstanciaResponseDTO res = service.registrarSalida(dto);

        assertThat(res.getCostoTotal()).isZero();

        ArgumentCaptor<Residente> captor = ArgumentCaptor.forClass(Residente.class);
        verify(residenteRepository).save(captor.capture());
        assertThat(captor.getValue().getTiempoAcumuladoMinutos()).isEqualTo(60.0);
    }

    @Test
    void salida_residente_acumulaSobreTiempoPrevIo() {
        Vehiculo v = vehiculo("RES1", Vehiculo.TipoVehiculo.RESIDENTE);
        Estancia activa = estanciaActiva("RES1", Vehiculo.TipoVehiculo.RESIDENTE,
                LocalDateTime.now().minusMinutes(60));
        when(vehiculoRepository.findById("RES1")).thenReturn(Optional.of(v));
        when(estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(v)).thenReturn(Optional.of(activa));
        when(estanciaRepository.save(any(Estancia.class))).thenAnswer(inv -> inv.getArgument(0));
        when(residenteRepository.findById("RES1"))
                .thenReturn(Optional.of(new Residente("RES1", 120.0, LocalDateTime.now())));

        EstanciaDTO dto = new EstanciaDTO();
        dto.setPlaca("RES1");

        service.registrarSalida(dto);

        ArgumentCaptor<Residente> captor = ArgumentCaptor.forClass(Residente.class);
        verify(residenteRepository).save(captor.capture());
        assertThat(captor.getValue().getTiempoAcumuladoMinutos()).isEqualTo(180.0);
    }

    // ── Reporte de pagos ─────────────────────────────────────────────────────

    @Test
    void informePagos_calculaMontoConTarifaResidente() {
        when(residenteRepository.findAll()).thenReturn(List.of(
                new Residente("RES1", 100.0, LocalDateTime.now()),
                new Residente("RES2", 0.0, LocalDateTime.now())
        ));

        List<PagoResidenteDTO> pagos = service.generarInformePagos();

        assertThat(pagos).hasSize(2);
        assertThat(pagos.get(0).getMontoTotal()).isEqualTo(100 * 0.05);
        assertThat(pagos.get(1).getMontoTotal()).isEqualTo(0.0);
    }

    // ── Estancias por vehículo ───────────────────────────────────────────────

    @Test
    void estanciasPorVehiculo_mapeaActivasYFinalizadas() {
        Vehiculo v = vehiculo("NR001", Vehiculo.TipoVehiculo.NO_RESIDENTE);
        LocalDateTime entrada = LocalDateTime.now().minusMinutes(30);
        when(estanciaRepository.findByVehiculoPlaca("NR001")).thenReturn(List.of(
                estanciaActiva("NR001", Vehiculo.TipoVehiculo.NO_RESIDENTE, entrada),
                new Estancia(2L, v, entrada, entrada.plusMinutes(30), 15.0)
        ));

        List<EstanciaResponseDTO> estancias = service.obtenerEstanciasPorVehiculo("nr001");

        assertThat(estancias).hasSize(2);
        assertThat(estancias.get(0).getFechaSalida()).isNull();
        assertThat(estancias.get(0).getDuracionMinutos()).isZero();
        assertThat(estancias.get(1).getCostoTotal()).isEqualTo(15.0);
        assertThat(estancias.get(1).getDuracionMinutos()).isEqualTo(30.0);
    }

    // ── Reinicio de mes ──────────────────────────────────────────────────────

    @Test
    void reiniciarMes_borraEstanciasYResidentes() {
        service.reiniciarMes();

        verify(estanciaRepository).deleteAll();
        verify(residenteRepository).deleteAll();
    }
}