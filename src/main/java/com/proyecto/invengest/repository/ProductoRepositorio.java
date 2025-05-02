package com.proyecto.invengest.repository;


import com.proyecto.invengest.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepositorio extends JpaRepository<Producto, String> {
    @Query("SELECT p FROM Producto p WHERE p.idEstado.id <> :idEstado")
    List<Producto> findAllByIdEstadoNot(@Param("idEstado") int idEstado);
}
