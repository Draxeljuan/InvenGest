package com.proyecto.invengest.service;

import com.proyecto.invengest.dto.AlertaDTO;
import com.proyecto.invengest.entities.Alerta;
import com.proyecto.invengest.exceptions.AlertaNoEncontradaException;
import com.proyecto.invengest.repository.AlertaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertaServicio {

    public final AlertaRepositorio alertaRepositorio;

    public AlertaServicio(AlertaRepositorio alertaRepositorio) {
        this.alertaRepositorio = alertaRepositorio;
    }

    // Obtener lista de alertas con DTO
    public List<AlertaDTO> listarAlertas() {
        return alertaRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener alertas con Id
    public AlertaDTO obtenerAlerta(@PathVariable int id) {
        Alerta alerta = alertaRepositorio.findById(id)
                .orElseThrow(() -> new AlertaNoEncontradaException("Alerta no encontrada con el ID: " + id));
        return convertirADTO(alerta);
    }

    // Crear una nueva alerta
    public AlertaDTO crearAlerta(@RequestBody Alerta alerta) {
        Alerta nuevaAlerta = alertaRepositorio.save(alerta);
        return convertirADTO(nuevaAlerta);
    }

    // Eliminar una alerta
    public void eliminarAlerta(@PathVariable int id) {
        if(!alertaRepositorio.existsById(id)) {
            throw new AlertaNoEncontradaException("Alerta no encontrada con el ID: " + id);
        }
        alertaRepositorio.deleteById(id);
    }


    // Metodo para convertir a DTO
    private AlertaDTO convertirADTO(Alerta alerta) {
        return new AlertaDTO(
                alerta.getIdAlerta(),
                alerta.getProducto().getIdProducto(),
                alerta.getFecha(),
                alerta.getTipo(),
                alerta.getLeida()
        );
    }

}
