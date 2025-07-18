package com.example.Licoreria_backend.dto;

import lombok.Data;

@Data
public class MarcaRequest {
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
