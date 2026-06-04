package com.veterinaria.login.service;

import com.veterinaria.login.dto.UsuarioRequestDTO;
import com.veterinaria.login.dto.UsuarioResponseDTO;
import com.veterinaria.login.exception.RecursoDuplicadoException;
import com.veterinaria.login.exception.RecursoNoEncontradoException;
import com.veterinaria.login.model.UsuarioCuenta;
import com.veterinaria.login.repository.UsuarioCuentaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ═══════════════════════════════════════════════════
// Pruebas unitarias de UsuarioCuentaService.
// Verifica el cifrado de password (BCrypt) y la regla de
// username unico. El PasswordEncoder se mockea.
// ═══════════════════════════════════════════════════

@ExtendWith(MockitoExtension.class)
class UsuarioCuentaServiceTest {

    @Mock
    private UsuarioCuentaRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioCuentaService usuarioService;

    @Test
    @DisplayName("guardar: cifra la password y crea la cuenta cuando el username es nuevo")
    void guardar_usernameNuevo_cifraYCrea() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("nuevo", "clave123", "RECEPCION", true);
        when(usuarioRepository.findByUsername("nuevo")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("clave123")).thenReturn("$2a$hashCifrado");
        when(usuarioRepository.save(any(UsuarioCuenta.class)))
                .thenReturn(new UsuarioCuenta(7L, "nuevo", "$2a$hashCifrado", "RECEPCION", true));

        // Act
        UsuarioResponseDTO resultado = usuarioService.guardar(dto);

        // Assert
        assertEquals(7L, resultado.getId());
        // se debe cifrar la password (nunca guardarla en texto plano)
        verify(passwordEncoder).encode("clave123");
        verify(usuarioRepository).save(any(UsuarioCuenta.class));
    }

    @Test
    @DisplayName("guardar: lanza RecursoDuplicadoException si el username ya existe")
    void guardar_usernameDuplicado_lanzaExcepcion() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("admin", "clave123", "ADMIN", true);
        when(usuarioRepository.findByUsername("admin"))
                .thenReturn(Optional.of(new UsuarioCuenta(1L, "admin", "$2a$x", "ADMIN", true)));

        // Act + Assert
        assertThrows(RecursoDuplicadoException.class,
                () -> usuarioService.guardar(dto));
        verify(usuarioRepository, never()).save(any(UsuarioCuenta.class));
    }

    @Test
    @DisplayName("actualizar: lanza RecursoNoEncontradoException si la cuenta no existe")
    void actualizar_cuentaInexistente_lanzaExcepcion() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("x", "y", "ADMIN", true);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.actualizar(99L, dto));
    }

    @Test
    @DisplayName("eliminar: lanza RecursoNoEncontradoException si la cuenta no existe")
    void eliminar_cuentaInexistente_lanzaExcepcion() {
        // Arrange
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.eliminar(99L));
        verify(usuarioRepository, never()).deleteById(any());
    }
}
