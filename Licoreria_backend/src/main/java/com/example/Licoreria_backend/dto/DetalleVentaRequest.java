package com.example.Licoreria_backend.dto;

import lombok.Data;

@Data
public class DetalleVentaRequest {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
}
