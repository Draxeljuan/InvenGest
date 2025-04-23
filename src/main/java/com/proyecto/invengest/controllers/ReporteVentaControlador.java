package com.proyecto.invengest.controllers;


import com.proyecto.invengest.dto.ProductoMasVendidoDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.service.ReporteVentaServicio;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes/venta")
public class ReporteVentaControlador {

    private final ReporteVentaServicio reporteVentaServicio;

    public ReporteVentaControlador(ReporteVentaServicio reporteVentaServicio) {
        this.reporteVentaServicio = reporteVentaServicio;
    }

    // 📌 Nuevo endpoint para generar y descargar el PDF
    @GetMapping("/generar-pdf")
    public ResponseEntity<byte[]> generarReporteVentas(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(defaultValue = "10") int minVentas) {

        try {
            // ✅ Conversión de fechas
            LocalDate FechaInicio = LocalDate.parse(fechaInicio);
            LocalDate FechaFin = LocalDate.parse(fechaFin);

            // Nombre del archivo PDF
            String nombreArchivo = "ReporteVentas_" + fechaInicio + "_to_" + fechaFin + ".pdf";

            // Generar el reporte en PDF
            reporteVentaServicio.generarReporteVentas(nombreArchivo, FechaInicio, FechaFin, minVentas);

            // Leer el archivo generado y enviarlo como respuesta
            File archivo = new File(nombreArchivo);
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
