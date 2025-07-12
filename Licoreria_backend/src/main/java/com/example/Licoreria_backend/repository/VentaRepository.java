package com.example.Licoreria_backend.repository;

import com.example.Licoreria_backend.model.Usuario;
import com.example.Licoreria_backend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByVendedor(Usuario vendedor);
    List<Venta> findByVendedorAndFechaBetween(Usuario vendedor, LocalDateTime desde, LocalDateTime hasta);
}
