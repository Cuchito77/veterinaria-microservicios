package com.veterinaria.login.controller;

import com.veterinaria.login.dto.LoginRequestDTO;
import com.veterinaria.login.dto.LoginResponseDTO;
import com.veterinaria.login.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales del usuario y devuelve el resultado de la autenticación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
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
