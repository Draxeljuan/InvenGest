package com.proyecto.invengest.controllers.ventas;



import com.proyecto.invengest.dto.ProductoTopDTO;
import com.proyecto.invengest.dto.VentaDTO;

import com.proyecto.invengest.service.ventas.VentaServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/ventas")
public class VentaControlador {


    private final VentaServicio ventaServicio;

    public VentaControlador(VentaServicio ventaServicio) {
        this.ventaServicio = ventaServicio;
    }

    // Obtener Ventas del dia actual
    @GetMapping("/hoy")
    public List<VentaDTO> ventasDia() { return ventaServicio.obtenerVentaDia(); }

    // Obtener Ventas de la ultima semana
    @GetMapping("/semana")
    public List<VentaDTO> ventasSemana() { return ventaServicio.obtenerVentaSemana(); }

    // Obtener top de ventas
    @GetMapping("/top")
    public List<ProductoTopDTO> obtenerProductoTop(@RequestParam(defaultValue = "5") int limite, @RequestParam(defaultValue = "30") int dias) {
        return ventaServicio.obtenerProductosMasVendidos(limite, dias);
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
    @PostMapping("/crear")
    public ResponseEntity<VentaDTO> crearVenta(@RequestBody @Valid VentaDTO ventaDTO) {
        VentaDTO nuevaVenta = ventaServicio.crearVenta(ventaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
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
