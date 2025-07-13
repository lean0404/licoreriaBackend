package com.example.Licoreria_backend.repository;

import com.example.Licoreria_backend.model.Usuario;
import com.example.Licoreria_backend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRolesContaining(Rol rol);
}
