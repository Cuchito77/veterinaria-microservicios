package com.veterinaria.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.inventario.dto.DescuentoStockDTO;
import com.veterinaria.inventario.dto.ProductoRequestDTO;
import com.veterinaria.inventario.dto.ProductoResponseDTO;
import com.veterinaria.inventario.exception.RecursoNoEncontradoException;
import com.veterinaria.inventario.security.JwtService;
import com.veterinaria.inventario.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ═══════════════════════════════════════════════════
// Pruebas del controller de productos con @WebMvcTest.
// - MockMvc       -> simula peticiones HTTP sin levantar el servidor
// - @MockitoBean  -> reemplaza el service y JwtService por mocks
// - addFilters=false -> desactiva los filtros de seguridad (JWT)
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService productoService;

    // Mock necesario porque JwtAuthFilter (componente del contexto web)
    // depende de JwtService para construirse.
    @MockitoBean
    private JwtService jwtService;

    // Helper para crear un DTO de respuesta de ejemplo
    private ProductoResponseDTO nuevoDTO(Long id, int stock) {
        return new ProductoResponseDTO(id, "Vacuna Antirrabica", "Vacuna",
                new BigDecimal("12000.00"), stock);
    }

    @Test
    @DisplayName("GET /api/productos: devuelve 200 con la lista de productos")
    void obtenerTodos_devuelve200ConLista() throws Exception {
        // Arrange
        when(productoService.obtenerTodos()).thenReturn(List.of(
                nuevoDTO(1L, 50),
                nuevoDTO(2L, 30)));

        // Act + Assert
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Vacuna Antirrabica"))
                .andExpect(jsonPath("$[1].stock").value(30));

        verify(productoService).obtenerTodos();
    }

    @Test
    @DisplayName("GET /api/productos/{id}: devuelve 200 cuando el producto existe")
    void obtenerPorId_cuandoExiste_devuelve200() throws Exception {
        // Arrange
        when(productoService.obtenerPorId(1L)).thenReturn(nuevoDTO(1L, 50));

        // Act + Assert
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Vacuna Antirrabica"))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    @DisplayName("GET /api/productos/{id}: devuelve 404 cuando el producto no existe")
    void obtenerPorId_cuandoNoExiste_devuelve404() throws Exception {
        // Arrange: el service lanza la excepcion que el @RestControllerAdvice mapea a 404
        when(productoService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException(
                        "Producto no encontrado con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado con id: 99"));
    }

    @Test
    @DisplayName("POST /api/productos: con datos validos devuelve 201 y el producto creado")
    void crear_conDatosValidos_devuelve201() throws Exception {
        // Arrange
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Alimento Premium", "Alimento", new BigDecimal("8500.00"), 20);
        ProductoResponseDTO creado = new ProductoResponseDTO(
                10L, "Alimento Premium", "Alimento", new BigDecimal("8500.00"), 20);
        when(productoService.guardar(any(ProductoRequestDTO.class))).thenReturn(creado);

        // Act + Assert: el controller responde 201 con header Location
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/productos/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Alimento Premium"));

        verify(productoService).guardar(any(ProductoRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/productos/{id}/descontar: descuenta stock y devuelve 200")
    void descontarStock_conCantidadValida_devuelve200() throws Exception {
        // Arrange: descontar 20 unidades deja el stock en 30
        DescuentoStockDTO request = new DescuentoStockDTO(20);
        when(productoService.descontarStock(eq(1L), eq(20)))
                .thenReturn(nuevoDTO(1L, 30));

        // Act + Assert
        mockMvc.perform(put("/api/productos/1/descontar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(30));

        verify(productoService).descontarStock(1L, 20);
    }

    @Test
    @DisplayName("GET /api/productos/categoria/{categoria}: devuelve 200 con la lista filtrada")
    void obtenerPorCategoria_devuelve200ConLista() throws Exception {
        // Arrange
        when(productoService.obtenerPorCategoria("Vacuna")).thenReturn(List.of(
                nuevoDTO(1L, 50),
                nuevoDTO(2L, 30)));

        // Act + Assert
        mockMvc.perform(get("/api/productos/categoria/Vacuna"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].categoria").value("Vacuna"));

        verify(productoService).obtenerPorCategoria("Vacuna");
    }

    @Test
    @DisplayName("PUT /api/productos/{id}: actualiza el producto y devuelve 200")
    void actualizar_conDatosValidos_devuelve200() throws Exception {
        // Arrange
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Vacuna Antirrabica", "Vacuna", new BigDecimal("12000.00"), 40);
        when(productoService.actualizar(eq(1L), any(ProductoRequestDTO.class)))
                .thenReturn(nuevoDTO(1L, 40));

        // Act + Assert
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(40));

        verify(productoService).actualizar(eq(1L), any(ProductoRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/productos/{id}: elimina el producto y devuelve 204")
    void eliminar_devuelve204() throws Exception {
        // Act + Assert: el service no devuelve nada, el controller responde 204
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }
}
