package com.example.Licoreria_backend.dto;

import lombok.Data;

@Data
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Long tipoProductoId;
    private String tipoProductoNombre;
    private Long marcaId;
    private String marcaNombre;
}
