package com.proyecto.invengest.service;

import com.proyecto.invengest.dto.EstadoProductoDTO;
import com.proyecto.invengest.entities.EstadoProducto;
import com.proyecto.invengest.repository.EstadoProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoProductoServicio {
    private final EstadoProductoRepositorio estadoProductoRepositorio;

    public EstadoProductoServicio(EstadoProductoRepositorio estadoProductoRepositorio) {
        this.estadoProductoRepositorio = estadoProductoRepositorio;
    }

    // Listar todos los estados con DTO
    public List<EstadoProductoDTO> listarEstados() {
        return estadoProductoRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener estado por ID con DTO
    public EstadoProductoDTO obtenerEstado(@PathVariable int id) {
        EstadoProducto estado = estadoProductoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + id));
        return convertirADTO(estado);
    }

    // Metodo para convertir a DTO
    private EstadoProductoDTO convertirADTO(EstadoProducto estado) {
        return new EstadoProductoDTO(estado.getId(), estado.getNombre());
    }
}
