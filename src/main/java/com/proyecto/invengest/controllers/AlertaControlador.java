package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.AlertaDTO;
import com.proyecto.invengest.entities.Alerta;
import com.proyecto.invengest.service.AlertaServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/alerta")

public class AlertaControlador {

    private final AlertaServicio alertaservicio;

    public AlertaControlador(AlertaServicio alertaservicio) {
        this.alertaservicio = alertaservicio;
    }

    // Obtener lista de alertas con DTO
    @GetMapping
    public List<AlertaDTO> listarAlertas() {
        return alertaservicio.listarAlertas();
    }

    // Obtener una alerta por su ID
    @GetMapping("/{id}")
    public AlertaDTO obtenerAlerta(@PathVariable int id) {
        return alertaservicio.obtenerAlerta(id);
    }


    // Eliminar una alerta por ID
    @DeleteMapping("/{id}")
    public void eliminarAlerta(@PathVariable int id) {
        alertaservicio.eliminarAlerta(id);
    }


}
