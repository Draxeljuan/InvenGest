package com.proyecto.invengest.service.autenticacion;


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IntentosFallidos {

    private final int MAX_INTENTOS = 3;
    private final long BLOQUEO_MILLIS = 120 * 1000; // 20 segs

    private final Map<String, Integer> intentos = new ConcurrentHashMap<>();
    private final Map<String, Long> bloqueados = new ConcurrentHashMap<>();

    // Metodo para validar si un usuario esta bloqueado
    public boolean estaBloqueado(String usuario) {
        Long tiempoBloqueo = bloqueados.get(usuario);
        if (tiempoBloqueo == null) return false;

        if (System.currentTimeMillis() - tiempoBloqueo > BLOQUEO_MILLIS) {
            bloqueados.remove(usuario);
            intentos.remove(usuario);
            return false;
        }
        return true;
    }

    // Metodo para ingresar intentos fallidos
    public void registrarIntentoFallido(String usuario) {
        int nuevoConteo = intentos.getOrDefault(usuario, 0) + 1;
        intentos.put(usuario, nuevoConteo);

        if (nuevoConteo >= MAX_INTENTOS) {
            bloqueados.put(usuario, System.currentTimeMillis());
        }
    }

    // metodo para limpiar intentos
    public void limpiarIntentos(String usuario) {
        intentos.remove(usuario);
        bloqueados.remove(usuario);
    }
}
