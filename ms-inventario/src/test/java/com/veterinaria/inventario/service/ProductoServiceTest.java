package com.veterinaria.inventario.service;

import com.veterinaria.inventario.dto.ProductoRequestDTO;
import com.veterinaria.inventario.dto.ProductoResponseDTO;
import com.veterinaria.inventario.exception.RecursoNoEncontradoException;
import com.veterinaria.inventario.exception.StockInsuficienteException;
import com.veterinaria.inventario.model.Producto;
import com.veterinaria.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// ═══════════════════════════════════════════════════
// Pruebas unitarias de ProductoService.
// - @Mock        -> simula el repositorio (no toca la BD)
// - @InjectMocks -> inyecta los mocks dentro del service
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    // Helper para crear un producto de ejemplo
    private Producto nuevoProducto(Long id, int stock) {
        return new Producto(id, "Vacuna Antirrabica", "Vacuna",
                new BigDecimal("12000.00"), stock);
    }

    @Test
    @DisplayName("obtenerTodos: devuelve la lista de productos mapeada a DTO")
    void obtenerTodos_devuelveListaDeProductos() {
        // Arrange
        when(productoRepository.findAll()).thenReturn(List.of(
                nuevoProducto(1L, 50),
                nuevoProducto(2L, 30)));

        // Act
        List<ProductoResponseDTO> resultado = productoService.obtenerTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Vacuna Antirrabica", resultado.get(0).getNombre());
        verify(productoRepository).findAll();
    }

    @Test
    @DisplayName("obtenerPorId: devuelve el producto cuando existe")
    void obtenerPorId_cuandoExiste_devuelveProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(nuevoProducto(1L, 50)));

        // Act
        ProductoResponseDTO resultado = productoService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(50, resultado.getStock());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepcion cuando el producto no existe")
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("guardar: crea el producto y devuelve el DTO")
    void guardar_creaProducto() {
        // Arrange
        ProductoRequestDTO dto = new ProductoRequestDTO(
                "Alimento Premium", "Alimento", new BigDecimal("8500.00"), 20);
        when(productoRepository.save(any(Producto.class)))
                .thenReturn(new Producto(10L, "Alimento Premium", "Alimento",
                        new BigDecimal("8500.00"), 20));

        // Act
        ProductoResponseDTO resultado = productoService.guardar(dto);

        // Assert
        assertEquals(10L, resultado.getId());
        assertEquals("Alimento Premium", resultado.getNombre());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("descontarStock: con stock suficiente descuenta correctamente (caso feliz)")
    void descontarStock_conStockSuficiente_descuenta() {
        // Arrange: producto con 50 unidades
        Producto producto = nuevoProducto(1L, 50);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act: descontamos 20
        ProductoResponseDTO resultado = productoService.descontarStock(1L, 20);

        // Assert: deben quedar 30
        assertEquals(30, resultado.getStock());
        verify(productoRepository).save(producto);
    }

    @Test
    @DisplayName("descontarStock: con stock insuficiente lanza StockInsuficienteException (caso error)")
    void descontarStock_conStockInsuficiente_lanzaExcepcion() {
        // Arrange: producto con solo 5 unidades
        Producto producto = nuevoProducto(1L, 5);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act + Assert: pedimos 10 -> debe fallar
        assertThrows(StockInsuficienteException.class,
                () -> productoService.descontarStock(1L, 10));

        // No debe guardar nada si la regla de negocio falla
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("descontarStock: si el producto no existe lanza RecursoNoEncontradoException")
    void descontarStock_productoInexistente_lanzaExcepcion() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.descontarStock(99L, 1));
    }

    @Test
    @DisplayName("eliminar: si el producto no existe lanza RecursoNoEncontradoException")
    void eliminar_productoInexistente_lanzaExcepcion() {
        // Arrange
        when(productoRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(anyLong());
    }
}
