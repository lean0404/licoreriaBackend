package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.dto.MetodoPagoMasUsadoDTO;
import com.example.Licoreria_backend.dto.ProductoReporteDTO;
import com.example.Licoreria_backend.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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


}
