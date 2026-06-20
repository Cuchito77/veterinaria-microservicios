package com.veterinaria.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.login.dto.LoginRequestDTO;
import com.veterinaria.login.dto.LoginResponseDTO;
import com.veterinaria.login.security.JwtService;
import com.veterinaria.login.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ═══════════════════════════════════════════════════
// Pruebas del AuthController con @WebMvcTest + MockMvc.
// Se mockea el AuthService (la logica ya se probo en
// AuthServiceTest); aqui se valida la capa HTTP:
// codigos de estado y cuerpo JSON de la respuesta.
// Los filtros de seguridad se desactivan (addFilters=false).
// ═══════════════════════════════════════════════════

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // JwtAuthFilter es un @Component tipo Filter que el contexto
    // de @WebMvcTest escanea: necesita un JwtService para construirse.
    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/auth/login: credenciales correctas responde 200 con token JWT")
    void login_credencialesValidas_responde200ConToken() throws Exception {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("admin", "admin123");
        when(authService.autenticar(any(LoginRequestDTO.class)))
                .thenReturn(new LoginResponseDTO(true,
                        "Autenticacion exitosa", "admin", "ADMIN", "token.jwt.generado"));

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(true))
                .andExpect(jsonPath("$.token").value("token.jwt.generado"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
        verify(authService).autenticar(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/login: credenciales invalidas responde 401 sin token")
    void login_credencialesInvalidas_responde401() throws Exception {
        // Arrange: el service devuelve autenticado=false -> el controller responde 401
        LoginRequestDTO dto = new LoginRequestDTO("admin", "claveMala");
        when(authService.autenticar(any(LoginRequestDTO.class)))
                .thenReturn(new LoginResponseDTO(false,
                        "Usuario o contrasena incorrectos", "admin", null, null));

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.autenticado").value(false))
                .andExpect(jsonPath("$.token").isEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/login: cuenta inactiva responde 401 con el mensaje correspondiente")
    void login_cuentaInactiva_responde401() throws Exception {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("inactivo", "test123");
        when(authService.autenticar(any(LoginRequestDTO.class)))
                .thenReturn(new LoginResponseDTO(false,
                        "La cuenta esta inactiva", "inactivo", null, null));

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("La cuenta esta inactiva"));
    }

    @Test
    @DisplayName("POST /api/auth/login: body sin username responde 400 (validacion @NotBlank)")
    void login_usernameVacio_responde400() throws Exception {
        // Arrange: username en blanco -> MethodArgumentNotValidException -> 400
        LoginRequestDTO dto = new LoginRequestDTO("", "admin123");

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
        // el service nunca debe ser invocado si la validacion falla
        verify(authService, never()).autenticar(any(LoginRequestDTO.class));
    }
}
