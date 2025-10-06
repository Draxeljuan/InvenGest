package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.RolUsuario;
import com.proyecto.invengest.entities.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest

class UsuarioRepositorioTest {

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    TestEntityManager testEntityManager;


    // Todo lo que este dentro de este metodo se ejecutara antes de
    // las pruebas unitarias
    @BeforeEach
    void setUp() {

        // Dependencias necesarias para Usuario
        RolUsuario rolUsuario = testEntityManager.persistFlushFind(
                RolUsuario.builder()
                        .nombre("Rol de prueba")
                        .build()
        );

        // Creacion del Usuario
        Usuario usuario = testEntityManager.persistFlushFind(
                Usuario.builder()
                        .nombre("Nombre Prueba")
                        .apellido("Apellido Prueba")
                        .idRol(rolUsuario)
                        .nombreUsuario("Usuario Prueba")
                        .contrasena("test password")
                        .build()
        );
    }

    @Test
    public void findByNombreUsuarioFound() {
        Optional<Usuario> encontrado = usuarioRepositorio.findByNombreUsuario("Usuario Prueba");

        assertTrue(encontrado.isPresent(), "El usuario fue encontrado");
        assertEquals("Usuario Prueba", encontrado.get().getNombreUsuario());
    }
}