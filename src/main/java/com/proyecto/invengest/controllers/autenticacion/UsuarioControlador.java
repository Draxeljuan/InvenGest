package com.proyecto.invengest.controllers.autenticacion;


import com.proyecto.invengest.dto.UsuarioDTO;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.service.autenticacion.UsuarioServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/usuario")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    // Metodo para listar usuarios

    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioServicio.listarUsuarios();
    }

    // Obtener usuario por Id
    @GetMapping("/{id}")
    public UsuarioDTO obtenerUsuario(@PathVariable int id) {
        return usuarioServicio.obtenerUsuario(id);
    }

    // Actualizar informacion usuario
    @PutMapping("/{id}")
    public UsuarioDTO actualizarUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        return usuarioServicio.actualizarUsuario(id, usuario);
    }




}
