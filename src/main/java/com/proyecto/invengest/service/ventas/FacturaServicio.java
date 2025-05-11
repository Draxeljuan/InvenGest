package com.proyecto.invengest.service.ventas;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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
        System.out.println("🔍 Buscando venta con ID: " + idVenta);
        Venta venta = ventaRepositorio.findVentaConDetalles(idVenta);

        if (venta == null) {
            System.out.println("⚠️ Error: Venta no encontrada con el ID: " + idVenta);
            throw new RuntimeException("Venta no encontrada con el ID: " + idVenta);
        }

        System.out.println("✅ Venta encontrada: " + venta.toString());

        List<DetalleFacturaDTO> detallesDTO = venta.getDetalleVentas()
                .stream()
                .map(detalle -> {
                    System.out.println("🛒 Producto en factura: " + detalle.getIdProducto().getNombre());
                    return new DetalleFacturaDTO(
                            detalle.getIdProducto().getNombre(),
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            detalle.getSubtotal()
                    );
                })
                .collect(Collectors.toList());

        System.out.println("✅ Factura generada para venta ID: " + idVenta);

        return new FacturaDTO(
                venta.getIdVenta(),
                venta.getFecha(),
                venta.getIdCliente().getPrimerNombre(),
                venta.getIdCliente().getSegundoNombre(),
                venta.getIdCliente().getPrimerApellido(),
                venta.getIdCliente().getSegundoApellido(),
                venta.getTotal(),
                detallesDTO
        );
    }

    // Metodo para generar la factura en PDF
    public byte[] generarFacturaPDF(FacturaDTO factura) {
        System.out.println("📄 Generando factura PDF para venta ID: " + factura.getIdVenta());

        if (factura == null) {
            System.out.println(" Error: FacturaDTO es null, no se puede generar el PDF.");
            throw new RuntimeException("FacturaDTO es null para la venta ID: " + factura.getIdVenta());
        }

        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            //  Estilo de fuente
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            //  Encabezado de la factura
            Paragraph titulo = new Paragraph("Factura de Compra", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("ID Venta: " + factura.getIdVenta(), boldFont));
            document.add(new Paragraph("Fecha: " + factura.getFecha(), boldFont));

            // Datos del cliente
            String nombreCliente = factura.getPrimerNombreCliente() +
                    (factura.getSegundoNombreCliente() != null ? " " + factura.getSegundoNombreCliente() : "") +
                    (factura.getPrimerApellidoCliente() != null ? " " + factura.getPrimerApellidoCliente() : "") +
                    (factura.getSegundoApellidoCliente() != null ? " " + factura.getSegundoApellidoCliente() : "");

            document.add(new Paragraph("Cliente: " + nombreCliente, boldFont));
            document.add(new Paragraph("______________________________________________", normalFont));

            // Tabla para detalles de productos
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // 🔹 Encabezados de la tabla
            table.addCell(new PdfPCell(new Phrase("Producto", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Cantidad", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Precio Unitario", boldFont)));
            table.addCell(new PdfPCell(new Phrase("Subtotal", boldFont)));

            System.out.println("🛒 Añadiendo detalles de la compra:");

            for (DetalleFacturaDTO detalle : factura.getDetalles()) {
                System.out.println(" Producto: " + detalle.getNombreProducto() + " | Cantidad: " + detalle.getCantidad());

                table.addCell(new PdfPCell(new Phrase(detalle.getNombreProducto(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), normalFont)));
                table.addCell(new PdfPCell(new Phrase("$" + detalle.getPrecioUnitario(), normalFont)));
                table.addCell(new PdfPCell(new Phrase("$" + detalle.getSubtotal(), normalFont)));
            }

            document.add(table);

            // Total
            document.add(new Paragraph("Total: $" + factura.getTotal(), boldFont));

            document.close();

            System.out.println("PDF generado correctamente para venta ID: " + factura.getIdVenta());

            return outputStream.toByteArray();

        } catch (DocumentException e) {
            System.out.println(" Error al generar el PDF para venta ID: " + factura.getIdVenta());
            throw new RuntimeException("Error al generar la factura PDF con el ID " + factura.getIdVenta(), e);
        }
    }
}
