package com.proyecto.invengest.service.ventas;


import com.proyecto.invengest.dto.DetalleVentaDTO;
import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.entities.*;
import com.proyecto.invengest.exceptions.VentaNoEncontradaException;
import com.proyecto.invengest.repository.*;
import com.proyecto.invengest.service.inventario.AlertaServicio;
import com.proyecto.invengest.service.inventario.MovimientoInventarioServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VentaServicio {

    private final VentaRepositorio ventaRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final EstadoProductoRepositorio estadoProductoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final AlertaServicio alertaServicio;
    private final MovimientoInventarioServicio movimientoInventarioServicio;


    public VentaServicio(VentaRepositorio ventaRepositorio, ClienteRepositorio clienteRepositorio, ProductoRepositorio productoRepositorio, UsuarioRepositorio usuarioRepositorio, AlertaServicio alertaServicio, MovimientoInventarioServicio movimientoInventarioServicio, EstadoProductoRepositorio estadoProductoRepositorio) {
        this.ventaRepositorio = ventaRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.estadoProductoRepositorio = estadoProductoRepositorio;
        this.alertaServicio = alertaServicio;
        this.movimientoInventarioServicio = movimientoInventarioServicio;

    }


    // Listar ventas con DTO
    public List<VentaDTO> listarVentas(){
        return ventaRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener ventas por Id
    public VentaDTO obtenerVenta(@PathVariable int idVenta){

        if(!ventaRepositorio.existsById(idVenta)){
            throw new VentaNoEncontradaException("Venta no encontrada con el ID: " + idVenta + " ");
        }

        Venta venta  = ventaRepositorio.findVentaConDetalles(idVenta);
        return convertirADTO(venta);
    }

    // Crear una nueva venta con detalles
    public VentaDTO crearVenta(@RequestBody @Valid VentaDTO ventaDTO) {
        // Validar usuario y cliente
        Usuario usuario = usuarioRepositorio.findById(ventaDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + ventaDTO.getIdUsuario()));

        Cliente cliente = clienteRepositorio.findById(ventaDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + ventaDTO.getIdCliente()));

        // Validar detalles
        if (ventaDTO.getDetalles() == null || ventaDTO.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un producto.");
        }

        // Crear la entidad Venta
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(ventaDTO.getFecha());
        nuevaVenta.setIdUsuario(usuario);
        nuevaVenta.setIdCliente(cliente);
        nuevaVenta.setTotal(BigDecimal.ZERO); // Inicializamos el total

        nuevaVenta = ventaRepositorio.saveAndFlush(nuevaVenta); // Guardar primero para tener ID asignado

        // Mapear y crear detalles de la venta
        Venta ventaFinal = nuevaVenta; // Variable efectivamente final para la lambda
        Set<DetalleVenta> detalles = ventaDTO.getDetalles().stream()
                .map(detalleDTO -> {
                    Producto producto = productoRepositorio.findById(detalleDTO.getIdProducto())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getIdProducto()));

                    // Verificar stock disponible
                    if (producto.getStock() < detalleDTO.getCantidad()) {
                        throw new IllegalArgumentException("Stock insuficiente para el producto con ID: " + detalleDTO.getIdProducto());
                    }

                    // Actualizar el stock del producto (Reducir antes de cambiar estado)
                    producto.setStock((short) (producto.getStock() - detalleDTO.getCantidad()));

                    // Ahora sí se puede actualizar el estado correctamente
                    producto.setIdEstado(determinarEstadoProducto(producto.getStock()));

                    // Guardar el producto con stock y estado actualizado
                    productoRepositorio.save(producto);





                    // Disparar alerta de stock bajo o sin stock
                    alertaServicio.generarAlertaStock(producto);

                    // Registrar el movimiento de inventario automáticamente
                    MovimientoInventarioDTO movimientoDTO = new MovimientoInventarioDTO(
                            null, // ID se genera automáticamente
                            producto.getIdProducto(),
                            usuario.getIdUsuario(),
                            2,
                            detalleDTO.getCantidad(),
                            LocalDate.now(),
                            "Venta realizada con ID: " + ventaFinal.getIdVenta()
                    );
                    movimientoInventarioServicio.registrarMovimientoInventario(movimientoDTO);

                    // Crear el detalle de la venta
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setIdVenta(ventaFinal); // Asociar correctamente la venta
                    detalle.setIdProducto(producto);
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setPrecioUnitario(producto.getPrecioVenta());
                    detalle.setSubtotal(producto.getPrecioVenta().multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));
                    return detalle;
                })
                .collect(Collectors.toSet());

        // Limpia y agrega nuevos detalles (evitar problemas con orphanRemoval)
        if (nuevaVenta.getDetalleVentas() == null) {
            nuevaVenta.setDetalleVentas(new LinkedHashSet<>());
        } else {
            nuevaVenta.getDetalleVentas().clear(); // Limpia cualquier referencia previa
        }
        nuevaVenta.getDetalleVentas().addAll(detalles);

        // Calcular el total y guardar nuevamente
        BigDecimal total = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        nuevaVenta.setTotal(total);

        nuevaVenta = ventaRepositorio.saveAndFlush(nuevaVenta); // Guardar con los detalles asignados

        return convertirADTO(nuevaVenta);
    }

    // Eliminar una venta
    public void eliminarVenta(@PathVariable int idVenta){
        if(!ventaRepositorio.existsById(idVenta)){
            throw new VentaNoEncontradaException("Venta no encontrada con el ID: " + idVenta + " ");
        }
        ventaRepositorio.deleteById(idVenta);
    }

    // Actualizar venta
    public VentaDTO actualizarVenta(@PathVariable int idVenta, @RequestBody @Valid VentaDTO ventaDTO) {
        // Buscar la venta existente por su ID
        Venta ventaExistente = ventaRepositorio.findById(idVenta)
                .orElseThrow(() -> new VentaNoEncontradaException("Venta no encontrada"));

        // Validar la existencia del usuario
        Usuario usuario = usuarioRepositorio.findById(ventaDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + ventaDTO.getIdUsuario()));

        // Validar la existencia del cliente
        Cliente cliente = clienteRepositorio.findById(ventaDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + ventaDTO.getIdCliente()));

        ventaExistente.setIdUsuario(usuario);
        ventaExistente.setIdCliente(cliente);
        ventaExistente.setTotal(ventaDTO.getTotal());
        ventaExistente.setFecha(ventaDTO.getFecha());

        // Validar y actualizar detalles de la venta
        Set<DetalleVenta> detallesActualizados = ventaDTO.getDetalles().stream()
                .map(detalleDTO -> {
                    Producto producto = productoRepositorio.findById(detalleDTO.getIdProducto())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getIdProducto()));

                    System.out.println("Producto encontrado: "+ producto.getNombre());

                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setIdVenta(ventaExistente);
                    detalle.setIdProducto(producto);
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setPrecioUnitario(producto.getPrecioVenta());
                    detalle.setSubtotal(producto.getPrecioVenta().multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));
                    return detalle;
                })
                .collect(Collectors.toSet());

        // Solo actualizar detalles si hay nuevos
        if (!detallesActualizados.isEmpty()) {
            ventaExistente.getDetalleVentas().clear();
            ventaExistente.getDetalleVentas().addAll(detallesActualizados);
        }

        // Guardar la venta actualizada
        Venta ventaActualizada = ventaRepositorio.save(ventaExistente);

        return convertirADTO(ventaActualizada);
    }

    private EstadoProducto determinarEstadoProducto(int stock) {
        int estadoId;
        if (stock > 10) {
            estadoId = 1; // Estado "Normal"
        } else if (stock > 0) {
            estadoId = 2; // Estado "Bajo"
        } else {
            estadoId = 3; // Estado "Sin Stock"
        }

        return estadoProductoRepositorio.findById(estadoId)
                .orElseThrow(() -> new RuntimeException("Estado de producto no encontrado con ID: " + estadoId));
    }


    // Metodo para convetir a DTO
    private VentaDTO convertirADTO(Venta venta){
        List<DetalleVentaDTO> detallesDTO = venta.getDetalleVentas()
                .stream()
                .map(detalle -> new DetalleVentaDTO(
                        detalle.getIdProducto().getNombre(),
                        detalle.getSubtotal(),
                        detalle.getPrecioUnitario(),
                        detalle.getCantidad()
                ))
                .collect(Collectors.toList());

        return new VentaDTO(
                venta.getIdVenta(),
                venta.getIdUsuario().getIdUsuario(),
                venta.getFecha(),
                venta.getIdCliente().getIdCliente(),
                venta.getTotal(),
                detallesDTO
        );

    }

}
