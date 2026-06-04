package com.veterinaria.usuarios.service;

import com.veterinaria.usuarios.dto.DuenoRequestDTO;
import com.veterinaria.usuarios.dto.DuenoResponseDTO;
import com.veterinaria.usuarios.exception.RecursoDuplicadoException;
import com.veterinaria.usuarios.exception.RecursoNoEncontradoException;
import com.veterinaria.usuarios.model.Dueno;
import com.veterinaria.usuarios.repository.DuenoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ═══════════════════════════════════════════════════
// Pruebas unitarias de DuenoService (JUnit 5 + Mockito).
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@ExtendWith(MockitoExtension.class)
class DuenoServiceTest {

    @Mock
    private DuenoRepository duenoRepository;

    @InjectMocks
    private DuenoService duenoService;

    private Dueno nuevoDueno(Long id, String rut) {
        return new Dueno(id, "Juan Perez", rut, "juan@mail.com", "+56911111111");
    }

    @Test
    @DisplayName("obtenerTodos: devuelve la lista de duenos mapeada a DTO")
    void obtenerTodos_devuelveLista() {
        // Arrange
        when(duenoRepository.findAll()).thenReturn(List.of(
                nuevoDueno(1L, "11111111-1"),
                nuevoDueno(2L, "22222222-2")));

        // Act
        List<DuenoResponseDTO> resultado = duenoService.obtenerTodos();

        // Assert
        assertEquals(2, resultado.size());
        verify(duenoRepository).findAll();
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepcion cuando el dueno no existe")
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        // Arrange
        when(duenoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> duenoService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("guardar: crea el dueno cuando el RUT no existe (caso feliz)")
    void guardar_rutNuevo_creaDueno() {
        // Arrange
        DuenoRequestDTO dto = new DuenoRequestDTO(
                "Ana Soto", "33333333-3", "ana@mail.com", "+56922222222");
        when(duenoRepository.findByRut("33333333-3")).thenReturn(Optional.empty());
        when(duenoRepository.save(any(Dueno.class)))
                .thenReturn(nuevoDueno(5L, "33333333-3"));

        // Act
        DuenoResponseDTO resultado = duenoService.guardar(dto);

        // Assert
        assertEquals(5L, resultado.getId());
        verify(duenoRepository).save(any(Dueno.class));
    }

    @Test
    @DisplayName("guardar: lanza RecursoDuplicadoException si el RUT ya existe (regla de negocio)")
    void guardar_rutDuplicado_lanzaExcepcion() {
        // Arrange: ya existe un dueno con ese RUT
        DuenoRequestDTO dto = new DuenoRequestDTO(
                "Ana Soto", "33333333-3", "ana@mail.com", "+56922222222");
        when(duenoRepository.findByRut("33333333-3"))
                .thenReturn(Optional.of(nuevoDueno(1L, "33333333-3")));

        // Act + Assert
        assertThrows(RecursoDuplicadoException.class,
                () -> duenoService.guardar(dto));
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("actualizar: lanza RecursoDuplicadoException si el RUT pertenece a OTRO dueno")
    void actualizar_rutDeOtroDueno_lanzaExcepcion() {
        // Arrange: edito el dueno 1, pero el RUT lo tiene el dueno 2
        DuenoRequestDTO dto = new DuenoRequestDTO(
                "Juan Perez", "22222222-2", "juan@mail.com", "+56911111111");
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(nuevoDueno(1L, "11111111-1")));
        when(duenoRepository.findByRut("22222222-2"))
                .thenReturn(Optional.of(nuevoDueno(2L, "22222222-2")));

        // Act + Assert
        assertThrows(RecursoDuplicadoException.class,
                () -> duenoService.actualizar(1L, dto));
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("eliminar: lanza RecursoNoEncontradoException si el dueno no existe")
    void eliminar_duenoInexistente_lanzaExcepcion() {
        // Arrange
        when(duenoRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> duenoService.eliminar(99L));
        verify(duenoRepository, never()).deleteById(any());
    }
}
