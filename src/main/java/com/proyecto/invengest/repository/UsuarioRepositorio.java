package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNombreUsuario(String nombre);
}
