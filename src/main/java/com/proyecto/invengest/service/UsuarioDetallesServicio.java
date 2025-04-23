package com.proyecto.invengest.service;

import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.repository.UsuarioRepositorio;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetallesServicio implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioDetallesServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        System.out.println("Buscando usuario en la base de datos: " + nombreUsuario);
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> {
                    // Puedes agregar un registro para depuración
                    System.out.println("Usuario no encontrado: " + nombreUsuario);
                    return new UsernameNotFoundException("Usuario con el nombre: " + nombreUsuario + " no encontrado");
                });

        System.out.println("Usuario encontrado: " + usuario.getNombreUsuario());

        // Convertimos el rol a ROLE_Administrador o ROLE_Vendedor
        String rolConPrefijo = "ROLE_" + usuario.getIdRol().getNombre().toUpperCase(); // Esto usará "ROLE_Administrador", etc.
        System.out.println(rolConPrefijo);

        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getContrasena())
                .authorities(rolConPrefijo)
                .build();
    }


}
