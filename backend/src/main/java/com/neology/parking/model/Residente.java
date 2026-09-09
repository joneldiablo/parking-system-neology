package com.neology.parking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "residentes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Residente {

    @Id
    @Column(length = 10)
    private String placa;

    @Column(name = "tiempo_acumulado_minutos", nullable = false)
    private Double tiempoAcumuladoMinutos = 0.0;

    @Column(name = "ultimo_reinicio")
    private LocalDateTime ultimoReinicio;
}
