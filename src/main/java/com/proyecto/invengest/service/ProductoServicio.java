package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.exceptions.ProductoNoEncontradoException;
import com.proyecto.invengest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductoServicio {


    private final ProductoRepositorio productoRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final EstadoProductoRepositorio estadoProductoRepositorio;
    private final VentaRepositorio ventaRepositorio;
    private final MovimientoInventarioRepositorio movimientoInventarioRepositorio;
    private final MovimientoInventarioServicio movimientoInventarioServicio;


    public ProductoServicio(ProductoRepositorio productoRepositorio, CategoriaRepositorio categoriaRepositorio, EstadoProductoRepositorio estadoProductoRepositorio, MovimientoInventarioServicio movimientoInventarioServicio, VentaRepositorio ventaRepositorio, MovimientoInventarioRepositorio movimientoInventarioRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
        this.estadoProductoRepositorio = estadoProductoRepositorio;
        this.movimientoInventarioServicio = movimientoInventarioServicio;
        this.ventaRepositorio = ventaRepositorio;
        this.movimientoInventarioRepositorio = movimientoInventarioRepositorio;
    }

    // Listar productos
    public List<ProductoDTO> listarProductos() {
        List<Producto> productos = productoRepositorio.findAllByIdEstadoNot(4); // Excluir descontinuados
        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener producto por Id con DTO
    public ProductoDTO obtenerProducto(@PathVariable String id) {
        Producto producto = productoRepositorio.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con el ID: " + id));

        // Verificar si el producto está descontinuado
        if (producto.getIdEstado().getId() == 4) {
            throw new ProductoNoEncontradoException("El producto con ID " + id + " está descontinuado.");
        }

        return convertirADTO(producto);
    }

    // Crear un nuevo producto
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        try {
            // Crear instancia de Producto
            Producto producto = new Producto();

            // Generar automáticamente el ID del producto si no se proporciona
            if (productoDTO.getIdProducto() == null || productoDTO.getIdProducto().isEmpty()) {
                producto.setIdProducto(generarIdProductoUnico());
            }

            // Mapear valores del DTO al objeto Producto
            producto.setNombre(productoDTO.getNombre());
            producto.setPrecioVenta(productoDTO.getPrecioVenta());
            producto.setCostoCompra(productoDTO.getCostoCompra());
            producto.setFechaIngreso(productoDTO.getFechaIngreso());
            producto.setStock(productoDTO.getStock());
            producto.setUbicacion(productoDTO.getUbicacion());

            // Resolver entidades relacionadas (Categoría y Estado)
            producto.setIdCategoria(categoriaRepositorio.findById(productoDTO.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + productoDTO.getIdCategoria())));
            producto.setIdEstado(estadoProductoRepositorio.findById(productoDTO.getIdEstado())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado: " + productoDTO.getIdEstado())));

            // Guardar el producto en la base de datos
            Producto productoGuardado;
            try {
                productoGuardado = productoRepositorio.save(producto);
            } catch (TransactionSystemException ex) {
                // Manejar errores de transacción y registrar la causa raíz
                if (ex.getRootCause() != null) {
                    ex.printStackTrace();
                    System.err.println("Causa raíz de TransactionSystemException: " + ex.getRootCause().getMessage());
                }
                throw ex;
            }

            // **Registrar el movimiento de inventario automáticamente**
            MovimientoInventarioDTO movimientoDTO = new MovimientoInventarioDTO(
                    null, // ID se genera automáticamente
                    productoGuardado.getIdProducto(),
                    1, // ID del usuario admin (porque solo el admin puede crear productos)
                    1, // ID del tipo de movimiento "Entrada"
                    productoGuardado.getStock(),
                    LocalDate.now(),
                    "Registro de nuevo producto: " + productoGuardado.getNombre()
            );

            // Llamar al método para registrar el movimiento
            movimientoInventarioServicio.registrarMovimientoInventario(movimientoDTO);

            // Convertir el producto guardado a DTO y devolverlo
            return convertirADTO(productoGuardado);

        } catch (RuntimeException ex) {
            // Manejo de errores específicos de runtime
            System.err.println("Error durante la creación del producto: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            // Manejo general de excepciones
            System.err.println("Error inesperado durante la creación del producto: " + ex.getMessage());
            throw new RuntimeException("Ocurrió un error inesperado al crear el producto.", ex);
        }
    }

    // Metodo para generar un identificador unico
    private String generarIdProductoUnico() {
        String idProducto;
        do {
            // Limitar el tamaño total a 10 caracteres (ejemplo: "PROD12345")
            idProducto = "PROD" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        } while (productoRepositorio.existsById(idProducto)); // Validación de unicidad
        return idProducto;
    }

    // Eliminar producto por Id
    public void eliminarProducto(@PathVariable String id) {
        // Verificar si el producto existe
        Producto productoEliminado = productoRepositorio.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con el ID: " + id));

        // Cambiar el estado del producto a "Descontinuado" (ID = 4)
        productoEliminado.setIdEstado(estadoProductoRepositorio.findById(4)
                .orElseThrow(() -> new RuntimeException("Estado 'Descontinuado' no encontrado")));

        productoRepositorio.save(productoEliminado); // Guardar el cambio de estado

        // Registrar movimiento de inventario
        MovimientoInventarioDTO movimientoDTO = new MovimientoInventarioDTO(
                null,
                productoEliminado.getIdProducto(),
                1, // ID del usuario admin
                3, // ID del tipo de movimiento "Ajuste"
                productoEliminado.getStock(),
                LocalDate.now(),
                "Producto marcado como descontinuado: " + productoEliminado.getNombre()
        );

        movimientoInventarioServicio.registrarMovimientoInventario(movimientoDTO);
    }

    // Modificar un producto
    public ProductoDTO modificarProducto(String id, ProductoDTO productoDTO) {
        // Buscar el producto existente
        Producto productoModificado = productoRepositorio.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado"));

        // Guarda valores del stock por si llega a ser modificado
        int stockAnterior = productoModificado.getStock();

        // Mapear campos básicos del DTO al producto existente
        productoModificado.setNombre(productoDTO.getNombre());
        productoModificado.setPrecioVenta(productoDTO.getPrecioVenta());
        productoModificado.setCostoCompra(productoDTO.getCostoCompra());
        productoModificado.setFechaIngreso(productoDTO.getFechaIngreso());
        productoModificado.setStock(productoDTO.getStock());
        productoModificado.setUbicacion(productoDTO.getUbicacion());

        // Resolver entidades relacionadas (Categoria y Estado) desde los repositorios
        productoModificado.setIdCategoria(categoriaRepositorio.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productoDTO.getIdCategoria())));
        productoModificado.setIdEstado(estadoProductoRepositorio.findById(productoDTO.getIdEstado())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con ID: " + productoDTO.getIdEstado())));

        // Guardar el producto actualizado en la base de datos
        Producto productoGuardado = productoRepositorio.save(productoModificado);

        // Registrar movimiento de inventario solo si el stock ha cambiado
        if (productoDTO.getStock() != stockAnterior) {
            MovimientoInventarioDTO movimientoDTO = new MovimientoInventarioDTO(
                    null,
                    productoGuardado.getIdProducto(),
                    1,
                    3,
                    productoGuardado.getStock(),
                    LocalDate.now(),
                    "Modificacion de producto. Stock anterior: " + stockAnterior + ", nuevo stock: " + productoGuardado.getStock()

            );
            movimientoInventarioServicio.registrarMovimientoInventario(movimientoDTO);
        }



        // Convertir el producto actualizado a un DTO y devolverlo
        return convertirADTO(productoGuardado);
    }

    // Metodo para convertir a DTO
    private ProductoDTO convertirADTO(Producto producto) {
        return new ProductoDTO(
                producto.getIdProducto(),
                producto.getIdCategoria().getIdCategoria(),
                producto.getNombre(),
                producto.getPrecioVenta(),
                producto.getCostoCompra(),
                producto.getFechaIngreso(),
                producto.getStock(),
                producto.getUbicacion(),
                producto.getIdEstado().getId()

        );
    }



}
