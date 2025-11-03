package com.proyecto.invengest.controllers.inventario;


import com.proyecto.invengest.dto.EstadoProveedorDTO;
import com.proyecto.invengest.service.inventario.EstadoProveedorServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estados-proveedor")
public class EstadoProveedorControlador {

    private final EstadoProveedorServicio estadoProveedorServicio;

    public EstadoProveedorControlador(EstadoProveedorServicio estadoProveedorServicio) {
        this.estadoProveedorServicio = estadoProveedorServicio;
    }

    // Obtener estados de proveedor con DTO
    @GetMapping
    public List<EstadoProveedorDTO> obtenerEstadoProveedor(){
        return estadoProveedorServicio.listarEstadosProveedores();
    }

    // Obtener un estado de proveedor por ID
    @GetMapping("/{id}")
    public EstadoProveedorDTO obtenerEstadoProveedorPorId(@PathVariable int id){
        return estadoProveedorServicio.obtenerEstadoProveedorPorId(id);
    }
}
