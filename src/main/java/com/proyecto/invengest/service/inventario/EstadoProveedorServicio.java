package com.proyecto.invengest.service.inventario;


import com.proyecto.invengest.dto.EstadoProveedorDTO;
import com.proyecto.invengest.entities.EstadoProveedor;
import com.proyecto.invengest.repository.EstadoProveedorRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoProveedorServicio {


    private final EstadoProveedorRepositorio estadoProveedorRepositorio;

    public EstadoProveedorServicio(EstadoProveedorRepositorio estadoProveedorRepositorio) {

        this.estadoProveedorRepositorio = estadoProveedorRepositorio;
    }

    // Listar los estados de proveedores
    public List<EstadoProveedorDTO> listarEstadosProveedores() {
        return estadoProveedorRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // obtener estados por id
    public EstadoProveedorDTO obtenerEstadoProveedorPorId(@PathVariable int id){
        EstadoProveedor estadoProveedor = estadoProveedorRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con id " + id));
        return convertirADTO(estadoProveedor);
    }

    private EstadoProveedorDTO convertirADTO (EstadoProveedor estadoProveedor) {
        return new EstadoProveedorDTO(
                estadoProveedor.getId()
                , estadoProveedor.getNombre()
        );
    }
}
