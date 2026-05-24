package com.veterinaria.login.controller;

import com.veterinaria.login.dto.LoginRequestDTO;
import com.veterinaria.login.dto.LoginResponseDTO;
import com.veterinaria.login.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ═══════════════════════════════════════════════════
// Controller de autenticacion. Ruta base: /api/auth
// POST /api/auth/login -> valida credenciales.
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO respuesta = authService.autenticar(dto);
        // 200 si autentico, 401 si las credenciales fallaron
        if (respuesta.isAutenticado()) {
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }
}
