package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoMovimientoRepositorio extends JpaRepository<TipoMovimiento, Integer> {

    // Buscar un Tipo de Movimiento por su nombre
    Optional<TipoMovimiento> findByNombre(String nombre);
}
