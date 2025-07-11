package com.example.Licoreria_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentaRequest {
    private String metodoPago;               // "Efectivo", "Yape" o "Tarjeta"
    private Long vendedorId;                 // ID del usuario que vende (opcional si se usa sesión)
    private List<DetalleVentaRequest> detalles; // lista de productos a vender
}
