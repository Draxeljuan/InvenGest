package com.proyecto.invengest.service;


import com.proyecto.invengest.dto.DetalleVentaDTO;
import com.proyecto.invengest.dto.ProductoMasVendidoDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.entities.Venta;
import com.proyecto.invengest.repository.VentaRepositorio;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteVentaServicio {

    private final VentaRepositorio ventaRepositorio;

    public ReporteVentaServicio(VentaRepositorio ventaRepositorio) {
        this.ventaRepositorio = ventaRepositorio;

    }


    // Productos mas vendidos
    public List<ProductoMasVendidoDTO> reporteProductosMasVendidos(Date fechaInicio, Date fechaFin, int minVentas) {
        return ventaRepositorio.obtenerProductosMasVendidos(fechaInicio, fechaFin, minVentas).stream()
                .map(this::convertirAProductoDTO)
                .collect(Collectors.toList());
    }

    // Reporte de venta por fecha
    public Map<Date, List<VentaDTO>> reporteVentasPorFecha(Date fechaInicio, Date fechaFin) {
        return ventaRepositorio.obtenerVentasPorFecha(fechaInicio, fechaFin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.groupingBy(VentaDTO::getFecha));
    }

    //Reporte Ingresos Totales
    public double reporteIngresosTotales(Date fechaInicio, Date fechaFin) {
        return ventaRepositorio.obtenerIngresosTotales(fechaInicio, fechaFin);
    }

    // Metodo para convertir a DTO
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


    // Convertir resultados query en DTO
    private ProductoMasVendidoDTO convertirAProductoDTO (Object[] resultado){
        return new ProductoMasVendidoDTO(
                resultado[0].toString(),
                Integer.parseInt(resultado[1].toString())
        );
    }

}
