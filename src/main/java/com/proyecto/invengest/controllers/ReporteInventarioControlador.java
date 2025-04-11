package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.service.ReporteInventarioServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes/inventario")
public class ReporteInventarioControlador {

    private final ReporteInventarioServicio reporteInventarioServicio;

    public ReporteInventarioControlador(ReporteInventarioServicio reporteInventarioServicio) {
        this.reporteInventarioServicio = reporteInventarioServicio;
    }

    // Reporte por bajo stock
    @GetMapping("/bajo-stock/{limite}")
    public List<ProductoDTO> reporteBajoStock(@PathVariable int limite){
        return reporteInventarioServicio.reporteBajoStock(limite);
    }

    // Reporte por categoria
    @GetMapping("/por-categoria")
    public Map<Integer, List<ProductoDTO>> reportePorCategoria() {
        return reporteInventarioServicio.reportePorCategoria();
    }

    // Reporte Inventario total
    @GetMapping("/total")
    public List<ProductoDTO> reporteInventarioTotal(){
        return reporteInventarioServicio.reporteInventarioTotal();
    }

}
