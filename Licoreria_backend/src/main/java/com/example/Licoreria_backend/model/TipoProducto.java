package com.example.Licoreria_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;  // Ej: "Pisco", "Whisky", "Ron"
}
