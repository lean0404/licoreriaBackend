package com.example.Licoreria_backend.service;

import com.example.Licoreria_backend.dto.VentaRequest;
import com.example.Licoreria_backend.model.*;
import com.example.Licoreria_backend.repository.ProductoRepository;
import com.example.Licoreria_backend.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Venta crearVenta(VentaRequest request, Usuario vendedor) {
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setVendedor(vendedor);

        List<DetalleVenta> detalles = request.getDetalles().stream().map(d -> {
            Producto producto = productoRepository.findById(d.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            producto.setStock(producto.getStock() - d.getCantidad());
            productoRepository.save(producto);

            return new DetalleVenta(null, d.getCantidad(), d.getPrecioUnitario(), producto, venta);
        }).collect(Collectors.toList());

        venta.setDetalles(detalles);
        return ventaRepository.save(venta);
    }
}
