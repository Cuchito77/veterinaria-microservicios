package com.veterinaria.citas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.citas.assembler.CitaModelAssembler;
import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.exception.RecursoNoEncontradoException;
import com.veterinaria.citas.security.JwtService;
import com.veterinaria.citas.service.CitaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ═══════════════════════════════════════════════════
// Pruebas del CONTROLLER V2 (HATEOAS) con @WebMvcTest.
// Solo se levanta la capa web (MockMvc): el service se
// mockea con @MockitoBean y los filtros de seguridad se
// desactivan con addFilters = false. JwtService se mockea
// porque JwtAuthFilter (un Filter @Component) entra al
// contexto de @WebMvcTest y lo necesita como dependencia.
// El CitaModelAssembler se importa real con @Import porque
// el controller lo usa para construir los enlaces hipermedia.
// ═══════════════════════════════════════════════════

@WebMvcTest(CitaControllerV2.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CitaModelAssembler.class)
class CitaControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CitaService citaService;

    // Requerido por JwtAuthFilter para que el contexto web cargue
    @MockitoBean
    private JwtService jwtService;

    private CitaResponseDTO citaEjemplo() {
        return new CitaResponseDTO(1L, 1L, "Firulais", "Control general",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 30),
                null, null, "PROGRAMADA");
    }

    @Test
    @DisplayName("GET /api/v2/citas: retorna 200 con CollectionModel y enlace self")
    void obtenerTodas_retorna200ConCollectionModel() throws Exception {
        // Arrange
        when(citaService.obtenerTodas()).thenReturn(List.of(citaEjemplo()));

        // Act + Assert
        mockMvc.perform(get("/api/v2/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/v2/citas")));
    }

    @Test
    @DisplayName("GET /api/v2/citas/{id}: retorna 200 con EntityModel y enlaces self y citas")
    void obtenerPorId_cuandoExiste_retorna200ConEnlaces() throws Exception {
        // Arrange
        when(citaService.obtenerPorId(1L)).thenReturn(citaEjemplo());

        // Act + Assert
        mockMvc.perform(get("/api/v2/citas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mascotaNombre").value("Firulais"))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v2/citas/1")))
                .andExpect(jsonPath("$._links.citas.href", endsWith("/api/v2/citas")));
    }

    @Test
    @DisplayName("GET /api/v2/citas/{id}: retorna 404 cuando la cita no existe")
    void obtenerPorId_cuandoNoExiste_retorna404() throws Exception {
        // Arrange: el @ControllerAdvice traduce la excepcion a 404
        when(citaService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Cita no encontrada con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/v2/citas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cita no encontrada con id: 99"));
    }

    @Test
    @DisplayName("POST /api/v2/citas: retorna 201 con header Location y enlaces HATEOAS")
    void crear_conDatosValidos_retorna201ConEnlaces() throws Exception {
        // Arrange
        CitaRequestDTO request = new CitaRequestDTO(1L, "Control general",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 30), null, null);
        when(citaService.guardar(any(CitaRequestDTO.class))).thenReturn(citaEjemplo());

        // Act + Assert
        mockMvc.perform(post("/api/v2/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v2/citas/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v2/citas/1")));
    }

    @Test
    @DisplayName("PUT /api/v2/citas/{id}: retorna 200 con la cita actualizada y enlaces")
    void actualizar_conDatosValidos_retorna200() throws Exception {
        // Arrange
        CitaRequestDTO request = new CitaRequestDTO(1L, "Control general",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 30), null, null);
        when(citaService.actualizar(eq(1L), any(CitaRequestDTO.class))).thenReturn(citaEjemplo());

        // Act + Assert
        mockMvc.perform(put("/api/v2/citas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v2/citas/1")));
    }

    @Test
    @DisplayName("DELETE /api/v2/citas/{id}: retorna 204 cuando se elimina la cita")
    void eliminar_cuandoExiste_retorna204() throws Exception {
        // Arrange: el service no lanza excepcion (eliminacion exitosa)

        // Act + Assert
        mockMvc.perform(delete("/api/v2/citas/1"))
                .andExpect(status().isNoContent());
        verify(citaService).eliminar(1L);
    }
}
