package com.veterinaria.citas.service;

import com.veterinaria.citas.client.InventarioClient;
import com.veterinaria.citas.client.UsuariosClient;
import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.dto.MascotaExternaDTO;
import com.veterinaria.citas.exception.RecursoNoEncontradoException;
import com.veterinaria.citas.exception.StockInsuficienteException;
import com.veterinaria.citas.model.Cita;
import com.veterinaria.citas.repository.CitaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// ═══════════════════════════════════════════════════
// Pruebas unitarias de CitaService.
// Es el caso mas rico: se mockean los CLIENTES de otros
// microservicios (UsuariosClient e InventarioClient) para
// probar la orquestacion sin levantar los otros servicios.
// ═══════════════════════════════════════════════════

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;
    @Mock
    private UsuariosClient usuariosClient;
    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private CitaService citaService;

    private CitaRequestDTO requestSinProducto() {
        return new CitaRequestDTO(1L, "Control general",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 30), null, null);
    }

    private CitaRequestDTO requestConProducto() {
        return new CitaRequestDTO(1L, "Vacunacion",
                LocalDate.of(2026, 6, 10), LocalTime.of(11, 0), 5L, 2);
    }

    @Test
    @DisplayName("guardar: valida la mascota y NO descuenta stock si la cita no usa producto")
    void guardar_sinProducto_validaMascotaYGuarda() {
        // Arrange
        when(usuariosClient.obtenerMascota(1L))
                .thenReturn(new MascotaExternaDTO(1L, "Firulais", "Perro"));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> {
            Cita c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        // Act
        CitaResponseDTO resultado = citaService.guardar(requestSinProducto());

        // Assert
        assertEquals(100L, resultado.getId());
        assertEquals("Firulais", resultado.getMascotaNombre());
        verify(usuariosClient).obtenerMascota(1L);
        // como no hay producto, NO debe llamar a ms-inventario
        verify(inventarioClient, never()).descontarStock(anyLong(), anyInt());
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("guardar: si la cita usa producto, descuenta stock en ms-inventario")
    void guardar_conProducto_descuentaStock() {
        // Arrange
        when(usuariosClient.obtenerMascota(1L))
                .thenReturn(new MascotaExternaDTO(1L, "Firulais", "Perro"));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> {
            Cita c = inv.getArgument(0);
            c.setId(101L);
            return c;
        });

        // Act
        citaService.guardar(requestConProducto());

        // Assert: se descuentan 2 unidades del producto 5
        verify(inventarioClient).descontarStock(5L, 2);
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("obtenerPorId: lanza RecursoNoEncontradoException si la cita no existe")
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        // Arrange
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> citaService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("cambiarEstado: actualiza el estado de la cita")
    void cambiarEstado_actualizaEstado() {
        // Arrange
        Cita cita = new Cita(1L, 1L, "Firulais", "Control",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 0), null, null, "PROGRAMADA");
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        CitaResponseDTO resultado = citaService.cambiarEstado(1L, "ATENDIDA");

        // Assert
        assertEquals("ATENDIDA", resultado.getEstado());
        verify(citaRepository).save(cita);
    }

    @Test
    @DisplayName("eliminar: lanza RecursoNoEncontradoException si la cita no existe")
    void eliminar_citaInexistente_lanzaExcepcion() {
        // Arrange
        when(citaRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> citaService.eliminar(99L));
        verify(citaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("obtenerTodas: mapea todas las citas del repositorio a DTOs")
    void obtenerTodas_retornaListaMapeada() {
        // Arrange
        Cita cita = new Cita(1L, 1L, "Firulais", "Control",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 0), null, null, "PROGRAMADA");
        when(citaRepository.findAll()).thenReturn(List.of(cita));

        // Act
        List<CitaResponseDTO> resultado = citaService.obtenerTodas();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Firulais", resultado.get(0).getMascotaNombre());
        verify(citaRepository).findAll();
    }

    @Test
    @DisplayName("obtenerPorId: retorna el DTO cuando la cita existe")
    void obtenerPorId_cuandoExiste_retornaDTO() {
        // Arrange
        Cita cita = new Cita(7L, 1L, "Firulais", "Control",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 0), null, null, "PROGRAMADA");
        when(citaRepository.findById(7L)).thenReturn(Optional.of(cita));

        // Act
        CitaResponseDTO resultado = citaService.obtenerPorId(7L);

        // Assert
        assertEquals(7L, resultado.getId());
        assertEquals("Firulais", resultado.getMascotaNombre());
    }

    @Test
    @DisplayName("obtenerPorMascota: lista las citas asociadas a una mascota")
    void obtenerPorMascota_retornaListaMapeada() {
        // Arrange
        Cita cita = new Cita(1L, 3L, "Rex", "Vacuna",
                LocalDate.of(2026, 6, 10), LocalTime.of(9, 0), null, null, "PROGRAMADA");
        when(citaRepository.findByMascotaId(3L)).thenReturn(List.of(cita));

        // Act
        List<CitaResponseDTO> resultado = citaService.obtenerPorMascota(3L);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get(0).getMascotaId());
        verify(citaRepository).findByMascotaId(3L);
    }

    @Test
    @DisplayName("guardar: si la cantidad de producto es null, descuenta 1 unidad por defecto")
    void guardar_conProductoSinCantidad_descuentaUnaUnidad() {
        // Arrange: producto presente pero cantidad null -> debe usar 1
        CitaRequestDTO request = new CitaRequestDTO(1L, "Vacunacion",
                LocalDate.of(2026, 6, 10), LocalTime.of(11, 0), 8L, null);
        when(usuariosClient.obtenerMascota(1L))
                .thenReturn(new MascotaExternaDTO(1L, "Firulais", "Perro"));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> {
            Cita c = inv.getArgument(0);
            c.setId(102L);
            return c;
        });

        // Act
        citaService.guardar(request);

        // Assert: al no especificar cantidad se descuenta 1
        verify(inventarioClient).descontarStock(8L, 1);
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("guardar: si la mascota no existe en ms-usuarios, propaga RecursoNoEncontradoException")
    void guardar_mascotaInexistente_propagaExcepcion() {
        // Arrange
        when(usuariosClient.obtenerMascota(1L))
                .thenThrow(new RecursoNoEncontradoException("Mascota no encontrada"));

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> citaService.guardar(requestSinProducto()));
        // no debe guardar nada si falla la validacion de la mascota
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("guardar: si no hay stock suficiente en ms-inventario, propaga StockInsuficienteException")
    void guardar_sinStock_propagaExcepcion() {
        // Arrange
        when(usuariosClient.obtenerMascota(1L))
                .thenReturn(new MascotaExternaDTO(1L, "Firulais", "Perro"));
        doThrow(new StockInsuficienteException("Stock insuficiente"))
                .when(inventarioClient).descontarStock(5L, 2);

        // Act + Assert
        assertThrows(StockInsuficienteException.class,
                () -> citaService.guardar(requestConProducto()));
        // no debe guardar la cita si el descuento de stock falla
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("actualizar: revalida la mascota y persiste los cambios cuando la cita existe")
    void actualizar_cuandoExiste_actualizaYGuarda() {
        // Arrange
        Cita existente = new Cita(4L, 1L, "Firulais", "Control",
                LocalDate.of(2026, 6, 10), LocalTime.of(10, 0), null, null, "PROGRAMADA");
        when(citaRepository.findById(4L)).thenReturn(Optional.of(existente));
        when(usuariosClient.obtenerMascota(1L))
                .thenReturn(new MascotaExternaDTO(1L, "Firulais", "Perro"));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        CitaResponseDTO resultado = citaService.actualizar(4L, requestConProducto());

        // Assert
        assertEquals(4L, resultado.getId());
        assertEquals("Vacunacion", resultado.getMotivo());
        assertEquals(5L, resultado.getProductoId());
        verify(usuariosClient).obtenerMascota(1L);
        verify(citaRepository).save(existente);
    }

    @Test
    @DisplayName("actualizar: lanza RecursoNoEncontradoException si la cita no existe")
    void actualizar_cuandoNoExiste_lanzaExcepcion() {
        // Arrange
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> citaService.actualizar(99L, requestSinProducto()));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("eliminar: elimina la cita cuando existe")
    void eliminar_cuandoExiste_eliminaCita() {
        // Arrange
        when(citaRepository.existsById(1L)).thenReturn(true);

        // Act
        citaService.eliminar(1L);

        // Assert
        verify(citaRepository).deleteById(1L);
    }
}
