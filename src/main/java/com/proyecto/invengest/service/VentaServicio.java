package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.DetalleVentaDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.entities.DetalleVenta;
import com.proyecto.invengest.entities.Venta;
import com.proyecto.invengest.exceptions.VentaNoEncontradaException;
import com.proyecto.invengest.repository.VentaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaServicio {

    private final VentaRepositorio ventaRepositorio;

    public VentaServicio(VentaRepositorio ventaRepositorio) {
        this.ventaRepositorio = ventaRepositorio;
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
            throw new VentaNoEncontradaException("Venta no encontrada con el ID: " + idVenta + "");
        }

        Venta venta  = ventaRepositorio.findVentaConDetalles(idVenta);
        return convertirADTO(venta);
    }

    // Crear una nueva venta con detalles
    public VentaDTO crearVenta(@RequestBody Venta venta){
        Venta nuevaVenta = ventaRepositorio.save(venta);
        return convertirADTO(nuevaVenta);
    }

    // Eliminar una venta
    public void eliminarVenta(@PathVariable int idVenta){
        if(!ventaRepositorio.existsById(idVenta)){
            throw new VentaNoEncontradaException("Venta no encontrada con el ID: " + idVenta + "");
        }
        ventaRepositorio.deleteById(idVenta);
    }

    // Actualizar venta
    public VentaDTO actualizarVenta(@PathVariable int idVenta, @RequestBody VentaDTO ventaDTO) {
        // Buscar la venta existente por su ID
        Venta ventaExistente = ventaRepositorio.findById(idVenta)
                .orElseThrow(() -> new VentaNoEncontradaException("Venta no encontrada"));

        // Actualizar los datos principales de la venta
        ventaExistente.setNombreCliente(ventaDTO.getNombreCliente());
        ventaExistente.setApellidoCliente(ventaDTO.getApellidoCliente());
        ventaExistente.setTotal(ventaDTO.getTotal());
        ventaExistente.setFecha(ventaDTO.getFecha());

        // Actualizar los detalles de la venta
        List<DetalleVenta> detallesActualizados = ventaDTO.getDetalles()
                .stream()
                .map(detalleDTO -> {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setVenta(ventaExistente); // Relación con la venta existente
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                    detalle.setSubtotal(detalleDTO.getSubtotal());
                    // Aquí podrías buscar el producto por ID si es necesario
                    return detalle;
                })
                .collect(Collectors.toList());

        // Limpiar y reemplazar los detalles existentes
        ventaExistente.getDetalles().clear();
        ventaExistente.getDetalles().addAll(detallesActualizados);

        // Guardar la venta actualizada
        Venta ventaActualizada = ventaRepositorio.save(ventaExistente);

        // Devolver el DTO de la venta actualizada
        return convertirADTO(ventaActualizada);
    }


    // Metodo para convetir a DTO
    private VentaDTO convertirADTO(Venta venta){
        List<DetalleVentaDTO> detallesDTO = venta.getDetalles()
                .stream()
                .map(detalle -> new DetalleVentaDTO(
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new VentaDTO(
                venta.getIdVenta(),
                venta.getUsuario().getIdUsuario(),
                venta.getNombreCliente(),
                venta.getApellidoCliente(),
                venta.getTotal(),
                venta.getFecha(),
                detallesDTO
        );

    }

}
