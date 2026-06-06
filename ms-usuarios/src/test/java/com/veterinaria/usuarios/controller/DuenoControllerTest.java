package com.veterinaria.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.usuarios.dto.DuenoRequestDTO;
import com.veterinaria.usuarios.dto.DuenoResponseDTO;
import com.veterinaria.usuarios.exception.RecursoNoEncontradoException;
import com.veterinaria.usuarios.security.JwtService;
import com.veterinaria.usuarios.service.DuenoService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ═══════════════════════════════════════════════════
// Pruebas del controller de Duenos con @WebMvcTest + MockMvc.
// Se desactivan los filtros de seguridad (addFilters = false)
// y se mockea la capa service con @MockitoBean.
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@WebMvcTest(DuenoController.class)
@AutoConfigureMockMvc(addFilters = false)
class DuenoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DuenoService duenoService;

    // Requerido por JwtAuthFilter para que el contexto web cargue
    @MockitoBean
    private JwtService jwtService;

    private DuenoResponseDTO nuevoDuenoDTO(Long id, String rut) {
        return new DuenoResponseDTO(id, "Juan Perez", rut, "juan@mail.com", "+56911111111");
    }

    @Test
    @DisplayName("GET /api/duenos: devuelve 200 con la lista de duenos")
    void obtenerTodos_devuelve200ConLista() throws Exception {
        // Arrange
        when(duenoService.obtenerTodos()).thenReturn(List.of(
                nuevoDuenoDTO(1L, "11111111-1"),
                nuevoDuenoDTO(2L, "22222222-2")));

        // Act + Assert
        mockMvc.perform(get("/api/duenos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez"));
    }

    @Test
    @DisplayName("GET /api/duenos/{id}: devuelve 200 cuando el dueno existe")
    void obtenerPorId_cuandoExiste_devuelve200() throws Exception {
        // Arrange
        when(duenoService.obtenerPorId(1L)).thenReturn(nuevoDuenoDTO(1L, "11111111-1"));

        // Act + Assert
        mockMvc.perform(get("/api/duenos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("11111111-1"));
    }

    @Test
    @DisplayName("GET /api/duenos/{id}: devuelve 404 cuando el dueno no existe (via @RestControllerAdvice)")
    void obtenerPorId_cuandoNoExiste_devuelve404() throws Exception {
        // Arrange
        when(duenoService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Dueno no encontrado con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/duenos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Dueno no encontrado con id: 99"));
    }

    @Test
    @DisplayName("POST /api/duenos: devuelve 201 con el dueno creado")
    void crear_duenoValido_devuelve201() throws Exception {
        // Arrange
        DuenoRequestDTO request = new DuenoRequestDTO(
                "Ana Soto", "33333333-3", "ana@mail.com", "+56922222222");
        DuenoResponseDTO creado = new DuenoResponseDTO(
                5L, "Ana Soto", "33333333-3", "ana@mail.com", "+56922222222");
        when(duenoService.guardar(any(DuenoRequestDTO.class))).thenReturn(creado);

        // Act + Assert
        mockMvc.perform(post("/api/duenos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Ana Soto"));
    }

    @Test
    @DisplayName("DELETE /api/duenos/{id}: devuelve 204 sin contenido")
    void eliminar_duenoExistente_devuelve204() throws Exception {
        // Arrange
        doNothing().when(duenoService).eliminar(1L);

        // Act + Assert
        mockMvc.perform(delete("/api/duenos/1"))
                .andExpect(status().isNoContent());
        verify(duenoService).eliminar(1L);
    }
}
