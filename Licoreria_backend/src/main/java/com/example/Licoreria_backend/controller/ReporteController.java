package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.dto.MetodoPagoMasUsadoDTO;
import com.example.Licoreria_backend.dto.ProductoReporteDTO;
import com.example.Licoreria_backend.service.ReporteService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/producto-mas-vendido")
    public List<ProductoReporteDTO> getProductoMasVendido(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.obtenerProductosMasVendidos(desde, hasta);
    }

    @GetMapping("/producto-menos-vendido")
    public List<ProductoReporteDTO> getProductoMenosVendido(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.obtenerProductosMenosVendidos(desde, hasta);
    }

    @GetMapping("/metodo-pago-mas-usado")
    public MetodoPagoMasUsadoDTO getMetodoPagoMasUsado() {
        return reporteService.obtenerMetodoPagoMasUsado();
    }

    // 📄 Exportar productos más vendidos en PDF
    @GetMapping("/producto-mas-vendido/pdf")
    public void exportarMasVendidosPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpServletResponse response
    ) throws IOException {
        List<ProductoReporteDTO> productos = reporteService.obtenerProductosMasVendidos(desde, hasta);
        generarPdfReporteProductos("Productos Más Vendidos", productos, desde, hasta, response);
    }

    // 📄 Exportar productos menos vendidos en PDF
    @GetMapping("/producto-menos-vendido/pdf")
    public void exportarMenosVendidosPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpServletResponse response
    ) throws IOException {
        List<ProductoReporteDTO> productos = reporteService.obtenerProductosMenosVendidos(desde, hasta);
        generarPdfReporteProductos("Productos Menos Vendidos", productos, desde, hasta, response);
    }

    // 📄 Método privado reutilizable para generar el PDF
    private void generarPdfReporteProductos(String titulo, List<ProductoReporteDTO> productos,
                                            LocalDate desde, LocalDate hasta,
                                            HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + titulo.replace(" ", "_").toLowerCase() + ".pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph(titulo, titleFont));
        document.add(new Paragraph("Desde: " + desde + " - Hasta: " + hasta, normalFont));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 5, 2});

        table.addCell("#");
        table.addCell("Producto");
        table.addCell("Cantidad Vendida");

        int index = 1;
        for (ProductoReporteDTO p : productos) {
            table.addCell(String.valueOf(index++));
            table.addCell(p.getNombre());
            table.addCell(String.valueOf(p.getCantidadVendida()));
        }

        document.add(table);
        document.close();
    }
}
