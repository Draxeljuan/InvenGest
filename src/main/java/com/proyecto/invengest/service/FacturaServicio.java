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
            System.out.println("⚠️ Error: FacturaDTO es null, no se puede generar el PDF.");
            throw new RuntimeException("FacturaDTO es null para la venta ID: " + factura.getIdVenta());
        }

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

            System.out.println("🛒 Añadiendo detalles de la compra:");
            for (DetalleFacturaDTO detalle : factura.getDetalles()) {
                System.out.println("🔹 Producto: " + detalle.getNombreProducto() + " | Cantidad: " + detalle.getCantidad());
                document.add(new Paragraph("Producto: " + detalle.getNombreProducto()));
                document.add(new Paragraph("Cantidad: " + detalle.getCantidad()));
                document.add(new Paragraph("Precio unitario: $" + detalle.getPrecioUnitario()));
                document.add(new Paragraph("Subtotal: $" + detalle.getSubtotal()));
                document.add(new Paragraph("------------------------------"));
            }

            document.add(new Paragraph("Total: $" + factura.getTotal()));
            document.add(new Paragraph("---------------------------------------------------"));

            document.close();

            System.out.println("✅ PDF generado correctamente para venta ID: " + factura.getIdVenta());

            return outputStream.toByteArray();

        } catch (DocumentException e) {
            System.out.println("⚠️ Error al generar el PDF para venta ID: " + factura.getIdVenta());
            throw new RuntimeException("Error al generar la factura PDF con el ID " + factura.getIdVenta(), e);
        }
    }
}
