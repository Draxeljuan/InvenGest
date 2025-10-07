package com.proyecto.invengest.repository;


import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductoRepositorio extends JpaRepository<Producto, String> {

    // Retorna todos los productos que tengan un estado diferente al proporcionado
    @Query("SELECT p FROM Producto p WHERE p.idEstado.id <> :idEstado")
    List<Producto> findAllByIdEstadoNot(@Param("idEstado") int idEstado);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM Producto p WHERE p.fechaIngreso BETWEEN :fechaInicio AND :fechaFin")
    List<Producto> obtenerProductosPorFecha(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Producto p WHERE p.idProducto = :idProducto")
    Optional<Producto> obtenerProductoPorId(@Param("idProducto") String idProducto);
}
