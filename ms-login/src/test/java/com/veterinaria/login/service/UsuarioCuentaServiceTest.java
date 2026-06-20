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

import java.util.List;
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

    @Test
    @DisplayName("obtenerTodos: mapea las entidades a DTO sin exponer la password")
    void obtenerTodos_devuelveListaDeDTOs() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of(
                new UsuarioCuenta(1L, "admin", "$2a$x", "ADMIN", true),
                new UsuarioCuenta(2L, "recepcion", "$2a$y", "RECEPCION", false)));

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("admin", resultado.get(0).getUsername());
        assertEquals("RECEPCION", resultado.get(1).getRol());
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("obtenerPorId: devuelve el DTO cuando la cuenta existe")
    void obtenerPorId_cuentaExistente_devuelveDTO() {
        // Arrange
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(new UsuarioCuenta(1L, "admin", "$2a$x", "ADMIN", true)));

        // Act
        UsuarioResponseDTO resultado = usuarioService.obtenerPorId(1L);

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("admin", resultado.getUsername());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId: lanza RecursoNoEncontradoException si la cuenta no existe")
    void obtenerPorId_cuentaInexistente_lanzaExcepcion() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("actualizar: cifra la password y actualiza cuando el username no esta duplicado")
    void actualizar_datosValidos_cifraYActualiza() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("editado", "claveNueva", "ADMIN", true);
        UsuarioCuenta existente = new UsuarioCuenta(1L, "admin", "$2a$viejo", "ADMIN", true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByUsername("editado")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("claveNueva")).thenReturn("$2a$nuevoHash");
        when(usuarioRepository.save(any(UsuarioCuenta.class)))
                .thenReturn(new UsuarioCuenta(1L, "editado", "$2a$nuevoHash", "ADMIN", true));

        // Act
        UsuarioResponseDTO resultado = usuarioService.actualizar(1L, dto);

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("editado", resultado.getUsername());
        // se debe cifrar la nueva password (nunca guardarla en texto plano)
        verify(passwordEncoder).encode("claveNueva");
        verify(usuarioRepository).save(any(UsuarioCuenta.class));
    }

    @Test
    @DisplayName("actualizar: mismo username de la misma cuenta no se considera duplicado")
    void actualizar_mismoUsernameMismaCuenta_actualiza() {
        // Arrange: findByUsername devuelve la MISMA cuenta (mismo id) -> no es duplicado
        UsuarioRequestDTO dto = new UsuarioRequestDTO("admin", "claveNueva", "ADMIN", false);
        UsuarioCuenta existente = new UsuarioCuenta(1L, "admin", "$2a$viejo", "ADMIN", true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("claveNueva")).thenReturn("$2a$nuevoHash");
        when(usuarioRepository.save(any(UsuarioCuenta.class)))
                .thenReturn(new UsuarioCuenta(1L, "admin", "$2a$nuevoHash", "ADMIN", false));

        // Act
        UsuarioResponseDTO resultado = usuarioService.actualizar(1L, dto);

        // Assert
        assertEquals("admin", resultado.getUsername());
        verify(usuarioRepository).save(any(UsuarioCuenta.class));
    }

    @Test
    @DisplayName("actualizar: lanza RecursoDuplicadoException si el username pertenece a OTRA cuenta")
    void actualizar_usernameDeOtraCuenta_lanzaExcepcion() {
        // Arrange: findByUsername devuelve una cuenta con id distinto -> duplicado
        UsuarioRequestDTO dto = new UsuarioRequestDTO("ocupado", "clave", "ADMIN", true);
        UsuarioCuenta existente = new UsuarioCuenta(1L, "admin", "$2a$x", "ADMIN", true);
        UsuarioCuenta otra = new UsuarioCuenta(2L, "ocupado", "$2a$y", "RECEPCION", true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByUsername("ocupado")).thenReturn(Optional.of(otra));

        // Act + Assert
        assertThrows(RecursoDuplicadoException.class,
                () -> usuarioService.actualizar(1L, dto));
        verify(usuarioRepository, never()).save(any(UsuarioCuenta.class));
    }

    @Test
    @DisplayName("eliminar: elimina la cuenta cuando existe")
    void eliminar_cuentaExistente_eliminaPorId() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        usuarioService.eliminar(1L);

        // Assert
        verify(usuarioRepository).deleteById(1L);
    }
}
