package com.proyecto.invengest.controllers;

import com.proyecto.invengest.dto.FacturaDTO;
import com.proyecto.invengest.service.FacturaServicio;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facturas")
public class FacturaControlador {

    private final FacturaServicio facturaServicio;

    public FacturaControlador(FacturaServicio facturaServicio) {
        this.facturaServicio = facturaServicio;
    }

    // Endpoint para generar y descargar factura en PDF
    @GetMapping("/{idVenta}/generar-pdf")
    public ResponseEntity<byte[]> generarFactura(@PathVariable int idVenta) {

        System.out.println("Generando factura con el ID de Venta: " + idVenta);
        try {
            //  Generar la FacturaDTO desde la venta
            FacturaDTO factura = facturaServicio.generarFacturaDesdeVenta(idVenta);

            // Generar el PDF de la factura
            byte[] facturaPDF = facturaServicio.generarFacturaPDF(factura);

            // Configurar encabezados HTTP para la descarga
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Factura_" + idVenta + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            // Enviar el archivo PDF al cliente
            return new ResponseEntity<>(facturaPDF, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Enviar error si no se encuentra la venta
        }
    }
}
