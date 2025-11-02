package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Integer> {

}
