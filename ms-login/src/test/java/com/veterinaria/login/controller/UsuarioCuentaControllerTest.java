package com.veterinaria.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.login.dto.UsuarioRequestDTO;
import com.veterinaria.login.dto.UsuarioResponseDTO;
import com.veterinaria.login.exception.RecursoDuplicadoException;
import com.veterinaria.login.exception.RecursoNoEncontradoException;
import com.veterinaria.login.security.JwtService;
import com.veterinaria.login.service.UsuarioCuentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ═══════════════════════════════════════════════════
// Pruebas del UsuarioCuentaController con @WebMvcTest.
// Se mockea el UsuarioCuentaService y se verifica la capa
// HTTP: codigos de estado, JSON de respuesta y el manejo
// de errores via GlobalExceptionHandler (404 / 409).
// Los filtros de seguridad se desactivan (addFilters=false).
// ═══════════════════════════════════════════════════

@WebMvcTest(UsuarioCuentaController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioCuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioCuentaService usuarioService;

    // JwtAuthFilter es un @Component tipo Filter que el contexto
    // de @WebMvcTest escanea: necesita un JwtService para construirse.
    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /api/usuarios: responde 200 con la lista de cuentas")
    void obtenerTodos_responde200ConLista() throws Exception {
        // Arrange
        when(usuarioService.obtenerTodos()).thenReturn(List.of(
                new UsuarioResponseDTO(1L, "admin", "ADMIN", true),
                new UsuarioResponseDTO(2L, "recepcion", "RECEPCION", true)));

        // Act + Assert
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].rol").value("RECEPCION"));
        verify(usuarioService).obtenerTodos();
    }

    @Test
    @DisplayName("GET /api/usuarios/{id}: id inexistente responde 404 con mensaje de error")
    void obtenerPorId_cuandoNoExiste_responde404() throws Exception {
        // Arrange: el service lanza la excepcion -> GlobalExceptionHandler -> 404
        when(usuarioService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Cuenta no encontrada con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cuenta no encontrada con id: 99"));
    }

    @Test
    @DisplayName("POST /api/usuarios: datos validos responde 201 con header Location y la cuenta creada")
    void crear_datosValidos_responde201() throws Exception {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("nuevo", "clave123", "RECEPCION", true);
        when(usuarioService.guardar(any(UsuarioRequestDTO.class)))
                .thenReturn(new UsuarioResponseDTO(7L, "nuevo", "RECEPCION", true));

        // Act + Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.username").value("nuevo"));
        verify(usuarioService).guardar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/usuarios: username duplicado responde 409 Conflict")
    void crear_usernameDuplicado_responde409() throws Exception {
        // Arrange: el service detecta el duplicado -> GlobalExceptionHandler -> 409
        UsuarioRequestDTO dto = new UsuarioRequestDTO("admin", "clave123", "ADMIN", true);
        when(usuarioService.guardar(any(UsuarioRequestDTO.class)))
                .thenThrow(new RecursoDuplicadoException(
                        "Ya existe una cuenta con el username: admin"));

        // Act + Assert
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ya existe una cuenta con el username: admin"));
    }
}
