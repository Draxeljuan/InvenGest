package com.proyecto.invengest.service;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.proyecto.invengest.dto.DetalleVentaDTO;
import com.proyecto.invengest.dto.ProductoMasVendidoDTO;
import com.proyecto.invengest.dto.ReporteDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.entities.Reporte;
import com.proyecto.invengest.entities.TipoReporte;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.entities.Venta;
import com.proyecto.invengest.repository.ReporteRepositorio;
import com.proyecto.invengest.repository.TipoReporteRepositorio;
import com.proyecto.invengest.repository.UsuarioRepositorio;
import com.proyecto.invengest.repository.VentaRepositorio;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteVentaServicio {

    private final VentaRepositorio ventaRepositorio;
    private final ReporteRepositorio reporteRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final TipoReporteRepositorio tipoReporteRepositorio;

    public ReporteVentaServicio(VentaRepositorio ventaRepositorio, ReporteRepositorio reporteRepositorio, UsuarioRepositorio usuarioRepositorio, TipoReporteRepositorio tipoReporteRepositorio) {
        this.ventaRepositorio = ventaRepositorio;
        this.reporteRepositorio = reporteRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.tipoReporteRepositorio = tipoReporteRepositorio;
    }

    // Metodo para generar reporte en PDF
    public void generarReporteVentas(String destino, LocalDate fechaInicio, LocalDate fechaFin, int minVentas, int idUsuario) {
        Document document = new Document();

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font subtituloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(destino));
            document.open();

            // 📌 Encabezado estilizado
            Paragraph titulo = new Paragraph("📊 Reporte de Ventas", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph periodo = new Paragraph("📅 Período: " + fechaInicio + " - " + fechaFin, subtituloFont);
            periodo.setAlignment(Element.ALIGN_CENTER);
            document.add(periodo);

            document.add(new Paragraph("---------------------------------------------------", normalFont));

            // 🔹 Obtener ventas
            List<VentaDTO> ventas = ventaRepositorio.obtenerVentasPorFecha(fechaInicio, fechaFin)
                    .stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());

            // 🔹 Resumen detallado
            document.add(new Paragraph("🔎 Resumen de Ventas", subtituloFont));
            document.add(new Paragraph("✅ Total de ventas realizadas: " + ventas.size(), normalFont));

            BigDecimal totalIngresos = new BigDecimal(ventaRepositorio.obtenerIngresosTotales(fechaInicio, fechaFin));
            BigDecimal ingresosPromedio = ventas.size() > 0
                    ? totalIngresos.divide(BigDecimal.valueOf(ventas.size()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            document.add(new Paragraph("💲 Ingreso promedio por venta: $" + ingresosPromedio, normalFont));

            int numeroClientes = ventaRepositorio.obtenerNumeroClientesUnicos(fechaInicio, fechaFin);
            document.add(new Paragraph("👥 Número de clientes en ventas: " + numeroClientes, normalFont));

            document.add(new Paragraph("---------------------------------------------------", normalFont));

            // Productos más vendidos

            List<Object[]> resultados = ventaRepositorio.obtenerProductosMasVendidos(fechaInicio, fechaFin);

            System.out.println("📌 Datos obtenidos desde la consulta:");
            for (Object[] obj : resultados) {
                System.out.println("🔹 ID: " + obj[0] + " | Nombre: " + obj[1] + " | Cantidad Vendida: " + obj[2]);
            }

            List<ProductoMasVendidoDTO> productosMasVendidos = resultados
                    .stream()
                    .map(obj -> new ProductoMasVendidoDTO(
                            obj[0] != null ? obj[0].toString() : "Desconocido",
                            obj[1] != null ? obj[1].toString() : "Desconocido",
                            obj[2] != null ? Integer.parseInt(obj[2].toString()) : 0
                    ))
                    .filter(producto -> producto.getCantidadVendida() >= 1) // Filtramos si queremos
                    .sorted(Comparator.comparingInt(ProductoMasVendidoDTO::getCantidadVendida).reversed()) // Ordenamos por cantidad vendida (descendente)
                    .collect(Collectors.toList());

            if (!productosMasVendidos.isEmpty()) {
                document.add(new Paragraph("📌 Productos Más Vendidos", subtituloFont));

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                table.addCell(new PdfPCell(new Phrase("Producto", subtituloFont)));
                table.addCell(new PdfPCell(new Phrase("Unidades Vendidas", subtituloFont)));

                for (ProductoMasVendidoDTO producto : productosMasVendidos) {
                    table.addCell(new PdfPCell(new Phrase(producto.getNombreProducto(), normalFont)));
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(producto.getCantidadVendida()), normalFont)));
                }

                document.add(table);
            }

            document.add(new Paragraph("---------------------------------------------------", normalFont));

            document.close();
            System.out.println("✅ Reporte de ventas generado exitosamente en: " + destino);

            // 🔹 Guardar reporte en BD
            ReporteDTO reporteDTO = new ReporteDTO(
                    0, idUsuario, destino, 1,
                    "Periodo: " + fechaInicio + " - " + fechaFin, destino,
                    LocalDate.now(), LocalDate.now()
            );
            guardarReporteEnBD(reporteDTO);

        } catch (DocumentException | IOException e) {
            System.err.println("⚠ Error crítico al generar el reporte de ventas: " + e.getMessage());
            throw new RuntimeException("Error crítico al generar el reporte de ventas. Verifica los datos y el acceso al archivo.");
        }
    }

    private JFreeChart crearGraficoProductosMasVendidos(List<ProductoMasVendidoDTO> productosMasVendidos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        productosMasVendidos.stream()
                .limit(5) // Limitamos a los 5 más vendidos
                .forEach(producto -> dataset.addValue(producto.getCantidadVendida(), "Ventas", producto.getNombreProducto()));

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Productos Más Vendidos",
                "Producto",
                "Cantidad Vendida",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        return chart;
    }

    private void agregarGraficoAlPDF(Document document, JFreeChart chart) throws IOException, DocumentException {
        File chartFile = new File("graficoVentas.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 400, 300);

        Image chartImage = Image.getInstance(chartFile.getAbsolutePath());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);
        chartFile.delete();
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

    // Guardar Informacion del Reporte Generado
    public void guardarReporteEnBD(ReporteDTO reporteDTO) {
        Reporte reporte = convertirAEntidad(reporteDTO); // Convertimos DTO a entidad
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
