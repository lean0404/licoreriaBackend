package com.example.Licoreria_backend.controller;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.awt.Color;
import java.io.IOException;
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
        // fallback manual
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
        if (authentication == null) {
            throw new RuntimeException("No hay usuario autenticado");
        }

        Usuario vendedor = usuarioService.obtenerPorUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Construir detalles
        List<DetalleVenta> detalles = new ArrayList<>();
        for (ItemVentaRequest item : request.getItems()) {
            Producto p = productoService.obtenerPorId(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + item.getProductoId()));
            detalles.add(new DetalleVenta(null, item.getCantidad(), p.getPrecio(), p, null));
        }

        Venta venta = new Venta();
        venta.setMetodoPago(request.getMetodoPago());

        Venta ventaGuardada = ventaService.registrarVenta(venta, detalles, vendedor);

        generarPdfVenta(ventaGuardada, request, response);
    }

    private void generarPdfVenta(Venta venta, VentaRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=venta_" + venta.getId() + ".pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("Licorería - " + request.getTipoComprobante(), titleFont));
        document.add(new Paragraph("Fecha: " + venta.getFecha(), normalFont));
        document.add(new Paragraph("Cliente (" + request.getTipoComprobante() + "): " + request.getDocumentoCliente(), normalFont));
        document.add(new Paragraph("Vendedor: " + venta.getVendedor().getUsername(), normalFont));
        document.add(new Paragraph("Método de Pago: " + venta.getMetodoPago(), normalFont));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1.5f, 2, 2});
        table.addCell(getHeaderCell("Producto"));
        table.addCell(getHeaderCell("Cant."));
        table.addCell(getHeaderCell("P. Unit."));
        table.addCell(getHeaderCell("Subtotal"));

        double total = 0;
        int totalProductos = 0;
        for (DetalleVenta det : venta.getDetalles()) {
            double subtotal = det.getCantidad() * det.getPrecioUnitario();
            total += subtotal;
            totalProductos += det.getCantidad();

            table.addCell(new PdfPCell(new Phrase(det.getProducto().getNombre(), normalFont)));
            table.addCell(new PdfPCell(new Phrase(det.getCantidad().toString(), normalFont)));
            table.addCell(new PdfPCell(new Phrase("S/ " + det.getPrecioUnitario(), normalFont)));
            table.addCell(new PdfPCell(new Phrase("S/ " + subtotal, normalFont)));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Total Productos: " + totalProductos, normalFont));
        document.add(new Paragraph("Total Venta: S/ " + total, titleFont));
        document.close();
    }

    private PdfPCell getHeaderCell(String text) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(new Color(211, 211, 211));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
