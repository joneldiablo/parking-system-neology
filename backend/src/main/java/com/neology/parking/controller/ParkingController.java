package com.neology.parking.controller;

import com.neology.parking.dto.*;
import com.neology.parking.model.Vehiculo;
import com.neology.parking.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/neo")
@RequiredArgsConstructor
@Tag(name = "Parking System API", description = "API para gestión de estacionamiento")
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/vehiculos/oficiales")
    @Operation(summary = "Registrar vehículo oficial")
    public ResponseEntity<MensajeResponseDTO> registrarVehiculoOficial(@Valid @RequestBody VehiculoDTO dto) {
        dto.setTipo("OFICIAL");
        parkingService.registrarVehiculo(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensajeResponseDTO("Vehículo oficial registrado exitosamente", true));
    }

    @PostMapping("/vehiculos/residentes")
    @Operation(summary = "Registrar vehículo residente")
    public ResponseEntity<MensajeResponseDTO> registrarVehiculoResidente(@Valid @RequestBody VehiculoDTO dto) {
        dto.setTipo("RESIDENTE");
        parkingService.registrarVehiculo(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensajeResponseDTO("Vehículo residente registrado exitosamente", true));
    }

    @PostMapping("/vehiculos/no-residentes")
    @Operation(summary = "Registrar vehículo no residente")
    public ResponseEntity<MensajeResponseDTO> registrarVehiculoNoResidente(@Valid @RequestBody VehiculoDTO dto) {
        dto.setTipo("NO_RESIDENTE");
        parkingService.registrarVehiculo(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensajeResponseDTO("Vehículo no residente registrado exitosamente", true));
    }

    @PostMapping("/estancias/entrada")
    @Operation(summary = "Registrar entrada de vehículo")
    public ResponseEntity<?> registrarEntrada(@Valid @RequestBody EstanciaDTO dto) {
        try {
            EstanciaResponseDTO estancia = parkingService.registrarEntrada(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(estancia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponseDTO(e.getMessage(), false));
        }
    }

    @PostMapping("/estancias/salida")
    @Operation(summary = "Registrar salida de vehículo")
    public ResponseEntity<?> registrarSalida(@Valid @RequestBody EstanciaDTO dto) {
        try {
            EstanciaResponseDTO estancia = parkingService.registrarSalida(dto);
            return ResponseEntity.ok(estancia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponseDTO(e.getMessage(), false));
        }
    }

    @GetMapping("/residentes/pagos")
    @Operation(summary = "Generar informe de pagos de residentes")
    public ResponseEntity<List<PagoResidenteDTO>> generarInformePagos() {
        return ResponseEntity.ok(parkingService.generarInformePagos());
    }

    @PostMapping("/mes/iniciar")
    @Operation(summary = "Reiniciar mes (limpiar estancias y residentes)")
    public ResponseEntity<MensajeResponseDTO> reiniciarMes() {
        parkingService.reiniciarMes();
        return ResponseEntity.ok(new MensajeResponseDTO("Mes reiniciado exitosamente", true));
    }

    @GetMapping("/vehiculos")
    @Operation(summary = "Obtener todos los vehículos")
    public ResponseEntity<List<Vehiculo>> obtenerTodosLosVehiculos() {
        return ResponseEntity.ok(parkingService.obtenerTodosLosVehiculos());
    }

    @GetMapping("/estancias")
    @Operation(summary = "Obtener todas las estancias")
    public ResponseEntity<List<EstanciaResponseDTO>> obtenerTodasLasEstancias() {
        return ResponseEntity.ok(parkingService.obtenerTodasLasEstancias());
    }

    @GetMapping("/estancias/{placa}")
    @Operation(summary = "Obtener estancias por placa")
    public ResponseEntity<List<EstanciaResponseDTO>> obtenerEstanciasPorVehiculo(@PathVariable String placa) {
        return ResponseEntity.ok(parkingService.obtenerEstanciasPorVehiculo(placa));
    }
}
