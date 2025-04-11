package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, String> {
}
