package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.dto.VentaHistorialDTO;
import com.example.Licoreria_backend.dto.VentaRequest;
import com.example.Licoreria_backend.dto.ItemVentaRequest;
import com.example.Licoreria_backend.model.DetalleVenta;
import com.example.Licoreria_backend.model.Producto;
import com.example.Licoreria_backend.model.Usuario;
import com.example.Licoreria_backend.model.Venta;
import com.example.Licoreria_backend.service.ProductoService;
import com.example.Licoreria_backend.service.UsuarioService;
import com.example.Licoreria_backend.service.VentaService;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public VentaController(VentaService ventaService, UsuarioService usuarioService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    @PostMapping
    public void crearVenta(@RequestBody VentaRequest request, Authentication authentication, HttpServletResponse response) throws IOException {
        Usuario vendedor = getUsuarioAutenticado(authentication);

        List<DetalleVenta> detalles = new ArrayList<>();
        for (ItemVentaRequest item : request.getItems()) {
            Producto p = productoService.obtenerPorId(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID " + item.getProductoId()));
            detalles.add(new DetalleVenta(null, item.getCantidad(), p.getPrecio(), p, null));
        }

        Venta venta = new Venta();
        venta.setMetodoPago(request.getMetodoPago());
        venta.setTipoComprobante(request.getTipoComprobante());
        venta.setDocumentoCliente(request.getDocumentoCliente());

        Venta ventaGuardada = ventaService.registrarVenta(venta, detalles, vendedor);

        generarPdfBoletaVenta(ventaGuardada, response);
    }

    @GetMapping("/mis-ventas")
    public List<VentaHistorialDTO> obtenerMisVentas(Authentication authentication) {
        Usuario vendedor = getUsuarioAutenticado(authentication);
        return ventaService.obtenerHistorialPorVendedor(vendedor);
    }

    @GetMapping("/mis-ventas/por-fechas")
    public List<VentaHistorialDTO> obtenerMisVentasPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Authentication authentication) {
        Usuario vendedor = getUsuarioAutenticado(authentication);
        return ventaService.obtenerHistorialPorVendedorYFechas(vendedor, desde, hasta);
    }

    @GetMapping("/mis-ventas/por-fechas/pdf")
    public void emitirReporteVentasPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Authentication authentication,
            HttpServletResponse response
    ) throws IOException {
        Usuario vendedor = getUsuarioAutenticado(authentication);
        List<VentaHistorialDTO> ventas = ventaService.obtenerHistorialPorVendedorYFechas(vendedor, desde, hasta);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_ventas_" + vendedor.getUsername() + ".pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("Reporte de Ventas", titleFont));
        document.add(new Paragraph("Vendedor: " + vendedor.getUsername(), normalFont));
        document.add(new Paragraph("Desde: " + desde + " - Hasta: " + hasta, normalFont));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2.5f, 1.5f, 2});
        addHeader(table, "ID", "Fecha", "Método", "Tipo", "Documento", "Total P.", "Total S/");

        double granTotal = 0;
        for (VentaHistorialDTO venta : ventas) {
            table.addCell(String.valueOf(venta.getId()));
            table.addCell(venta.getFecha().toString());
            table.addCell(venta.getMetodoPago());
            table.addCell(venta.getTipoComprobante());
            table.addCell(venta.getDocumentoCliente());
            table.addCell(String.valueOf(venta.getTotalProductos()));
            table.addCell("S/ " + String.format("%.2f", venta.getTotalVenta()));
            granTotal += venta.getTotalVenta();
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Total: S/ " + String.format("%.2f", granTotal), titleFont));
        document.close();
    }

    private Usuario getUsuarioAutenticado(Authentication authentication) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
        if (authentication == null) {
            throw new RuntimeException("No hay usuario autenticado");
        }
        return usuarioService.obtenerPorUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void generarPdfBoletaVenta(Venta venta, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=venta_" + venta.getId() + ".pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("Licorería - " + venta.getTipoComprobante(), titleFont));
        document.add(new Paragraph("Fecha: " + venta.getFecha(), normalFont));
        document.add(new Paragraph("Cliente: " + venta.getDocumentoCliente(), normalFont));
        document.add(new Paragraph("Vendedor: " + venta.getVendedor().getUsername(), normalFont));
        document.add(new Paragraph("Método de Pago: " + venta.getMetodoPago(), normalFont));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1.5f, 2, 2});
        addHeader(table, "Producto", "Cant.", "P. Unit.", "Subtotal");

        double total = 0;
        int totalProductos = 0;
        for (DetalleVenta det : venta.getDetalles()) {
            double subtotal = det.getCantidad() * det.getPrecioUnitario();
            total += subtotal;
            totalProductos += det.getCantidad();

            table.addCell(det.getProducto().getNombre());
            table.addCell(det.getCantidad().toString());
            table.addCell("S/ " + String.format("%.2f", det.getPrecioUnitario()));
            table.addCell("S/ " + String.format("%.2f", subtotal));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Total Productos: " + totalProductos, normalFont));
        document.add(new Paragraph("Total Venta: S/ " + String.format("%.2f", total), titleFont));
        document.close();
    }

    private void addHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(211, 211, 211));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
}
