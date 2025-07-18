package com.example.Licoreria_backend.config;

import com.example.Licoreria_backend.model.Rol;
import com.example.Licoreria_backend.model.Usuario;
import com.example.Licoreria_backend.repository.RolRepository;
import com.example.Licoreria_backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        return args -> {

            Rol adminRol = rolRepository.findByNombre("ADMIN")
                    .orElseGet(() -> rolRepository.save(new Rol(null, "ADMIN")));

            Rol vendedorRol = rolRepository.findByNombre("VENDEDOR")
                    .orElseGet(() -> rolRepository.save(new Rol(null, "VENDEDOR")));

            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setActivo(true);
                admin.setRoles(Set.of(adminRol));
                usuarioRepository.save(admin);
                System.out.println("Usuario admin creado: usuario=admin / contraseña=admin123");
            }

            if (usuarioRepository.findByUsername("vendedor").isEmpty()) {
                Usuario vendedor = new Usuario();
                vendedor.setUsername("vendedor");
                vendedor.setPassword(encoder.encode("vendedor123"));
                vendedor.setActivo(true);
                vendedor.setRoles(Set.of(vendedorRol));
                usuarioRepository.save(vendedor);
                System.out.println("Usuario vendedor creado: usuario=vendedor / contraseña=vendedor123");
            }
        };
    }
}
