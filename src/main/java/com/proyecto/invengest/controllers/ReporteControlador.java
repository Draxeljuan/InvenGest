package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.ReporteDTO;
import com.proyecto.invengest.service.ReporteServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    public ReporteControlador(ReporteServicio reporteServicio) {
        this.reporteServicio = reporteServicio;
    }

    // Listar todos los reportes
    @GetMapping
    public List<ReporteDTO> listarReportes(){
        return reporteServicio.listarReportes();
    }

    // Buscar reporte por Id
    @GetMapping("/{id}")
    public ReporteDTO obtenerReporte(@PathVariable int id){
        return reporteServicio.obtenerReporte(id);
    }


    // Eliminar un reporte
    @DeleteMapping("/{id}")
    public void eliminarReporte(@PathVariable int id){
        reporteServicio.eliminarReporte(id);
    }


}
