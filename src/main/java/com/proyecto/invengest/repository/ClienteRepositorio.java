package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
}
