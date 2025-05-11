package com.proyecto.invengest.service.inventario;


import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.entities.EstadoProducto;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.exceptions.ProductoNoEncontradoException;
import com.proyecto.invengest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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

    // Consultar producto por nombre
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepositorio.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
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

            // Resolver categoría
            producto.setIdCategoria(categoriaRepositorio.findById(productoDTO.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + productoDTO.getIdCategoria())));

            // Asignar estado automáticamente según stock
            producto.setIdEstado(determinarEstadoProducto(productoDTO.getStock()));

            // Guardar producto
            Producto productoGuardado = productoRepositorio.save(producto);

            // **Registrar el movimiento de inventario automáticamente**
            MovimientoInventarioDTO movimientoDTO = new MovimientoInventarioDTO(
                    null,
                    productoGuardado.getIdProducto(),
                    1, // ID del usuario admin
                    1, // Tipo de movimiento "Entrada"
                    productoGuardado.getStock(),
                    LocalDate.now(),
                    "Registro de nuevo producto: " + productoGuardado.getNombre()
            );

            movimientoInventarioServicio.registrarMovimientoInventario(movimientoDTO);

            return convertirADTO(productoGuardado);

        } catch (Exception ex) {
            System.err.println("Error inesperado al crear el producto: " + ex.getMessage());
            throw new RuntimeException("Ocurrió un error al crear el producto.", ex);
        }
    }

    // Metodo para determinar el estado del producto según el stock
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
    public void descontinuarProducto(@PathVariable String id) {
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

        // Guarda valores del stock y estado anteriores
        int stockAnterior = productoModificado.getStock();
        int estadoAnterior = productoModificado.getIdEstado().getId();

        // Mapear campos básicos del DTO al producto existente
        productoModificado.setNombre(productoDTO.getNombre());
        productoModificado.setPrecioVenta(productoDTO.getPrecioVenta());
        productoModificado.setCostoCompra(productoDTO.getCostoCompra());
        productoModificado.setFechaIngreso(productoDTO.getFechaIngreso());
        productoModificado.setStock(productoDTO.getStock());
        productoModificado.setUbicacion(productoDTO.getUbicacion());

        // Resolver categoría desde el repositorio
        productoModificado.setIdCategoria(categoriaRepositorio.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + productoDTO.getIdCategoria())));

        // Ajuste automático del estado según stock
        EstadoProducto nuevoEstado = determinarEstadoProducto(productoDTO.getStock());
        productoModificado.setIdEstado(nuevoEstado);

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
                    "Modificación de producto. Stock anterior: " + stockAnterior + ", nuevo stock: " + productoGuardado.getStock()
            );
            movimientoInventarioServicio.registrarMovimientoInventario(movimientoDTO);
        }

        // Registrar cambio de estado solo si se modificó
        if (nuevoEstado.getId() != estadoAnterior) {
            System.out.println("Cambio de estado: de " + estadoAnterior + " a " + nuevoEstado.getId());
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
