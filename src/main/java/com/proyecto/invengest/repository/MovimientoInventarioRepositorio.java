package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoInventarioRepositorio extends JpaRepository<MovimientoInventario, Integer> {

    // Obtener los movimientos de inventario de un dia en concreto
    @Query("SELECT m FROM MovimientoInventario m WHERE m.fechaMovimiento = :fecha")
    List<MovimientoInventario> movimientosDia(@Param("fecha") LocalDate fecha);

    // Filtrar los ultimos movimientos
    @Query("SELECT m FROM MovimientoInventario m ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> ultimosMovimientos(Pageable pageable);
}
