package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioRepositorio extends JpaRepository<MovimientoInventario, Integer> {
    // Verifica si existe algun movimiento de inventario para el producto
    boolean existsByIdProducto(String idProducto);
}
