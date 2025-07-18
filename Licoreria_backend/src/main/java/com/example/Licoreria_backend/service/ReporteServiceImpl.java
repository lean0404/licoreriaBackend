package com.example.Licoreria_backend.service.impl;

import com.example.Licoreria_backend.dto.MetodoPagoMasUsadoDTO;
import com.example.Licoreria_backend.dto.ProductoReporteDTO;
import com.example.Licoreria_backend.repository.VentaRepository;
import com.example.Licoreria_backend.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private VentaRepository ventaRepository;

    @Override
    public List<ProductoReporteDTO> obtenerProductosMasVendidos(LocalDate inicio, LocalDate fin) {
        LocalDateTime desde = inicio.atStartOfDay();
        LocalDateTime hasta = fin.atTime(23, 59, 59);
        return ventaRepository.findProductosMasVendidos(desde, hasta);
    }

    @Override
    public List<ProductoReporteDTO> obtenerProductosMenosVendidos(LocalDate inicio, LocalDate fin) {
        LocalDateTime desde = inicio.atStartOfDay();
        LocalDateTime hasta = fin.atTime(23, 59, 59);
        return ventaRepository.findProductosMenosVendidos(desde, hasta);
    }


    @Override
    public MetodoPagoMasUsadoDTO obtenerMetodoPagoMasUsado() {
        List<MetodoPagoMasUsadoDTO> lista = ventaRepository.obtenerMetodoPagoMasUsadoConCantidad();
        if (lista.isEmpty()) {
            return new MetodoPagoMasUsadoDTO("No disponible", 0L);
        }
        return lista.get(0);
    }


}
