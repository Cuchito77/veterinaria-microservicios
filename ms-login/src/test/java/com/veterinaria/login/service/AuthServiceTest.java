package com.veterinaria.login.service;

import com.veterinaria.login.dto.LoginRequestDTO;
import com.veterinaria.login.dto.LoginResponseDTO;
import com.veterinaria.login.model.UsuarioCuenta;
import com.veterinaria.login.repository.UsuarioCuentaRepository;
import com.veterinaria.login.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ═══════════════════════════════════════════════════
// Pruebas unitarias de AuthService (login con JWT).
// Se mockean el repositorio, el PasswordEncoder (BCrypt)
// y el JwtService para aislar la logica de autenticacion.
// ═══════════════════════════════════════════════════

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioCuentaRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UsuarioCuenta cuentaActiva() {
        // password ya cifrada (simulada)
        return new UsuarioCuenta(1L, "admin", "$2a$hashFalso", "ADMIN", true);
    }

    @Test
    @DisplayName("autenticar: credenciales correctas devuelve autenticado=true y un token")
    void autenticar_credencialesValidas_devuelveToken() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("admin", "admin123");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(cuentaActiva()));
        when(passwordEncoder.matches("admin123", "$2a$hashFalso")).thenReturn(true);
        when(jwtService.generarToken("admin", "ADMIN")).thenReturn("token.jwt.generado");

        // Act
        LoginResponseDTO resultado = authService.autenticar(dto);

        // Assert
        assertTrue(resultado.isAutenticado());
        assertEquals("token.jwt.generado", resultado.getToken());
        assertEquals("ADMIN", resultado.getRol());
        verify(jwtService).generarToken("admin", "ADMIN");
    }

    @Test
    @DisplayName("autenticar: usuario inexistente devuelve autenticado=false y sin token")
    void autenticar_usuarioNoExiste_devuelveFalse() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("noexiste", "x");
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        // Act
        LoginResponseDTO resultado = authService.autenticar(dto);

        // Assert
        assertFalse(resultado.isAutenticado());
        assertNull(resultado.getToken());
        verify(jwtService, never()).generarToken(anyString(), anyString());
    }

    @Test
    @DisplayName("autenticar: cuenta inactiva devuelve autenticado=false")
    void autenticar_cuentaInactiva_devuelveFalse() {
        // Arrange
        UsuarioCuenta inactiva = new UsuarioCuenta(2L, "inactivo", "$2a$x", "RECEPCION", false);
        LoginRequestDTO dto = new LoginRequestDTO("inactivo", "test123");
        when(usuarioRepository.findByUsername("inactivo")).thenReturn(Optional.of(inactiva));

        // Act
        LoginResponseDTO resultado = authService.autenticar(dto);

        // Assert
        assertFalse(resultado.isAutenticado());
        assertNull(resultado.getToken());
    }

    @Test
    @DisplayName("autenticar: password incorrecta devuelve autenticado=false")
    void autenticar_passwordIncorrecta_devuelveFalse() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("admin", "claveMala");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(cuentaActiva()));
        when(passwordEncoder.matches("claveMala", "$2a$hashFalso")).thenReturn(false);

        // Act
        LoginResponseDTO resultado = authService.autenticar(dto);

        // Assert
        assertFalse(resultado.isAutenticado());
        assertNull(resultado.getToken());
        verify(jwtService, never()).generarToken(anyString(), anyString());
    }
}
