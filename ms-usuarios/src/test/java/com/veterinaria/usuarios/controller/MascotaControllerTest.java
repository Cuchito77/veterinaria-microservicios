package com.veterinaria.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.usuarios.dto.MascotaRequestDTO;
import com.veterinaria.usuarios.dto.MascotaResponseDTO;
import com.veterinaria.usuarios.exception.RecursoNoEncontradoException;
import com.veterinaria.usuarios.security.JwtService;
import com.veterinaria.usuarios.service.MascotaService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ═══════════════════════════════════════════════════
// Pruebas del controller de Mascotas con @WebMvcTest + MockMvc.
// Se desactivan los filtros de seguridad (addFilters = false)
// y se mockea la capa service con @MockitoBean.
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@WebMvcTest(MascotaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MascotaService mascotaService;

    // Requerido por JwtAuthFilter para que el contexto web cargue
    @MockitoBean
    private JwtService jwtService;

    private MascotaResponseDTO nuevaMascotaDTO(Long id, String nombre) {
        return new MascotaResponseDTO(id, nombre, "Perro", "Labrador", 3, 1L, "Juan Perez");
    }

    @Test
    @DisplayName("GET /api/mascotas: devuelve 200 con la lista de mascotas")
    void obtenerTodas_devuelve200ConLista() throws Exception {
        // Arrange
        when(mascotaService.obtenerTodas()).thenReturn(List.of(
                nuevaMascotaDTO(1L, "Firulais"),
                nuevaMascotaDTO(2L, "Bobby")));

        // Act + Assert
        mockMvc.perform(get("/api/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Firulais"))
                .andExpect(jsonPath("$[1].nombre").value("Bobby"));
    }

    @Test
    @DisplayName("GET /api/mascotas/{id}: devuelve 404 cuando la mascota no existe (via @RestControllerAdvice)")
    void obtenerPorId_cuandoNoExiste_devuelve404() throws Exception {
        // Arrange
        when(mascotaService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Mascota no encontrada con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/mascotas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mascota no encontrada con id: 99"));
    }

    @Test
    @DisplayName("POST /api/mascotas: devuelve 201 con la mascota creada")
    void crear_mascotaValida_devuelve201() throws Exception {
        // Arrange
        MascotaRequestDTO request = new MascotaRequestDTO(
                "Firulais", "Perro", "Labrador", 3, 1L);
        when(mascotaService.guardar(any(MascotaRequestDTO.class)))
                .thenReturn(nuevaMascotaDTO(5L, "Firulais"));

        // Act + Assert
        mockMvc.perform(post("/api/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.duenoId").value(1));
    }

    @Test
    @DisplayName("DELETE /api/mascotas/{id}: devuelve 204 sin contenido")
    void eliminar_mascotaExistente_devuelve204() throws Exception {
        // Arrange: el service no lanza excepcion (mock por defecto)

        // Act + Assert
        mockMvc.perform(delete("/api/mascotas/1"))
                .andExpect(status().isNoContent());
    }
}
