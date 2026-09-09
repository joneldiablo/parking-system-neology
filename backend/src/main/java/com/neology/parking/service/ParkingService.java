package com.neology.parking.service;

import com.neology.parking.dto.*;
import com.neology.parking.model.Estancia;
import com.neology.parking.model.Residente;
import com.neology.parking.model.Vehiculo;
import com.neology.parking.repository.EstanciaRepository;
import com.neology.parking.repository.ResidenteRepository;
import com.neology.parking.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final VehiculoRepository vehiculoRepository;
    private final EstanciaRepository estanciaRepository;
    private final ResidenteRepository residenteRepository;

    private static final double TARIFA_RESIDENTE = 0.05;
    private static final double TARIFA_NO_RESIDENTE = 0.50;

    @Transactional
    public Vehiculo registrarVehiculo(VehiculoDTO dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(dto.getPlaca().toUpperCase());
        vehiculo.setTipo(Vehiculo.TipoVehiculo.valueOf(dto.getTipo().toUpperCase()));
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public EstanciaResponseDTO registrarEntrada(EstanciaDTO dto) {
        String placa = dto.getPlaca().toUpperCase();
        Vehiculo vehiculo = vehiculoRepository.findById(placa)
                .orElseThrow(() -> new RuntimeException("Vehículo no registrado: " + placa));

        estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(vehiculo)
                .ifPresent(e -> {
                    throw new RuntimeException("El vehículo ya tiene una estancia activa");
                });

        Estancia estancia = new Estancia();
        estancia.setVehiculo(vehiculo);
        estancia.setFechaEntrada(LocalDateTime.now());
        estanciaRepository.save(estancia);

        return convertirAEstanciaResponse(estancia);
    }

    @Transactional
    public EstanciaResponseDTO registrarSalida(EstanciaDTO dto) {
        String placa = dto.getPlaca().toUpperCase();
        Vehiculo vehiculo = vehiculoRepository.findById(placa)
                .orElseThrow(() -> new RuntimeException("Vehículo no registrado: " + placa));

        Estancia estancia = estanciaRepository.findFirstByVehiculoAndFechaSalidaIsNull(vehiculo)
                .orElseThrow(() -> new RuntimeException("No hay estancia activa para este vehículo"));

        LocalDateTime ahora = LocalDateTime.now();
        estancia.setFechaSalida(ahora);

        long minutos = ChronoUnit.MINUTES.between(estancia.getFechaEntrada(), ahora);
        double costo = calcularCosto(vehiculo.getTipo(), minutos, vehiculo.getPlaca());
        estancia.setCostoTotal(costo);

        estanciaRepository.save(estancia);

        if (vehiculo.getTipo() == Vehiculo.TipoVehiculo.RESIDENTE) {
            actualizarTiempoResidente(vehiculo.getPlaca(), minutos);
        }

        return convertirAEstanciaResponse(estancia);
    }

    private double calcularCosto(Vehiculo.TipoVehiculo tipo, long minutos, String placa) {
        return switch (tipo) {
            case OFICIAL -> 0.0;
            case RESIDENTE -> 0.0;
            case NO_RESIDENTE -> minutos * TARIFA_NO_RESIDENTE;
        };
    }

    private void actualizarTiempoResidente(String placa, long minutos) {
        Residente residente = residenteRepository.findById(placa)
                .orElse(new Residente(placa, 0.0, LocalDateTime.now()));

        residente.setTiempoAcumuladoMinutos(
                residente.getTiempoAcumuladoMinutos() + minutos
        );
        residenteRepository.save(residente);
    }

    @Transactional(readOnly = true)
    public List<EstanciaResponseDTO> obtenerEstanciasPorVehiculo(String placa) {
        return estanciaRepository.findByVehiculoPlaca(placa.toUpperCase())
                .stream()
                .map(this::convertirAEstanciaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PagoResidenteDTO> generarInformePagos() {
        return residenteRepository.findAll()
                .stream()
                .map(r -> new PagoResidenteDTO(
                        r.getPlaca(),
                        r.getTiempoAcumuladoMinutos(),
                        r.getTiempoAcumuladoMinutos() * TARIFA_RESIDENTE
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void reiniciarMes() {
        estanciaRepository.deleteAll();
        residenteRepository.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> obtenerTodosLosVehiculos() {
        return vehiculoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<EstanciaResponseDTO> obtenerTodasLasEstancias() {
        return estanciaRepository.findAll()
                .stream()
                .map(this::convertirAEstanciaResponse)
                .collect(Collectors.toList());
    }

    private EstanciaResponseDTO convertirAEstanciaResponse(Estancia estancia) {
        long minutos = 0;
        if (estancia.getFechaSalida() != null) {
            minutos = ChronoUnit.MINUTES.between(estancia.getFechaEntrada(), estancia.getFechaSalida());
        }
        return new EstanciaResponseDTO(
                estancia.getId(),
                estancia.getVehiculo().getPlaca(),
                estancia.getVehiculo().getTipo().name(),
                estancia.getFechaEntrada(),
                estancia.getFechaSalida(),
                (double) minutos,
                estancia.getCostoTotal()
        );
    }
}
