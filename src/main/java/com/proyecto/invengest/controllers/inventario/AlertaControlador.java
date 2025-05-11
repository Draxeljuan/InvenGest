package com.proyecto.invengest.controllers.inventario;


import com.proyecto.invengest.dto.AlertaDTO;
import com.proyecto.invengest.service.inventario.AlertaServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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

    @GetMapping("/con-producto")
    public List<Map<String, Object>> listarAlertasConProducto() {
        return alertaservicio.listarAlertasConProducto();
    }

    // Obtener una alerta por su ID
    @GetMapping("/{id}")
    public AlertaDTO obtenerAlerta(@PathVariable int id) {
        return alertaservicio.obtenerAlerta(id);
    }

    // Eliminar Alertas Innecesarias
    @DeleteMapping("/limpiar")
    public ResponseEntity<String> limpiarAlertasInnecesarias() {
        alertaservicio.limpiarAlertasInnecesarias();
        return ResponseEntity.ok("Alertas innecesarias limpiadas correctamente");
    }

    // Eliminar una alerta por ID
    @DeleteMapping("/{id}")
    public void eliminarAlerta(@PathVariable int id) {
        alertaservicio.eliminarAlerta(id);
    }


}
