package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioRepositorio extends JpaRepository<MovimientoInventario, Integer> {
}
