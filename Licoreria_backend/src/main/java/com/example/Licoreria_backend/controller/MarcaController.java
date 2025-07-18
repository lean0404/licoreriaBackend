package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.dto.MarcaRequest;
import com.example.Licoreria_backend.dto.MarcaResponse;
import com.example.Licoreria_backend.model.Marca;
import com.example.Licoreria_backend.service.MarcaService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/marcas")
@CrossOrigin(origins = "http://localhost:4200")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    @GetMapping
    public ResponseEntity<List<MarcaResponse>> listar() {
        List<MarcaResponse> lista = marcaService.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MarcaRequest request) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        Marca marca = new Marca();
        marca.setNombre(request.getNombre());

        Marca guardado = marcaService.guardar(marca);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MarcaRequest request) {
        if (!esAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        return marcaService.obtenerPorId(id)
                .map(m -> {
                    m.setNombre(request.getNombre());
                    Marca actualizado = marcaService.guardar(m);
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
            marcaService.eliminar(id);
            return ResponseEntity.ok("Marca eliminada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se puede eliminar, está en uso.");
        }
    }

    private MarcaResponse toResponse(Marca marca) {
        MarcaResponse r = new MarcaResponse();
        r.setId(marca.getId());
        r.setNombre(marca.getNombre());
        return r;
    }

    private boolean esAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
