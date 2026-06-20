package com.veterinaria.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.usuarios.assembler.MascotaModelAssembler;
import com.veterinaria.usuarios.dto.MascotaRequestDTO;
import com.veterinaria.usuarios.dto.MascotaResponseDTO;
import com.veterinaria.usuarios.security.JwtService;
import com.veterinaria.usuarios.service.MascotaService;
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
// Pruebas del controller V2 de Mascotas (HATEOAS) con @WebMvcTest + MockMvc.
// Se desactivan los filtros de seguridad (addFilters = false),
// se mockea la capa service con @MockitoBean y se importa el
// assembler REAL (no se mockea) para generar los enlaces HAL.
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@WebMvcTest(MascotaControllerV2.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MascotaModelAssembler.class)
class MascotaControllerV2Test {

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
    @DisplayName("GET /api/v2/mascotas: devuelve 200 con CollectionModel y enlace self")
    void obtenerTodas_devuelve200ConColeccion() throws Exception {
        // Arrange
        when(mascotaService.obtenerTodas()).thenReturn(List.of(
                nuevaMascotaDTO(1L, "Firulais"),
                nuevaMascotaDTO(2L, "Bobby")));

        // Act + Assert
        mockMvc.perform(get("/api/v2/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.mascotaResponseDTOList.length()").value(2))
                .andExpect(jsonPath("$._embedded.mascotaResponseDTOList[0].nombre").value("Firulais"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/v2/mascotas/{id}: devuelve 200 con EntityModel y enlaces self y mascotas")
    void obtenerPorId_cuandoExiste_devuelve200ConEnlaces() throws Exception {
        // Arrange
        when(mascotaService.obtenerPorId(1L)).thenReturn(nuevaMascotaDTO(1L, "Firulais"));

        // Act + Assert
        mockMvc.perform(get("/api/v2/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Firulais"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.mascotas.href").exists());
    }

    @Test
    @DisplayName("POST /api/v2/mascotas: devuelve 201 con la mascota creada, Location y enlaces")
    void crear_mascotaValida_devuelve201() throws Exception {
        // Arrange
        MascotaRequestDTO request = new MascotaRequestDTO(
                "Firulais", "Perro", "Labrador", 3, 1L);
        when(mascotaService.guardar(any(MascotaRequestDTO.class)))
                .thenReturn(nuevaMascotaDTO(5L, "Firulais"));

        // Act + Assert
        mockMvc.perform(post("/api/v2/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("PUT /api/v2/mascotas/{id}: devuelve 200 con la mascota actualizada y enlaces")
    void actualizar_mascotaValida_devuelve200() throws Exception {
        // Arrange
        MascotaRequestDTO request = new MascotaRequestDTO(
                "Firulais", "Perro", "Labrador", 4, 1L);
        when(mascotaService.actualizar(eq(1L), any(MascotaRequestDTO.class)))
                .thenReturn(nuevaMascotaDTO(1L, "Firulais"));

        // Act + Assert
        mockMvc.perform(put("/api/v2/mascotas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("DELETE /api/v2/mascotas/{id}: devuelve 204 sin contenido")
    void eliminar_mascotaExistente_devuelve204() throws Exception {
        // Arrange
        doNothing().when(mascotaService).eliminar(1L);

        // Act + Assert
        mockMvc.perform(delete("/api/v2/mascotas/1"))
                .andExpect(status().isNoContent());
        verify(mascotaService).eliminar(1L);
    }
}
