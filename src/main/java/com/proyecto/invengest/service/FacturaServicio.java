package com.proyecto.invengest.service;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.invengest.dto.DetalleFacturaDTO;
import com.proyecto.invengest.dto.FacturaDTO;
import com.proyecto.invengest.entities.Venta;
import com.proyecto.invengest.repository.VentaRepositorio;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaServicio {

    private final VentaRepositorio ventaRepositorio;

    public FacturaServicio(VentaRepositorio ventaRepositorio) {
        this.ventaRepositorio = ventaRepositorio;
    }

    // Metodo para generar FacturaDTO desde una venta
    public FacturaDTO generarFacturaDesdeVenta(int idVenta) {
        Venta venta = ventaRepositorio.findVentaConDetalles(idVenta);

        if (venta == null) {
            throw new RuntimeException("Venta no encontrada con el ID: " + idVenta);
        }

        List<DetalleFacturaDTO> detallesDTO = venta.getDetalleVentas()
                .stream()
                .map(detalle -> new DetalleFacturaDTO(
                        detalle.getIdProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new FacturaDTO(
                venta.getIdVenta(),
                venta.getFecha(),
                venta.getIdCliente().getPrimerNombre(), // Adaptar si necesitas otro campo para el cliente
                venta.getIdCliente().getSegundoNombre(),
                venta.getIdCliente().getPrimerApellido(),
                venta.getIdCliente().getSegundoApellido(),
                venta.getTotal(),
                detallesDTO
        );
    }

    // Metodo para generar la factura en PDF
    public byte[] generarFacturaPDF(FacturaDTO factura) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Encabezado de la factura
            document.add(new Paragraph("Factura"));
            document.add(new Paragraph("ID Venta: " + factura.getIdVenta()));
            document.add(new Paragraph("Fecha: " + factura.getFecha()));
            String nombreCliente = factura.getPrimerNombreCliente() +
                    (factura.getSegundoNombreCliente() != null ? " " + factura.getSegundoNombreCliente() : "") +
                    (factura.getPrimerApellidoCliente() != null ? " " + factura.getPrimerApellidoCliente() : "") +
                    (factura.getSegundoApellidoCliente() != null ? " " + factura.getSegundoApellidoCliente() : "");
            document.add(new Paragraph("Cliente: " + nombreCliente));
            document.add(new Paragraph("---------------------------------------------------"));

            // Detalles de la factura
            document.add(new Paragraph("Detalles de la compra:"));
            for (DetalleFacturaDTO detalle : factura.getDetalles()) {
                document.add(new Paragraph("Producto: " + detalle.getNombreProducto()));
                document.add(new Paragraph("Cantidad: " + detalle.getCantidad()));
                document.add(new Paragraph("Precio unitario: $" + detalle.getPrecioUnitario()));
                document.add(new Paragraph("Subtotal: $" + detalle.getSubtotal()));
                document.add(new Paragraph("------------------------------"));
            }

            // Total de la factura
            document.add(new Paragraph("Total: $" + factura.getTotal()));
            document.add(new Paragraph("---------------------------------------------------"));

            document.close();

            return outputStream.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar la factura PDF con el ID" + factura.getIdVenta(), e);
        }
    }
}
