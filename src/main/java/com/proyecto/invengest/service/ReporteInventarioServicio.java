package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.repository.ProductoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteInventarioServicio {

    private final ProductoRepositorio productoRepositorio;

    public ReporteInventarioServicio(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }


    // Reporte de productos con bajo stock
    public List<ProductoDTO> reporteBajoStock (int limiteBusquedaStock){
        return productoRepositorio.findAll()
                .stream()
                .filter(producto -> producto.getStock() <= limiteBusquedaStock)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Reporte por Categoria
    public Map<Integer, List<ProductoDTO>> reportePorCategoria() {
        return productoRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.groupingBy(ProductoDTO::getIdCategoria));
    }

    // Inventario total (todos los productos)
    public List<ProductoDTO> reporteInventarioTotal(){
        return productoRepositorio.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
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

}
