package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.entities.MovimientoInventario;
import com.proyecto.invengest.exceptions.MovimientoInventarioNoEncontradoException;
import com.proyecto.invengest.repository.MovimientoInventarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoInventarioServicio {

    private final MovimientoInventarioRepositorio movimientoInventarioRepositorio;

    public MovimientoInventarioServicio(MovimientoInventarioRepositorio movimientoInventarioRepositorio) {
        this.movimientoInventarioRepositorio = movimientoInventarioRepositorio;
    }

    // Obtener todos los movimiento con DTO
    public List<MovimientoInventarioDTO> listarMovimientos() {
        return movimientoInventarioRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener movimiento por Id
    public MovimientoInventarioDTO obtenerMovimiento(@PathVariable int id) {
        MovimientoInventario movimiento = movimientoInventarioRepositorio.findById(id)
                .orElseThrow(() -> new MovimientoInventarioNoEncontradoException("Movimiento no encontrado con el ID: " + id + ""));
        return convertirADTO(movimiento);
    }

    // Crear un nuevo movimiento
    public MovimientoInventarioDTO crearMovimiento(@RequestBody MovimientoInventario movimientoInventario) {
        MovimientoInventario nuevoMovimiento = movimientoInventarioRepositorio.save(movimientoInventario);
        return convertirADTO(nuevoMovimiento);
    }

    // Borrar un movimiento
    public void eliminarMovimiento(@PathVariable int id) {
        if(!movimientoInventarioRepositorio.existsById(id)) {
            throw new MovimientoInventarioNoEncontradoException("Movimiento no encontrado con el ID: " + id + "");
        }
        movimientoInventarioRepositorio.deleteById(id);
    }

    // Modificar un movimiento
    public MovimientoInventarioDTO modificarMovimiento(@PathVariable int id, @RequestBody MovimientoInventario movimientoInventario) {
        MovimientoInventario movimiento = movimientoInventarioRepositorio.findById(id).orElseThrow(() -> new MovimientoInventarioNoEncontradoException("Movimiento no encontrado"));
        movimiento.setTipoMovimiento(movimientoInventario.getTipoMovimiento());
        movimiento.setCantidad(movimientoInventario.getCantidad());
        movimiento.setObservacion(movimientoInventario.getObservacion());
        return convertirADTO(movimientoInventarioRepositorio.save(movimiento));
    }

    // Metodo para convertir a DTO
    private MovimientoInventarioDTO convertirADTO(MovimientoInventario movimientoInventario) {
        return new MovimientoInventarioDTO(
                movimientoInventario.getIdMovimientoInventario(),
                movimientoInventario.getIdUsuario(),
                movimientoInventario.getIdProducto(),
                movimientoInventario.getTipoMovimiento(),
                movimientoInventario.getCantidad(),
                movimientoInventario.getFechaMovimiento(),
                movimientoInventario.getObservacion()
        );
    }

}
