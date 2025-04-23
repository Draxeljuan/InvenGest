package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.service.MovimientoInventarioServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/movimiento")
public class MovimientoInventarioControlador {

    private final MovimientoInventarioServicio movimientoInventarioServicio;

    public MovimientoInventarioControlador(MovimientoInventarioServicio movimientoInventarioServicio) {
        this.movimientoInventarioServicio = movimientoInventarioServicio;
    }


    // Obtener todos los movimientos con DTO
    @GetMapping
    public List<MovimientoInventarioDTO> listarMovimientos() {
        return movimientoInventarioServicio.listarMovimientos();
    }

    // Obtener movimiento por Id
    @GetMapping("/{id}")
    public MovimientoInventarioDTO obtenerMovimiento(@PathVariable int id) {
        return movimientoInventarioServicio.obtenerMovimiento(id);
    }

    // Borrar un movimiento
    @DeleteMapping("/{id}")
    public void eliminarMovimiento(@PathVariable int id) {
        movimientoInventarioServicio.eliminarMovimiento(id);
    }

    // Modificar un movimiento
    @PutMapping("/{id}")
    public MovimientoInventarioDTO modificarMovimiento(@PathVariable int id, @RequestBody MovimientoInventarioDTO movimientoInventarioDTO) {
        return movimientoInventarioServicio.modificarMovimiento(id, movimientoInventarioDTO);
    }


}
