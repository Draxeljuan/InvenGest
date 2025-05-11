package com.proyecto.invengest.controllers.reportes;



import com.proyecto.invengest.dto.ReporteDTO;
import com.proyecto.invengest.service.reportes.ReporteVentaServicio;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;


@RestController
@RequestMapping("/reportes/venta")
public class ReporteVentaControlador {

    private final ReporteVentaServicio reporteVentaServicio;

    public ReporteVentaControlador(ReporteVentaServicio reporteVentaServicio) {
        this.reporteVentaServicio = reporteVentaServicio;
    }

    // Nuevo endpoint para generar y descargar el PDF
    @GetMapping("/generar-pdf")
    public ResponseEntity<byte[]> generarReporteVentas(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(defaultValue = "10") int minVentas,
            @RequestParam int idUsuario) {

        try {
            // Conversión de fechas
            LocalDate FechaInicio = LocalDate.parse(fechaInicio);
            LocalDate FechaFin = LocalDate.parse(fechaFin);

            // Nombre del archivo PDF
            String nombreArchivo = "ReporteVentas_" + fechaInicio + "_to_" + fechaFin + ".pdf";
            String destino = System.getProperty("user.home") + "/Downloads/ReporteVentas_" + fechaInicio + "_to_" + fechaFin + ".pdf";

            // Generar el reporte en PDF y guardarlo en la BD
            reporteVentaServicio.generarReporteVentas(destino, FechaInicio, FechaFin, minVentas, idUsuario);

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

    // Guardar Reporte tras generarlo
    @PostMapping("/guardar-reporte")
    public ResponseEntity<String> guardarReporte(@RequestBody ReporteDTO reporteDTO) {
        try {
            reporteVentaServicio.guardarReporteEnBD(reporteDTO);
            return ResponseEntity.ok(" Reporte almacenado exitosamente en la BD.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" Error al guardar el reporte: " + e.getMessage());
        }
    }
}
