package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.entities.MovimientoInventario;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.entities.TipoMovimiento;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.exceptions.MovimientoInventarioNoEncontradoException;
import com.proyecto.invengest.exceptions.ProductoNoEncontradoException;
import com.proyecto.invengest.exceptions.TipoMovimientoInventarioNoEncontradoException;
import com.proyecto.invengest.exceptions.UsuarioNoEncontradoException;
import com.proyecto.invengest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoInventarioServicio {

    private final MovimientoInventarioRepositorio movimientoInventarioRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final TipoMovimientoRepositorio tipoMovimientoRepositorio;

    private final UsuarioRepositorio usuarioRepositorio;

    public MovimientoInventarioServicio(MovimientoInventarioRepositorio movimientoInventarioRepositorio, ProductoRepositorio productoRepositorio, TipoMovimientoRepositorio tipoMovimientoRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.movimientoInventarioRepositorio = movimientoInventarioRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.tipoMovimientoRepositorio = tipoMovimientoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
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
//    public MovimientoInventarioDTO crearMovimiento(MovimientoInventarioDTO movimientoDTO) {
//        MovimientoInventario movimiento = new MovimientoInventario();
//
//        // Resolver producto
//        Producto producto = productoRepositorio.findById(movimientoDTO.getIdProducto())
//                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + movimientoDTO.getIdProducto()));
//
//        // Resolver tipo de movimiento
//        TipoMovimiento tipoMovimiento = tipoMovimientoRepositorio.findById(movimientoDTO.getIdMovimiento())
//                .orElseThrow(() -> new RuntimeException("Tipo de movimiento no encontrado con ID: " + movimientoDTO.getIdMovimiento()));
//
//        movimiento.setIdProducto(producto);
//        movimiento.setIdMovimiento(tipoMovimiento);
//        movimiento.setCantidad(movimientoDTO.getCantidad());
//        movimiento.setFechaMovimiento(LocalDate.now());
//        movimiento.setObservacion(movimientoDTO.getObservacion());
//
//        MovimientoInventario nuevoMovimiento = movimientoInventarioRepositorio.save(movimiento);
//        return convertirADTO(nuevoMovimiento);
//    }

    public void registrarMovimientoInventario(MovimientoInventarioDTO movimientoDTO) {
        MovimientoInventario movimiento = new MovimientoInventario();

        // Resolver producto
        Producto producto = productoRepositorio.findById(movimientoDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + movimientoDTO.getIdProducto()));

        // Resolver tipo de movimiento
        TipoMovimiento tipoMovimiento = tipoMovimientoRepositorio.findById(movimientoDTO.getIdMovimiento())
                .orElseThrow(() -> new RuntimeException("Tipo de movimiento no encontrado con ID: " + movimientoDTO.getIdMovimiento()));

        // Resolver usuario
        Usuario usuario = usuarioRepositorio.findById(movimientoDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + movimientoDTO.getIdUsuario()));

        // Asignar valores
        movimiento.setIdProducto(producto);
        movimiento.setIdMovimiento(tipoMovimiento);
        movimiento.setIdUsuario(usuario);
        movimiento.setCantidad(movimientoDTO.getCantidad());
        movimiento.setFechaMovimiento(LocalDate.now());
        movimiento.setObservacion(movimientoDTO.getObservacion());

        // Guardar en la base de datos
        movimientoInventarioRepositorio.save(movimiento);
    }

    // Borrar un movimiento
    public void eliminarMovimiento(@PathVariable int id) {
        if(!movimientoInventarioRepositorio.existsById(id)) {
            throw new MovimientoInventarioNoEncontradoException("Movimiento no encontrado con el ID: " + id + "");
        }
        movimientoInventarioRepositorio.deleteById(id);
    }

    // Modificar un movimiento
    public MovimientoInventarioDTO modificarMovimiento(@PathVariable int id, @RequestBody MovimientoInventarioDTO movimientoInventarioDTO) {

        MovimientoInventario movimientoModificado = movimientoInventarioRepositorio.findById(id)
                .orElseThrow(() -> new MovimientoInventarioNoEncontradoException("Movimiento no encontrado con el ID: " + id + ""));

        // Mapeamos campos del DTO al producto existente
        movimientoModificado.setIdProducto(productoRepositorio.findById(movimientoInventarioDTO.getIdProducto())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con el ID: " + movimientoInventarioDTO.getIdProducto() + "")));

        movimientoModificado.setIdUsuario(usuarioRepositorio.findById(movimientoInventarioDTO.getIdUsuario())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con el ID: " + movimientoInventarioDTO.getIdUsuario() + "")));
        movimientoModificado.setIdMovimiento(tipoMovimientoRepositorio.findById(movimientoInventarioDTO.getIdMovimiento())
                .orElseThrow(() -> new TipoMovimientoInventarioNoEncontradoException("Tipo de movimiento no encontrado con el ID: " + movimientoInventarioDTO.getIdMovimiento() + "")));

        movimientoModificado.setCantidad(movimientoInventarioDTO.getCantidad());
        movimientoModificado.setFechaMovimiento(movimientoInventarioDTO.getFechaMovimiento());
        movimientoModificado.setObservacion(movimientoInventarioDTO.getObservacion());

        MovimientoInventario movimientoGuardado = movimientoInventarioRepositorio.save(movimientoModificado);

        return convertirADTO(movimientoGuardado);
    }

    // Metodo para convertir a DTO
    public MovimientoInventarioDTO convertirADTO(MovimientoInventario movimientoInventario) {
        return new MovimientoInventarioDTO(
                movimientoInventario.getIdMovimientoInventario(),
                movimientoInventario.getIdProducto().getIdProducto(),
                movimientoInventario.getIdUsuario().getIdUsuario(),
                movimientoInventario.getIdMovimiento().getIdMovimiento(),
                movimientoInventario.getCantidad(),
                movimientoInventario.getFechaMovimiento(),
                movimientoInventario.getObservacion()
        );
    }

}
