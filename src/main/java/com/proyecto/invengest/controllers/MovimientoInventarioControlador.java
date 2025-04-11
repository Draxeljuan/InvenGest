package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.entities.MovimientoInventario;
import com.proyecto.invengest.service.MovimientoInventarioServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/movimiento")
public class MovimientoInventarioControlador {

    private final MovimientoInventarioServicio MovimientoInventarioServicio;

    public MovimientoInventarioControlador(MovimientoInventarioServicio movimientoInventarioServicio) {
        MovimientoInventarioServicio = movimientoInventarioServicio;
    }


    // Obtener todos los movimientos con DTO
    @GetMapping
    public List<MovimientoInventarioDTO> listarMovimientos() {
        return MovimientoInventarioServicio.listarMovimientos();
    }

    // Obtener movimiento por Id
    @GetMapping("/{id}")
    public MovimientoInventarioDTO obtenerMovimiento(@PathVariable int id) {
        return MovimientoInventarioServicio.obtenerMovimiento(id);
    }

    // Crear un nuevo movimiento
    @PostMapping
    public MovimientoInventarioDTO crearMovimiento(@RequestBody MovimientoInventario movimientoInventario) {
        return MovimientoInventarioServicio.crearMovimiento(movimientoInventario);
    }

    // Borrar un movimiento
    @DeleteMapping("/{id}")
    public void eliminarMovimiento(@PathVariable int id) {
        MovimientoInventarioServicio.eliminarMovimiento(id);
    }

    // Modificar un movimiento
    @PutMapping("/{id}")
    public MovimientoInventarioDTO modificarMovimiento(@PathVariable int id, @RequestBody MovimientoInventario movimientoInventario) {
        return MovimientoInventarioServicio.modificarMovimiento(id, movimientoInventario);
    }


}
