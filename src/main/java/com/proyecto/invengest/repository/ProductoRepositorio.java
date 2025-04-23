package com.proyecto.invengest.repository;


import com.proyecto.invengest.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepositorio extends JpaRepository<Producto, String> {
    List<Producto> findAllByIdEstadoNot(int idEstado);
}
