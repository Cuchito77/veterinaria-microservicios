package com.veterinaria.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.login.assembler.UsuarioModelAssembler;
import com.veterinaria.login.dto.UsuarioRequestDTO;
import com.veterinaria.login.dto.UsuarioResponseDTO;
import com.veterinaria.login.exception.RecursoNoEncontradoException;
import com.veterinaria.login.security.JwtService;
import com.veterinaria.login.service.UsuarioCuentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ═══════════════════════════════════════════════════
// Pruebas del UsuarioCuentaControllerV2 (HATEOAS) con @WebMvcTest.
// Se mockea el UsuarioCuentaService y se importa el assembler real
// para verificar los enlaces hipermedia (_links self / usuarios)
// y el wrapper _embedded en las respuestas HAL_JSON.
// Los filtros de seguridad se desactivan (addFilters=false).
// ═══════════════════════════════════════════════════

@WebMvcTest(UsuarioCuentaControllerV2.class)
@Import(UsuarioModelAssembler.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioCuentaControllerV2Test {

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
    @DisplayName("GET /api/v2/usuarios: responde 200 con CollectionModel y enlace self")
    void obtenerTodos_responde200ConColeccionYLinks() throws Exception {
        // Arrange
        when(usuarioService.obtenerTodos()).thenReturn(List.of(
                new UsuarioResponseDTO(1L, "admin", "ADMIN", true),
                new UsuarioResponseDTO(2L, "recepcion", "RECEPCION", true)));

        // Act + Assert
        mockMvc.perform(get("/api/v2/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioResponseDTOList.length()").value(2))
                .andExpect(jsonPath("$._embedded.usuarioResponseDTOList[0].username").value("admin"))
                .andExpect(jsonPath("$._embedded.usuarioResponseDTOList[0]._links.self.href")
                        .value(org.hamcrest.Matchers.endsWith("/api/v2/usuarios/1")))
                .andExpect(jsonPath("$._links.self.href")
                        .value(org.hamcrest.Matchers.endsWith("/api/v2/usuarios")));
        verify(usuarioService).obtenerTodos();
    }

    @Test
    @DisplayName("GET /api/v2/usuarios/{id}: responde 200 con EntityModel y enlaces self y usuarios")
    void obtenerPorId_responde200ConEntityYLinks() throws Exception {
        // Arrange
        when(usuarioService.obtenerPorId(1L))
                .thenReturn(new UsuarioResponseDTO(1L, "admin", "ADMIN", true));

        // Act + Assert
        mockMvc.perform(get("/api/v2/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$._links.self.href")
                        .value(org.hamcrest.Matchers.endsWith("/api/v2/usuarios/1")))
                .andExpect(jsonPath("$._links.usuarios.href")
                        .value(org.hamcrest.Matchers.endsWith("/api/v2/usuarios")));
        verify(usuarioService).obtenerPorId(1L);
    }

    @Test
    @DisplayName("GET /api/v2/usuarios/{id}: id inexistente responde 404 con mensaje de error")
    void obtenerPorId_cuandoNoExiste_responde404() throws Exception {
        // Arrange: el service lanza la excepcion -> GlobalExceptionHandler -> 404
        when(usuarioService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Cuenta no encontrada con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/v2/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cuenta no encontrada con id: 99"));
    }

    @Test
    @DisplayName("POST /api/v2/usuarios: datos validos responde 201 con header Location y enlaces")
    void crear_datosValidos_responde201ConLinks() throws Exception {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("nuevo", "clave123", "RECEPCION", true);
        when(usuarioService.guardar(any(UsuarioRequestDTO.class)))
                .thenReturn(new UsuarioResponseDTO(7L, "nuevo", "RECEPCION", true));

        // Act + Assert
        mockMvc.perform(post("/api/v2/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.username").value("nuevo"))
                .andExpect(jsonPath("$._links.self.href")
                        .value(org.hamcrest.Matchers.endsWith("/api/v2/usuarios/7")));
        verify(usuarioService).guardar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v2/usuarios/{id}: actualiza y responde 200 con EntityModel y enlaces")
    void actualizar_datosValidos_responde200ConLinks() throws Exception {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO("editado", "clave123", "ADMIN", true);
        when(usuarioService.actualizar(eq(1L), any(UsuarioRequestDTO.class)))
                .thenReturn(new UsuarioResponseDTO(1L, "editado", "ADMIN", true));

        // Act + Assert
        mockMvc.perform(put("/api/v2/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("editado"))
                .andExpect(jsonPath("$._links.self.href")
                        .value(org.hamcrest.Matchers.endsWith("/api/v2/usuarios/1")));
        verify(usuarioService).actualizar(eq(1L), any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v2/usuarios/{id}: elimina y responde 204 sin contenido")
    void eliminar_responde204() throws Exception {
        // Arrange
        // (el service no devuelve nada; solo se verifica la invocacion)

        // Act + Assert
        mockMvc.perform(delete("/api/v2/usuarios/1"))
                .andExpect(status().isNoContent());
        verify(usuarioService).eliminar(1L);
    }
}
