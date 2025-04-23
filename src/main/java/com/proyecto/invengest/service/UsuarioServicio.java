package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.UsuarioDTO;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.exceptions.UsuarioNoEncontradoException;
import com.proyecto.invengest.repository.UsuarioRepositorio;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }


    public UsuarioDTO registrarUsuario(Usuario usuario) {
        throw new UnsupportedOperationException("La creación de nuevos usuarios está deshabilitada.");
    }

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO obtenerUsuario(@PathVariable int id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con el ID: " + id));
        return convertirADTO(usuario);
    }

    public UsuarioDTO actualizarUsuario(@PathVariable int id, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        actualizarUsuario(usuario, usuarioActualizado);
        return convertirADTO(usuarioRepositorio.save(usuarioActualizado));
    }

    private UsuarioDTO convertirADTO (Usuario usuario){
        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getUltimoAcceso(),
                usuario.getIdRol().getNombre(),
                usuario.getNombreUsuario()
        );
    }

    private void actualizarUsuario(Usuario origen, Usuario destino){
        destino.setNombre(origen.getNombre());
        destino.setEmail(origen.getEmail());
        destino.setTelefono(origen.getTelefono());
        destino.setUltimoAcceso(origen.getUltimoAcceso());
        destino.setIdRol(origen.getIdRol());
        destino.setNombreUsuario(origen.getNombreUsuario());
    }

}
