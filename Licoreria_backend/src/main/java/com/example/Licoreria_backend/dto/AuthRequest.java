package com.example.Licoreria_backend.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
