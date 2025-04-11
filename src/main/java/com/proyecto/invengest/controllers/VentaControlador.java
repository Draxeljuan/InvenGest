package com.proyecto.invengest.controllers;



import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.entities.Venta;

import com.proyecto.invengest.service.VentaServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/ventas")
public class VentaControlador {


    private final VentaServicio ventaServicio;

    public VentaControlador(VentaServicio ventaServicio) {
        this.ventaServicio = ventaServicio;
    }


    // Obtener una Venta por ID
    @GetMapping("/{idVenta}")
    public VentaDTO obtenerVenta(@PathVariable int idVenta){
        return ventaServicio.obtenerVenta(idVenta);
    }

    // Obtener lista de todas las ventas
    @GetMapping
    public List<VentaDTO> listarVentas(){
        return ventaServicio.listarVentas();
    }

    // Crear una nueva venta con detalles
    @PostMapping
    public VentaDTO crearVenta(@RequestBody Venta venta){
        return ventaServicio.crearVenta(venta);
    }

    // Eliminar venta por Id
    @DeleteMapping("/{idVenta}")
    public void eliminarVenta(@PathVariable int idVenta){
        ventaServicio.eliminarVenta(idVenta);
    }

    // Actualizar venta
    @PutMapping("/{idVenta}")
    public VentaDTO actualizarVenta(@PathVariable int idVenta, @RequestBody VentaDTO ventaDTO) {
        return ventaServicio.actualizarVenta(idVenta, ventaDTO);
    }

}
