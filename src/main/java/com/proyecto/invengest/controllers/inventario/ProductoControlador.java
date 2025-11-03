package com.proyecto.invengest.controllers.inventario;


import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.service.inventario.ProductoServicio;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")

public class ProductoControlador {

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }


    // Obtener lista de productos con DTO
    @GetMapping
    public List<ProductoDTO> listarProductos() {
        return productoServicio.listarProductos();
    }

    // Obtener producto por id con DTO
    @GetMapping("/{id}")
    public ProductoDTO obtenerProducto(@PathVariable String id) {
        return productoServicio.obtenerProducto(id);
    }

    // Obtener producto por nombre
    @GetMapping("/buscar")
    public List<ProductoDTO> buscarProductos(@RequestParam String nombre) {
        return productoServicio.buscarPorNombre(nombre);
    }

    // Crear nuevo producto
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/crear")
    public ProductoDTO crearProducto(@RequestBody ProductoDTO productoDTO) {
        return productoServicio.crearProducto(productoDTO);
    }

    // Eliminar producto por id
    @PutMapping("/descontinuar/{id}")
    public void descontinuarProducto(@PathVariable String id) {
        productoServicio.descontinuarProducto(id);
    }

    @PutMapping("/{id}")
    public ProductoDTO modificarProducto(@PathVariable String id, @RequestBody ProductoDTO productoDTO) {
        return productoServicio.modificarProducto(id, productoDTO);
    }




}
