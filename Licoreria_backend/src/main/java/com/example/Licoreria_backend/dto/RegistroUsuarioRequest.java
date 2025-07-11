package com.example.Licoreria_backend.dto;

import lombok.Data;

import java.util.Set;

@Data
public class RegistroUsuarioRequest {
    private String username;
    private String password;
    private Set<String> roles;
}
