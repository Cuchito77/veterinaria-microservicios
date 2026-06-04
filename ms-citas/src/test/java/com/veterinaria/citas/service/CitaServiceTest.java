package com.veterinaria.citas.service;

import com.veterinaria.citas.client.InventarioClient;
import com.veterinaria.citas.client.UsuariosClient;
import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.dto.MascotaExternaDTO;
import com.veterinaria.citas.exception.RecursoNoEncontradoException;
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
}
