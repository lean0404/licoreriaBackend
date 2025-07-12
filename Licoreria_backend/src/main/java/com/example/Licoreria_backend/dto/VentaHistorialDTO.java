package com.example.Licoreria_backend.dto;

import java.time.LocalDateTime;

public class VentaHistorialDTO {
    private Long id;
    private LocalDateTime fecha;
    private String metodoPago;
    private String tipoComprobante;
    private String documentoCliente;
    private int totalProductos;
    private double totalVenta;

    public VentaHistorialDTO() {}

    public VentaHistorialDTO(Long id, LocalDateTime fecha, String metodoPago, String tipoComprobante, String documentoCliente, int totalProductos, double totalVenta) {
        this.id = id;
        this.fecha = fecha;
        this.metodoPago = metodoPago;
        this.tipoComprobante = tipoComprobante;
        this.documentoCliente = documentoCliente;
        this.totalProductos = totalProductos;
        this.totalVenta = totalVenta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public void setDocumentoCliente(String documentoCliente) {
        this.documentoCliente = documentoCliente;
    }

    public int getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(int totalProductos) {
        this.totalProductos = totalProductos;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
}
