package com.example.Licoreria_backend.repository;

import com.example.Licoreria_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
