package com.proyecto.invengest.service.reportes;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.dto.ReporteDTO;
import com.proyecto.invengest.entities.*;
import com.proyecto.invengest.repository.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteInventarioServicio {

    private final ProductoRepositorio productoRepositorio;
    private final TipoReporteRepositorio tipoReporteRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ReporteRepositorio reporteRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;

    public ReporteInventarioServicio(ProductoRepositorio productoRepositorio, TipoReporteRepositorio tipoReporteRepositorio, UsuarioRepositorio usuarioRepositorio, ReporteRepositorio repo, CategoriaRepositorio categoriaRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.tipoReporteRepositorio = tipoReporteRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.reporteRepositorio = repo;
        this.categoriaRepositorio = categoriaRepositorio;
    }

    // Metodo para generar pdf
    public void generarReporteInventarioGeneral(String destino, LocalDate fechaInicio, LocalDate fechaFin, int limiteStock, int idUsuario) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(destino));
            document.open();

            // Estilizar encabezados
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font subtituloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            // Encabezado del reporte
            Paragraph titulo = new Paragraph("📊 Reporte General de Inventario", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph periodo = new Paragraph("📅 Período: " + fechaInicio + " - " + fechaFin, subtituloFont);
            periodo.setAlignment(Element.ALIGN_CENTER);
            document.add(periodo);

            document.add(new Paragraph("---------------------------------------------------", normalFont));

            // Obtener productos y calcular estadísticas
            List<ProductoDTO> productosFiltrados = productoRepositorio.obtenerProductosPorFecha(fechaInicio, fechaFin)
                    .stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());

            // Resumen del inventario
            document.add(new Paragraph("🔎 Resumen del inventario", subtituloFont));
            document.add(new Paragraph("✅ Total de productos: " + productosFiltrados.size(), normalFont));

            BigDecimal sumaPrecios = productosFiltrados.stream()
                    .map(ProductoDTO::getPrecioVenta)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal precioPromedio = productosFiltrados.size() > 0
                    ? sumaPrecios.divide(BigDecimal.valueOf(productosFiltrados.size()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            document.add(new Paragraph("💲 Precio promedio: $" + precioPromedio, normalFont));

            long productosStockCritico = productosFiltrados.stream().filter(p -> p.getStock() <= limiteStock).count();
            document.add(new Paragraph("⚠ Productos con stock crítico: " + productosStockCritico, normalFont));

            document.add(new Paragraph("---------------------------------------------------", normalFont));


            // Mapeo de IDs de categoría a sus nombres
            // Convertir Categoria a CategoriaDTO
            Map<Integer, String> categoriasMap = categoriaRepositorio.findAll().stream()
                    .collect(Collectors.toMap(Categoria::getIdCategoria, Categoria::getNombre));

            // Agrupar productos por nombre de categoría
            Map<String, Long> productosPorCategoria = productosFiltrados.stream()
                    .collect(Collectors.groupingBy(producto -> categoriasMap.get(producto.getIdCategoria()), Collectors.counting()));

            JFreeChart chart = crearGraficoCategorias(productosPorCategoria);
            agregarGraficoAlPDF(document, chart);

            // Tabla de productos con mejor formato
            if (productosFiltrados.isEmpty()) {
                document.add(new Paragraph("⚠ No hay productos en inventario dentro del período seleccionado.", normalFont));
            } else {
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.addCell(new PdfPCell(new Phrase("ID", subtituloFont)));
                table.addCell(new PdfPCell(new Phrase("Nombre", subtituloFont)));
                table.addCell(new PdfPCell(new Phrase("Categoría", subtituloFont)));
                table.addCell(new PdfPCell(new Phrase("Stock", subtituloFont)));
                table.addCell(new PdfPCell(new Phrase("Precio", subtituloFont)));

                for (ProductoDTO producto : productosFiltrados) {
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(producto.getIdProducto()), normalFont)));
                    table.addCell(new PdfPCell(new Phrase(producto.getNombre(), normalFont)));

                    // Obtener nombre de la categoría desde el mapa
                    String nombreCategoria = categoriasMap.get(producto.getIdCategoria());
                    table.addCell(new PdfPCell(new Phrase(nombreCategoria != null ? nombreCategoria : "Desconocido", normalFont)));

                    table.addCell(new PdfPCell(new Phrase(String.valueOf(producto.getStock()), normalFont)));
                    table.addCell(new PdfPCell(new Phrase("$" + producto.getPrecioVenta(), normalFont)));
                }

                document.add(table);
            }

            document.add(new Paragraph("---------------------------------------------------", normalFont));

            // Recomendaciones
            document.add(new Paragraph("📌 Recomendaciones para reposición", subtituloFont));
            productosFiltrados.stream()
                    .filter(p -> p.getStock() <= limiteStock)
                    .forEach(producto -> {
                        try {
                            document.add(new Paragraph("➡ Reponer stock de " + producto.getNombre() + " (ID: " + producto.getIdProducto() + ")", normalFont));
                        } catch (DocumentException e) {
                            throw new RuntimeException(e);
                        }
                    });

            document.close();
            System.out.println(" Reporte de inventario generado exitosamente en: " + destino);

            // Guardar reporte en BD
            ReporteDTO reporteDTO = new ReporteDTO(
                    0, idUsuario, destino, 2,
                    "Periodo: " + fechaInicio + " - " + fechaFin, destino,
                    LocalDate.now(), LocalDate.now()
            );
            guardarReporteEnBD(reporteDTO);

        } catch (DocumentException | IOException e) {
            System.err.println("⚠ Error crítico al generar el reporte de inventario: " + e.getMessage());
            throw new RuntimeException("Error crítico al generar el reporte de inventario. Verifica los datos y el acceso al archivo.");
        }
    }

    private JFreeChart crearGraficoCategorias(Map<String, Long> productosPorCategoria) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        productosPorCategoria.forEach((categoria, cantidad) -> {
            dataset.addValue(cantidad, "Productos", categoria);
        });

        JFreeChart chart = ChartFactory.createBarChart(
                "Distribución de Productos por Categoría",
                "Categoría",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        return chart;
    }

    private void agregarGraficoAlPDF(Document document, JFreeChart chart) throws IOException, DocumentException {
        File chartFile = new File("graficoInventario.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 400, 300);

        Image chartImage = Image.getInstance(chartFile.getAbsolutePath());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);
        chartFile.delete();
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
                producto.getIdEstado().getId()
        );
    }

    public void guardarReporteEnBD(ReporteDTO reporteDTO) {
        Reporte reporte = convertirAEntidad(reporteDTO);
        reporteRepositorio.save(reporte);
        System.out.println(" Reporte almacenado en la BD con nombre: " + reporte.getNombre());
    }

    public Reporte convertirAEntidad(ReporteDTO reporteDTO) {
        Reporte reporte = new Reporte();
        reporte.setNombre(reporteDTO.getNombre());
        reporte.setFechaCreacion(reporteDTO.getFechaCreacion());
        reporte.setUltimaEjecucion(reporteDTO.getUltimaEjecucion());
        reporte.setParametros(reporteDTO.getParametros());
        reporte.setContenido(reporteDTO.getContenido());

        // Asignar relaciones correctamente
        Usuario usuario = usuarioRepositorio.findById(reporteDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + reporteDTO.getIdUsuario()));
        reporte.setIdUsuario(usuario);

        TipoReporte tipo = tipoReporteRepositorio.findById(reporteDTO.getIdTipo())
                .orElseThrow(() -> new RuntimeException("Tipo de reporte no encontrado con ID: " + reporteDTO.getIdTipo()));
        reporte.setIdTipo(tipo);

        return reporte;
    }
}
