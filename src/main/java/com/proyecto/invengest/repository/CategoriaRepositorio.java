package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Integer> {
}
