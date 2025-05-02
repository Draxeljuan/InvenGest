package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioRepositorio extends JpaRepository<MovimientoInventario, Integer> {
    // Verifica si existe algun movimiento de inventario para el producto
//    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM MovimientoInventario m WHERE m.idProducto.idProducto = :idProducto")
//    boolean existsByIdProducto(@Param("idProducto") String idProducto);
}
