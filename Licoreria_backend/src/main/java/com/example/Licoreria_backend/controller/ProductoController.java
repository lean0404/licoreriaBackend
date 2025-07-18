package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.dto.ProductoRequest;
import com.example.Licoreria_backend.dto.ProductoResponse;
import com.example.Licoreria_backend.model.Marca;
import com.example.Licoreria_backend.model.Producto;
import com.example.Licoreria_backend.model.TipoProducto;
import com.example.Licoreria_backend.service.MarcaService;
import com.example.Licoreria_backend.service.ProductoService;
import com.example.Licoreria_backend.service.TipoProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final TipoProductoService tipoProductoService;
    private final MarcaService marcaService;

    public ProductoController(ProductoService productoService,
                              TipoProductoService tipoProductoService,
                              MarcaService marcaService) {
        this.productoService = productoService;
        this.tipoProductoService = tipoProductoService;
        this.marcaService = marcaService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> obtenerProductos() {
        List<Producto> productos = productoService.obtenerTodos();
        List<ProductoResponse> respuesta = productos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody ProductoRequest request) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }
        Producto producto = toEntity(request, null);
        Producto guardado = productoService.guardar(producto);
        return ResponseEntity.ok(toResponse(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequest request) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }
        return productoService.obtenerPorId(id)
                .map(p -> {
                    Producto productoActualizado = toEntity(request, id);
                    Producto actualizado = productoService.guardar(productoActualizado);
                    return ResponseEntity.ok(toResponse(actualizado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }
        productoService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado");
    }

    // Helpers
    private ProductoResponse toResponse(Producto p) {
        ProductoResponse r = new ProductoResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setDescripcion(p.getDescripcion());
        r.setPrecio(p.getPrecio());
        r.setStock(p.getStock());

        if (p.getTipoProducto() != null) {
            r.setTipoProductoId(p.getTipoProducto().getId());
            r.setTipoProductoNombre(p.getTipoProducto().getNombre());
        }

        if (p.getMarca() != null) {
            r.setMarcaId(p.getMarca().getId());
            r.setMarcaNombre(p.getMarca().getNombre());
        }

        return r;
    }

    private Producto toEntity(ProductoRequest request, Long id) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(request.getNombre());
        p.setDescripcion(request.getDescripcion());
        p.setPrecio(request.getPrecio());
        p.setStock(request.getStock());

        if (request.getTipoProductoId() != null) {
            TipoProducto tipo = tipoProductoService.obtenerPorId(request.getTipoProductoId())
                    .orElseThrow(() -> new RuntimeException("TipoProducto no encontrado con id " + request.getTipoProductoId()));
            p.setTipoProducto(tipo);
        }

        if (request.getMarcaId() != null) {
            Marca marca = marcaService.obtenerPorId(request.getMarcaId())
                    .orElseThrow(() -> new RuntimeException("Marca no encontrada con id " + request.getMarcaId()));
            p.setMarca(marca);
        }

        return p;
    }

    private boolean esAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
