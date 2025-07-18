package com.example.Licoreria_backend.dto;

public class MetodoPagoMasUsadoDTO {
    private String metodoPago;
    private Long cantidad;

    public MetodoPagoMasUsadoDTO(String metodoPago, Long cantidad) {
        this.metodoPago = metodoPago;
        this.cantidad = cantidad;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public Long getCantidad() {
        return cantidad;
    }
}
