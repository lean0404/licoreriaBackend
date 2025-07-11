package com.example.Licoreria_backend.service;

import com.example.Licoreria_backend.model.DetalleVenta;
import com.example.Licoreria_backend.model.Producto;
import com.example.Licoreria_backend.model.Usuario;
import com.example.Licoreria_backend.model.Venta;
import com.example.Licoreria_backend.repository.ProductoRepository;
import com.example.Licoreria_backend.repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Venta registrarVenta(Venta venta, List<DetalleVenta> detalles, Usuario vendedor) {
        venta.setFecha(LocalDateTime.now());
        venta.setVendedor(vendedor);

        for (DetalleVenta detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID " + detalle.getProducto().getId()));

            // Actualiza stock
            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("No hay suficiente stock para el producto: " + producto.getNombre());
            }
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            detalle.setProducto(producto);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setVenta(venta);
        }

        venta.setDetalles(detalles);
        return ventaRepository.save(venta);
    }

    public Venta obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID " + id));
    }
}
