package com.example.Licoreria_backend.controller;

import com.example.Licoreria_backend.security.UserDetailsImpl;
import com.example.Licoreria_backend.dto.AuthRequest;
import com.example.Licoreria_backend.dto.RegistroUsuarioRequest;
import com.example.Licoreria_backend.model.Rol;
import com.example.Licoreria_backend.model.Usuario;
import com.example.Licoreria_backend.repository.RolRepository;
import com.example.Licoreria_backend.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, UsuarioRepository usuarioRepository,
                          RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByUsername(request.getUsername());

        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        Usuario usuario = optionalUsuario.get();

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // ✅ Ahora se guarda en el SecurityContext + Session para que persista
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        System.out.println("LOGIN -> Usuario autenticado: " + usuario.getUsername() + " con roles: " + userDetails.getAuthorities());

        String rol = usuario.getRoles().stream().findFirst().map(Rol::getNombre).orElse("SIN_ROL");

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("username", usuario.getUsername());
        respuesta.put("rol", rol);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay usuario autenticado.");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return ResponseEntity.ok(userDetails.getUsuario());
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody RegistroUsuarioRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(request.getUsername());
        nuevo.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Rol> roles = new HashSet<>();
        for (String rolStr : request.getRoles()) {
            rolRepository.findByNombre(rolStr).ifPresent(roles::add);
        }
        nuevo.setRoles(roles);

        usuarioRepository.save(nuevo);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
}
