package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.dto.TipoProductoRequest;
import com.example.Licoreria_backend.dto.TipoProductoResponse;
import com.example.Licoreria_backend.model.TipoProducto;
import com.example.Licoreria_backend.service.TipoProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipos-producto")
public class TipoProductoController {

    private final TipoProductoService tipoProductoService;

    public TipoProductoController(TipoProductoService tipoProductoService) {
        this.tipoProductoService = tipoProductoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoProductoResponse>> listar() {
        List<TipoProductoResponse> lista = tipoProductoService.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TipoProductoRequest request) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        TipoProducto tipo = new TipoProducto();
        tipo.setNombre(request.getNombre());

        TipoProducto guardado = tipoProductoService.guardar(tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody TipoProductoRequest request) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        return tipoProductoService.obtenerPorId(id)
                .map(t -> {
                    t.setNombre(request.getNombre());
                    TipoProducto actualizado = tipoProductoService.guardar(t);
                    return ResponseEntity.ok(toResponse(actualizado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        try {
            tipoProductoService.eliminar(id);
            return ResponseEntity.ok("Tipo de producto eliminado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se puede eliminar, está en uso.");
        }
    }

    private TipoProductoResponse toResponse(TipoProducto tipo) {
        TipoProductoResponse r = new TipoProductoResponse();
        r.setId(tipo.getId());
        r.setNombre(tipo.getNombre());
        return r;
    }

    private boolean esAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
