package com.proyecto.invengest.controllers;



import com.proyecto.invengest.service.ReporteInventarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;


@RestController
@RequestMapping("/reportes/inventario")
public class ReporteInventarioControlador {

    private final ReporteInventarioServicio reporteInventarioServicio;

    public ReporteInventarioControlador(ReporteInventarioServicio reporteInventarioServicio) {
        this.reporteInventarioServicio = reporteInventarioServicio;
    }

    // Nuevo endpoint para generar el PDF del reporte general
    @GetMapping("/generar-pdf")
    public ResponseEntity<String> generarReporteInventario(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(defaultValue = "10") int limiteStock) {

        try {
            // Convertir las fechas de String a LocalDate
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);

            // Ruta donde se generará el PDF
            String destino = "ReporteInventario_" + fechaInicio + "_to_" + fechaFin + ".pdf";

            // Generar el reporte en PDF
            reporteInventarioServicio.generarReporteInventarioGeneral(destino, inicio, fin, limiteStock);

            return ResponseEntity.ok("Reporte PDF generado en: " + destino);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el PDF: " + e.getMessage());
        }
    }
}
