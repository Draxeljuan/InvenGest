package com.proyecto.invengest.service.autenticacion;

import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.exceptions.ApiException;
import com.proyecto.invengest.repository.UsuarioRepositorio;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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

        try {
            Usuario usuario = usuarioRepositorio.findByNombreUsuario(nombreUsuario)
                    .orElseThrow(() -> {
                        System.out.println("Usuario no encontrado: " + nombreUsuario);
                        return new UsernameNotFoundException("Usuario con el nombre: " + nombreUsuario + " no encontrado");
                    });

            System.out.println("Usuario encontrado: " + usuario.getNombreUsuario());

            // Convertimos el rol a ROLE_Administrador o ROLE_Vendedor
            String rolConPrefijo = "ROLE_" + usuario.getIdRol().getNombre().toUpperCase();
            System.out.println(rolConPrefijo);

            return User.builder()
                    .username(usuario.getNombreUsuario())
                    .password(usuario.getContrasena())
                    .authorities(rolConPrefijo)
                    .build();

        } catch (PessimisticLockException | LockTimeoutException e) {  // Detectar bloqueo de tabla
            System.out.println("⚠ ERROR: La base de datos está bloqueada.");
            throw new ApiException(4081, "⚠ La base de datos está bloqueada. Intente más tarde.", HttpStatus.REQUEST_TIMEOUT);
        } catch (DataAccessException e) {
            System.out.println("⚠ ERROR: Fallo en la conexión con la base de datos.");
            throw new ApiException(5001, "⚠ Error de conexión con la base de datos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
