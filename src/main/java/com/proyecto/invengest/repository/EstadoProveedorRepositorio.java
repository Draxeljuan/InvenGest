package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.EstadoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoProveedorRepositorio extends JpaRepository<EstadoProveedor, Integer> {
}
