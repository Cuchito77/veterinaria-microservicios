package com.veterinaria.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.usuarios.assembler.DuenoModelAssembler;
import com.veterinaria.usuarios.dto.DuenoRequestDTO;
import com.veterinaria.usuarios.dto.DuenoResponseDTO;
import com.veterinaria.usuarios.security.JwtService;
import com.veterinaria.usuarios.service.DuenoService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ═══════════════════════════════════════════════════
// Pruebas del controller V2 de Duenos (HATEOAS) con @WebMvcTest + MockMvc.
// Se desactivan los filtros de seguridad (addFilters = false),
// se mockea la capa service con @MockitoBean y se importa el
// assembler REAL (no se mockea) para generar los enlaces HAL.
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@WebMvcTest(DuenoControllerV2.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DuenoModelAssembler.class)
class DuenoControllerV2Test {

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
    @DisplayName("GET /api/v2/duenos: devuelve 200 con CollectionModel y enlace self")
    void obtenerTodos_devuelve200ConColeccion() throws Exception {
        // Arrange
        when(duenoService.obtenerTodos()).thenReturn(List.of(
                nuevoDuenoDTO(1L, "11111111-1"),
                nuevoDuenoDTO(2L, "22222222-2")));

        // Act + Assert
        mockMvc.perform(get("/api/v2/duenos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.duenoResponseDTOList.length()").value(2))
                .andExpect(jsonPath("$._embedded.duenoResponseDTOList[0].id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v2/duenos/{id}: devuelve 200 con EntityModel y enlaces self y duenos")
    void obtenerPorId_cuandoExiste_devuelve200ConEnlaces() throws Exception {
        // Arrange
        when(duenoService.obtenerPorId(1L)).thenReturn(nuevoDuenoDTO(1L, "11111111-1"));

        // Act + Assert
        mockMvc.perform(get("/api/v2/duenos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("11111111-1"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.duenos.href").exists());
    }

    @Test
    @DisplayName("POST /api/v2/duenos: devuelve 201 con el dueno creado, Location y enlaces")
    void crear_duenoValido_devuelve201() throws Exception {
        // Arrange
        DuenoRequestDTO request = new DuenoRequestDTO(
                "Ana Soto", "33333333-3", "ana@mail.com", "+56922222222");
        DuenoResponseDTO creado = new DuenoResponseDTO(
                5L, "Ana Soto", "33333333-3", "ana@mail.com", "+56922222222");
        when(duenoService.guardar(any(DuenoRequestDTO.class))).thenReturn(creado);

        // Act + Assert
        mockMvc.perform(post("/api/v2/duenos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("PUT /api/v2/duenos/{id}: devuelve 200 con el dueno actualizado y enlaces")
    void actualizar_duenoValido_devuelve200() throws Exception {
        // Arrange
        DuenoRequestDTO request = new DuenoRequestDTO(
                "Juan Perez", "11111111-1", "juan@mail.com", "+56911111111");
        when(duenoService.actualizar(eq(1L), any(DuenoRequestDTO.class)))
                .thenReturn(nuevoDuenoDTO(1L, "11111111-1"));

        // Act + Assert
        mockMvc.perform(put("/api/v2/duenos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("DELETE /api/v2/duenos/{id}: devuelve 204 sin contenido")
    void eliminar_duenoExistente_devuelve204() throws Exception {
        // Arrange
        doNothing().when(duenoService).eliminar(1L);

        // Act + Assert
        mockMvc.perform(delete("/api/v2/duenos/1"))
                .andExpect(status().isNoContent());
        verify(duenoService).eliminar(1L);
    }
}
