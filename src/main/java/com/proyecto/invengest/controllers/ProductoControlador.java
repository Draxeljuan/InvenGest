package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.service.ProductoServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")

public class ProductoControlador {

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }


    // ✅ Obtener lista de productos con DTO
    @GetMapping
    public List<ProductoDTO> listarProductos() {
        return productoServicio.listarProductos();
    }

    // ✅ Obtener producto por id con DTO
    @GetMapping("/{id}")
    public ProductoDTO obtenerProducto(@PathVariable String id) {
        return productoServicio.obtenerProducto(id);
    }

    // ✅ Crear nuevo producto
    @PostMapping
    public ProductoDTO crearProducto(@RequestBody Producto producto) {
        return productoServicio.crearProducto(producto);
    }

    // ✅ Eliminar producto por id
    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable String id) {
        productoServicio.eliminarProducto(id);
    }

    // Modificar un producto
    @PutMapping("/{id}")
    public ProductoDTO modificarProducto(@PathVariable String id, @RequestBody Producto producto) {
        return productoServicio.modificarProducto(id, producto);
    }



}
