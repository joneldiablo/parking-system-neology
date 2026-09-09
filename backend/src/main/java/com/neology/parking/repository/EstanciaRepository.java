package com.neology.parking.repository;

import com.neology.parking.model.Estancia;
import com.neology.parking.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstanciaRepository extends JpaRepository<Estancia, Long> {

    Optional<Estancia> findFirstByVehiculoAndFechaSalidaIsNull(Vehiculo vehiculo);

    List<Estancia> findByVehiculoPlaca(String placa);

    @Query("SELECT e FROM Estancia e WHERE e.fechaSalida IS NOT NULL")
    List<Estancia> findAllFinalizadas();
}
