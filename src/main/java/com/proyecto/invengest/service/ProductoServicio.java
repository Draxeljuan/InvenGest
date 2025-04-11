package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.exceptions.ProductoNoEncontradoException;
import com.proyecto.invengest.repository.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;
    public ProductoServicio(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    // Listar productos
    public List<ProductoDTO> listarProductos() {
        List<Producto> productos = productoRepositorio.findAll();
        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener producto por Id con DTO
    public ProductoDTO obtenerProducto(@PathVariable String id) {
        Producto producto = productoRepositorio.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con el ID: " + id + ""));
        return convertirADTO(producto);
    }

    // Crear un nuevo producto
    public ProductoDTO crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoRepositorio.save(producto);
        return convertirADTO(nuevoProducto);
    }

    // Eliminar producto por Id
    public void eliminarProducto(@PathVariable String id) {
        if(!productoRepositorio.existsById(id)) {
            throw new ProductoNoEncontradoException("Producto no encontrado con el ID: " + id + "");
        }
        productoRepositorio.deleteById(id);
    }

    // Modificar un producto
    public ProductoDTO modificarProducto(@PathVariable String id, @RequestBody Producto producto) {
        Producto productoModificado = productoRepositorio.findById(id).orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado"));

        actualizarProducto(producto, productoModificado);

        return convertirADTO(productoRepositorio.save(productoModificado));
    }

    // Metodo para convertir a DTO
    private ProductoDTO convertirADTO(Producto producto) {
        return new ProductoDTO(
                producto.getIdProducto(),
                producto.getCategoria().getIdCategoria(),
                producto.getNombre(),
                producto.getPrecioVenta(),
                producto.getCostoCompra(),
                producto.getStock(),
                producto.getUbicacion(),
                producto.getEstado(),
                producto.getFechaIngreso()
        );
    }

    // Metodo para actualizar producto
    private void actualizarProducto(Producto origen, Producto destino) {
        destino.setNombre(origen.getNombre());
        destino.setPrecioVenta(origen.getPrecioVenta());
        destino.setCostoCompra(origen.getCostoCompra());
        destino.setStock(origen.getStock());
        destino.setUbicacion(origen.getUbicacion());
        destino.setEstado(origen.getEstado());
    }

}
