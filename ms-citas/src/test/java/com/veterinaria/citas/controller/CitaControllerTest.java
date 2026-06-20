package com.veterinaria.citas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ═══════════════════════════════════════════════════
// Pruebas del CONTROLLER de citas con @WebMvcTest.
// Solo se levanta la capa web (MockMvc): el service se
// mockea con @MockitoBean y los filtros de seguridad se
// desactivan con addFilters = false. JwtService se mockea
// porque JwtAuthFilter (un Filter @Component) entra al
// contexto de @WebMvcTest y lo necesita como dependencia.
// ═══════════════════════════════════════════════════

@WebMvcTest(CitaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CitaControllerTest {

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
    @DisplayName("GET /api/citas: retorna 200 con la lista de citas")
    void obtenerTodas_retorna200ConLista() throws Exception {
        // Arrange
        when(citaService.obtenerTodas()).thenReturn(List.of(citaEjemplo()));

        // Act + Assert
        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].mascotaNombre").value("Firulais"))
                .andExpect(jsonPath("$[0].estado").value("PROGRAMADA"));
    }

    @Test
    @DisplayName("GET /api/citas/{id}: retorna 200 cuando la cita existe")
    void obtenerPorId_cuandoExiste_retorna200() throws Exception {
        // Arrange
        when(citaService.obtenerPorId(1L)).thenReturn(citaEjemplo());

        // Act + Assert
        mockMvc.perform(get("/api/citas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.motivo").value("Control general"));
    }

    @Test
    @DisplayName("GET /api/citas/{id}: retorna 404 cuando la cita no existe")
    void obtenerPorId_cuandoNoExiste_retorna404() throws Exception {
        // Arrange: el @ControllerAdvice traduce la excepcion a 404
        when(citaService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Cita no encontrada con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/citas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cita no encontrada con id: 99"));
    }

    @Test
    @DisplayName("POST /api/citas: retorna 201 con header Location y la cita creada")
    void crear_conDatosValidos_retorna201() throws Exception {
        // Arrange
        CitaRequestDTO request = new CitaRequestDTO(1L, "Control general",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 30), null, null);
        when(citaService.guardar(any(CitaRequestDTO.class))).thenReturn(citaEjemplo());

        // Act + Assert
        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/citas/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PROGRAMADA"));
    }

    @Test
    @DisplayName("DELETE /api/citas/{id}: retorna 204 cuando se elimina la cita")
    void eliminar_cuandoExiste_retorna204() throws Exception {
        // Arrange: el service no lanza excepcion (eliminacion exitosa)

        // Act + Assert
        mockMvc.perform(delete("/api/citas/1"))
                .andExpect(status().isNoContent());
        verify(citaService).eliminar(1L);
    }
}
