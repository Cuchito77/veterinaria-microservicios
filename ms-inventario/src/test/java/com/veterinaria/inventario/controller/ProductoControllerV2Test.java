package com.veterinaria.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.inventario.assembler.ProductoModelAssembler;
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
import org.springframework.context.annotation.Import;
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
// Pruebas del controller V2 (HATEOAS) con @WebMvcTest.
// - MockMvc       -> simula peticiones HTTP sin levantar el servidor
// - @MockitoBean  -> reemplaza el service y JwtService por mocks
// - @Import       -> incluye el ProductoModelAssembler real, que el
//                    controller usa por inyeccion de constructor para
//                    construir los enlaces hipermedia (_links)
// - addFilters=false -> desactiva los filtros de seguridad (JWT)
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@WebMvcTest(ProductoControllerV2.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProductoModelAssembler.class)
class ProductoControllerV2Test {

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
    @DisplayName("GET /api/v2/productos: devuelve 200 con CollectionModel y enlace self")
    void obtenerTodos_devuelveCollectionModelConLinks() throws Exception {
        // Arrange
        when(productoService.obtenerTodos()).thenReturn(List.of(
                nuevoDTO(1L, 50),
                nuevoDTO(2L, 30)));

        // Act + Assert: la coleccion expone _embedded con los productos,
        // cada uno con su enlace self, y un enlace self de la coleccion
        mockMvc.perform(get("/api/v2/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoResponseDTOList.length()").value(2))
                .andExpect(jsonPath("$._embedded.productoResponseDTOList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.productoResponseDTOList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(productoService).obtenerTodos();
    }

    @Test
    @DisplayName("GET /api/v2/productos/{id}: devuelve 200 con EntityModel y enlaces self y productos")
    void obtenerPorId_cuandoExiste_devuelveEntityModelConLinks() throws Exception {
        // Arrange
        when(productoService.obtenerPorId(1L)).thenReturn(nuevoDTO(1L, 50));

        // Act + Assert: el recurso incluye sus enlaces hipermedia
        mockMvc.perform(get("/api/v2/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Vacuna Antirrabica"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.productos.href").exists());
    }

    @Test
    @DisplayName("GET /api/v2/productos/{id}: devuelve 404 cuando el producto no existe")
    void obtenerPorId_cuandoNoExiste_devuelve404() throws Exception {
        // Arrange: el service lanza la excepcion que el advice mapea a 404
        when(productoService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException(
                        "Producto no encontrado con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/v2/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado con id: 99"));
    }

    @Test
    @DisplayName("POST /api/v2/productos: con datos validos devuelve 201 con Location y enlaces")
    void crear_conDatosValidos_devuelve201ConLinks() throws Exception {
        // Arrange
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Alimento Premium", "Alimento", new BigDecimal("8500.00"), 20);
        ProductoResponseDTO creado = new ProductoResponseDTO(
                10L, "Alimento Premium", "Alimento", new BigDecimal("8500.00"), 20);
        when(productoService.guardar(any(ProductoRequestDTO.class))).thenReturn(creado);

        // Act + Assert: el controller responde 201 con header Location y _links
        mockMvc.perform(post("/api/v2/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/v2/productos/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(productoService).guardar(any(ProductoRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v2/productos/{id}: actualiza y devuelve 200 con enlaces")
    void actualizar_conDatosValidos_devuelve200ConLinks() throws Exception {
        // Arrange
        ProductoRequestDTO request = new ProductoRequestDTO(
                "Vacuna Antirrabica", "Vacuna", new BigDecimal("12000.00"), 40);
        when(productoService.actualizar(eq(1L), any(ProductoRequestDTO.class)))
                .thenReturn(nuevoDTO(1L, 40));

        // Act + Assert
        mockMvc.perform(put("/api/v2/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(40))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(productoService).actualizar(eq(1L), any(ProductoRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v2/productos/{id}: elimina y devuelve 204 sin contenido")
    void eliminar_devuelve204() throws Exception {
        // Act + Assert: el service no devuelve nada, el controller responde 204
        mockMvc.perform(delete("/api/v2/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminar(1L);
    }
}
