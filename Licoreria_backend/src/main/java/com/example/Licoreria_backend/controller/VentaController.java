package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.dto.VentaRequest;
import com.example.Licoreria_backend.model.Usuario;
import com.example.Licoreria_backend.model.Venta;
import com.example.Licoreria_backend.service.VentaService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<byte[]> crearVenta(@RequestBody VentaRequest request, HttpSession session) {
        Usuario vendedor = (Usuario) session.getAttribute("usuario");
        if (vendedor == null) {
            return ResponseEntity.status(401).build();
        }

        Venta venta = ventaService.crearVenta(request, vendedor);
        byte[] pdfBytes = generarBoletaPDF(venta);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "boleta_venta.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    private byte[] generarBoletaPDF(Venta venta) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            doc.add(new Paragraph("BOLETA DE VENTA"));
            doc.add(new Paragraph("Fecha: " + venta.getFecha()));
            doc.add(new Paragraph("Método de Pago: " + venta.getMetodoPago()));
            doc.add(new Paragraph("Vendedor: " + venta.getVendedor().getUsername()));
            doc.add(new Paragraph(" "));

            for (var detalle : venta.getDetalles()) {
                doc.add(new Paragraph(detalle.getCantidad() + " x " +
                        detalle.getProducto().getNombre() +
                        " - S/." + detalle.getPrecioUnitario()));
            }

            double total = venta.getDetalles().stream()
                    .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                    .sum();
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Total: S/. " + total));

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }
}
