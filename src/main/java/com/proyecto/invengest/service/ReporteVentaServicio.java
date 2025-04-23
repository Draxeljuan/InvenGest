package com.proyecto.invengest.service;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.invengest.dto.DetalleVentaDTO;
import com.proyecto.invengest.dto.ProductoMasVendidoDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.entities.Venta;
import com.proyecto.invengest.repository.VentaRepositorio;
import org.springframework.stereotype.Service;


import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteVentaServicio {

    private final VentaRepositorio ventaRepositorio;

    public ReporteVentaServicio(VentaRepositorio ventaRepositorio) {
        this.ventaRepositorio = ventaRepositorio;
    }

    // Metodo para generar reporte en PDF
    public void generarReporteVentas(String destino, LocalDate fechaInicio, LocalDate fechaFin, int minVentas) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(destino));
            document.open();

            // Encabezado del reporte
            document.add(new Paragraph("Reporte de Ventas"));
            document.add(new Paragraph("Periodo: " + fechaInicio + " - " + fechaFin));
            document.add(new Paragraph("---------------------------------------------------"));

            // Ajustar tipo de dato fecha
            Date sqlFechaInicio = Date.valueOf(fechaInicio);
            Date sqlFechaFin = Date.valueOf(fechaFin);

            // Obtener ventas en el rango de fechas
            List<VentaDTO> ventas = ventaRepositorio.obtenerVentasPorFecha(sqlFechaInicio, sqlFechaFin)
                    .stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());

            // Sección: Listado de ventas
            document.add(new Paragraph("📌 Ventas realizadas"));
            for (VentaDTO venta : ventas) {
                document.add(new Paragraph("ID Venta: " + venta.getIdVenta()));
                document.add(new Paragraph("Fecha: " + venta.getFecha()));
                document.add(new Paragraph("Cliente ID: " + venta.getIdCliente()));
                document.add(new Paragraph("Total: $" + venta.getTotal()));
                document.add(new Paragraph("---------------------------------------------------"));
            }

            // Sección: Productos más vendidos
            List<ProductoMasVendidoDTO> productosMasVendidos = ventaRepositorio.obtenerProductosMasVendidos(sqlFechaInicio, sqlFechaFin, minVentas)
                    .stream()
                    .map(this::convertirAProductoDTO)
                    .collect(Collectors.toList());

            if (!productosMasVendidos.isEmpty()) {
                document.add(new Paragraph("🔥 Productos más vendidos (mínimo " + minVentas + " unidades)"));
                for (ProductoMasVendidoDTO producto : productosMasVendidos) {
                    document.add(new Paragraph("Producto: " + producto.getNombreProducto() + " - Unidades Vendidas: " + producto.getCantidadVendida()));
                }
                document.add(new Paragraph("---------------------------------------------------"));
            }

            // Sección: Ingresos Totales
            double totalIngresos = ventaRepositorio.obtenerIngresosTotales(sqlFechaInicio, sqlFechaFin);
            document.add(new Paragraph("📊 Ingresos Totales: $" + totalIngresos));
            document.add(new Paragraph("---------------------------------------------------"));

            document.close();
            System.out.println("Reporte de ventas generado exitosamente en: " + destino);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    // Conversión a DTO
    private VentaDTO convertirADTO(Venta venta) {
        List<DetalleVentaDTO> detallesDTO = venta.getDetalleVentas()
                .stream()
                .map(detalle -> new DetalleVentaDTO(
                        detalle.getIdProducto().getNombre(),
                        detalle.getSubtotal(),
                        detalle.getPrecioUnitario(),
                        detalle.getCantidad()
                ))
                .collect(Collectors.toList());

        return new VentaDTO(
                venta.getIdVenta(),
                venta.getIdUsuario().getIdUsuario(),
                venta.getFecha(),
                venta.getIdCliente().getIdCliente(),
                venta.getTotal(),
                detallesDTO
        );
    }

    private ProductoMasVendidoDTO convertirAProductoDTO(Object[] resultado) {
        return new ProductoMasVendidoDTO(
                resultado[0].toString(), // ID del producto
                resultado[1].toString(), // Nombre del producto
                Integer.parseInt(resultado[2].toString()) // Cantidad vendida
        );
    }
}
