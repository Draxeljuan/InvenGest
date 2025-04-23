package com.proyecto.invengest.service;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.repository.ProductoRepositorio;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteInventarioServicio {

    private final ProductoRepositorio productoRepositorio;

    public ReporteInventarioServicio(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    // Metodo para generar pdf
    public void generarReporteInventarioGeneral(String destino, LocalDate fechaInicio, LocalDate fechaFin, int limiteStock) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(destino));
            document.open();

            // Encabezado del reporte
            document.add(new Paragraph("Reporte General de Inventario"));
            document.add(new Paragraph("Período: " + fechaInicio + " - " + fechaFin));
            document.add(new Paragraph("---------------------------------------------------"));

            // Filtrar productos por fecha de ingreso
            List<ProductoDTO> productosFiltrados = productoRepositorio.findAll()
                    .stream()
                    .filter(producto -> producto.getFechaIngreso().isAfter(fechaInicio.minusDays(1)) &&
                            producto.getFechaIngreso().isBefore(fechaFin.plusDays(1)))
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());

            // Sección: Listado de productos filtrados
            document.add(new Paragraph("📌 Inventario Total"));
            for (ProductoDTO producto : productosFiltrados) {
                document.add(new Paragraph("ID: " + producto.getIdProducto()));
                document.add(new Paragraph("Nombre: " + producto.getNombre()));
                document.add(new Paragraph("Categoría: " + producto.getIdCategoria()));
                document.add(new Paragraph("Stock: " + producto.getStock()));
                document.add(new Paragraph("Precio: $" + producto.getPrecioVenta()));
                document.add(new Paragraph("Fecha Ingreso: " + producto.getFechaIngreso()));
                document.add(new Paragraph("---------------------------------------------------"));
            }

            // Sección: Productos con bajo stock
            List<ProductoDTO> bajoStock = productosFiltrados.stream()
                    .filter(producto -> producto.getStock() <= limiteStock)
                    .collect(Collectors.toList());

            if (!bajoStock.isEmpty()) {
                document.add(new Paragraph("⚠ Productos con bajo stock (≤ " + limiteStock + ")"));
                for (ProductoDTO producto : bajoStock) {
                    document.add(new Paragraph("Nombre: " + producto.getNombre() + " - Stock: " + producto.getStock()));
                }
                document.add(new Paragraph("---------------------------------------------------"));
            }

            // Sección: Inventario por categoría
            Map<Integer, List<ProductoDTO>> productosPorCategoria = productosFiltrados.stream()
                    .collect(Collectors.groupingBy(ProductoDTO::getIdCategoria));

            document.add(new Paragraph("📂 Inventario por Categoría"));
            for (Map.Entry<Integer, List<ProductoDTO>> entry : productosPorCategoria.entrySet()) {
                document.add(new Paragraph("Categoría ID: " + entry.getKey()));
                for (ProductoDTO producto : entry.getValue()) {
                    document.add(new Paragraph(" - " + producto.getNombre() + " (Stock: " + producto.getStock() + ")"));
                }
                document.add(new Paragraph("---------------------------------------------------"));
            }

            // Sección: Totales y métricas generales
            int totalStock = productosFiltrados.stream().mapToInt(ProductoDTO::getStock).sum();
            BigDecimal valorTotalInventario = productosFiltrados.stream()
                    .map(p -> p.getPrecioVenta().multiply(BigDecimal.valueOf(p.getStock())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            document.add(new Paragraph("📊 Resumen del inventario"));
            document.add(new Paragraph("Stock total: " + totalStock));
            document.add(new Paragraph("Valor total del inventario: $" + valorTotalInventario));
            document.add(new Paragraph("---------------------------------------------------"));

            document.close();
            System.out.println("Reporte generado exitosamente: " + destino);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    // Conversión a DTO
    private ProductoDTO convertirADTO(Producto producto) {
        return new ProductoDTO(
                producto.getIdProducto(),
                producto.getIdCategoria().getIdCategoria(),
                producto.getNombre(),
                producto.getPrecioVenta(),
                producto.getCostoCompra(),
                producto.getFechaIngreso(),
                producto.getStock(),
                producto.getUbicacion(),
                producto.getIdEstado().getIdEstado()
        );
    }
}
