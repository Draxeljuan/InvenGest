package com.proyecto.invengest.controllers;



import com.proyecto.invengest.service.ReporteInventarioServicio;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    public ResponseEntity<byte[]> generarReporteInventario(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(defaultValue = "10") int limiteStock,
            @RequestParam int idUsuario) {

        try {
            // Conversión de fechas
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);

            // Nombre del archivo PDF
            String nombreArchivo = "ReporteInventario_" + fechaInicio + "_to_" + fechaFin + ".pdf";
            String destino = System.getProperty("user.home") + "/Downloads/ReporteInventario_" + fechaInicio + "_to_" + fechaFin + ".pdf";


            // Generar el reporte en PDF y guardarlo en la BD
            reporteInventarioServicio.generarReporteInventarioGeneral(destino, inicio, fin, limiteStock, idUsuario);

            // Leer el archivo generado y enviarlo como respuesta
            File archivo = new File(destino);
            FileInputStream inputStream = new FileInputStream(archivo);
            byte[] contenidoPDF = inputStream.readAllBytes();
            inputStream.close();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo);
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            return new ResponseEntity<>(contenidoPDF, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
