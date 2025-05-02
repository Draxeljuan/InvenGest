package com.proyecto.invengest.controllers;

import com.proyecto.invengest.dto.EstadoProductoDTO;
import com.proyecto.invengest.service.EstadoProductoServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estados-producto")
public class EstadoProductoControlador {
    private final EstadoProductoServicio estadoProductoServicio;

    public EstadoProductoControlador(EstadoProductoServicio estadoProductoServicio) {
        this.estadoProductoServicio = estadoProductoServicio;
    }

    // Obtener lista de estados con DTO
    @GetMapping
    public List<EstadoProductoDTO> listarEstados() {
        return estadoProductoServicio.listarEstados();
    }

    // Obtener estado por ID con DTO
    @GetMapping("/{id}")
    public EstadoProductoDTO obtenerEstado(@PathVariable int id) {
        return estadoProductoServicio.obtenerEstado(id);
    }
}
