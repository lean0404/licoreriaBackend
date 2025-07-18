package com.example.Licoreria_backend.repository;

import com.example.Licoreria_backend.dto.MetodoPagoMasUsadoDTO;
import com.example.Licoreria_backend.dto.ProductoReporteDTO;
import com.example.Licoreria_backend.model.Usuario;
import com.example.Licoreria_backend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByVendedor(Usuario vendedor);
    List<Venta> findByVendedorAndFechaBetween(Usuario vendedor, LocalDateTime desde, LocalDateTime hasta);

    // Lista de productos más vendidos (orden DESC)
    @Query("""
        SELECT new com.example.Licoreria_backend.dto.ProductoReporteDTO(d.producto.nombre, SUM(d.cantidad))
        FROM DetalleVenta d
        WHERE d.venta.fecha BETWEEN :inicio AND :fin
        GROUP BY d.producto.nombre
        ORDER BY SUM(d.cantidad) DESC
    """)
    List<ProductoReporteDTO> findProductosMasVendidos(@Param("inicio") LocalDateTime inicio,
                                                      @Param("fin") LocalDateTime fin);

    // Lista de productos menos vendidos (orden ASC)
    @Query("""
    SELECT new com.example.Licoreria_backend.dto.ProductoReporteDTO(d.producto.nombre, SUM(d.cantidad))
    FROM DetalleVenta d
    WHERE d.venta.fecha BETWEEN :inicio AND :fin
    GROUP BY d.producto.nombre
    ORDER BY SUM(d.cantidad) ASC
""")
    List<ProductoReporteDTO> findProductosMenosVendidos(@Param("inicio") LocalDateTime inicio,
                                                        @Param("fin") LocalDateTime fin);


    // Método de pago más usado
    @Query("""
    SELECT new com.example.Licoreria_backend.dto.MetodoPagoMasUsadoDTO(v.metodoPago, COUNT(v))
    FROM Venta v
    WHERE v.metodoPago IS NOT NULL
    GROUP BY v.metodoPago
    ORDER BY COUNT(v) DESC
""")
    List<MetodoPagoMasUsadoDTO> obtenerMetodoPagoMasUsadoConCantidad();


}
