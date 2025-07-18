package com.example.Licoreria_backend.service;

import com.example.Licoreria_backend.dto.MetodoPagoMasUsadoDTO;
import com.example.Licoreria_backend.dto.ProductoReporteDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<ProductoReporteDTO> obtenerProductosMasVendidos(LocalDate inicio, LocalDate fin);
    List<ProductoReporteDTO> obtenerProductosMenosVendidos(LocalDate inicio, LocalDate fin);
    MetodoPagoMasUsadoDTO obtenerMetodoPagoMasUsado();
}
