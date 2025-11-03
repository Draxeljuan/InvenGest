package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Integer> {
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
}
