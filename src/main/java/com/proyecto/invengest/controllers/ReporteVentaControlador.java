package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.ProductoMasVendidoDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.service.ReporteVentaServicio;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes/venta")
public class ReporteVentaControlador {

    private final ReporteVentaServicio reporteVentaServicio;

    public ReporteVentaControlador(ReporteVentaServicio reporteVentaServicio) {
        this.reporteVentaServicio = reporteVentaServicio;
    }

    // Productos mas vendidos
    public List<ProductoMasVendidoDTO> productosMasVendidos(@RequestParam Date fechaInicio,
                                                            @RequestParam Date fechaFin,
                                                            @RequestParam int minVentas){
        return reporteVentaServicio.reporteProductosMasVendidos( fechaInicio, fechaFin, minVentas);

    }

    // Obtener ventas en rango de tiempo
    public Map<Date, List<VentaDTO>> ventasPorFecha (@RequestParam Date fechaInicio,
                                                     @RequestParam Date fechaFin){
        return reporteVentaServicio.reporteVentasPorFecha(fechaInicio, fechaFin);
    }

    // Obtener ingresos en periodo de tiempo
    public double ingresosTotales (@RequestParam Date fechaInicio,
                                    @RequestParam Date fechaFin){
        return reporteVentaServicio.reporteIngresosTotales(fechaInicio, fechaFin);
    }

}
