package com.proyecto.invengest.controllers.inventario;


import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.service.inventario.MovimientoInventarioServicio;
import org.springframework.boot.context.properties.bind.DefaultValue;
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

    // Obtener movimientos de inventario del dia actual
    @GetMapping("/hoy")
    public List<MovimientoInventarioDTO> obtenerMovimientosHoy() {
        return movimientoInventarioServicio.obtenerMovimientosDia();
    }

    // Obtener movimientos recientes del dia
    @GetMapping("/recientes")
    public List<MovimientoInventarioDTO> obtenerMovimientosRecientes(@RequestParam(defaultValue = "4") int limite) {
        return movimientoInventarioServicio.obtenerMovimientoRecientes(limite);
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
