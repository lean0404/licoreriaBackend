package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.model.TipoProducto;
import com.example.Licoreria_backend.service.TipoProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-producto")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TipoProductoController {

    private final TipoProductoService tipoProductoService;

    public TipoProductoController(TipoProductoService tipoProductoService) {
        this.tipoProductoService = tipoProductoService;
    }

    @GetMapping
    public List<TipoProducto> listar() {
        return tipoProductoService.listarTodos();
    }

    @GetMapping("/{id}")
    public TipoProducto obtenerPorId(@PathVariable Long id) {
        return tipoProductoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("TipoProducto no encontrado con id " + id));
    }
}
