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

    @Test
    @DisplayName("obtenerPorId: devuelve el DTO cuando el dueno existe (caso feliz)")
    void obtenerPorId_cuandoExiste_devuelveDTO() {
        // Arrange
        when(duenoRepository.findById(1L))
                .thenReturn(Optional.of(nuevoDueno(1L, "11111111-1")));

        // Act
        DuenoResponseDTO resultado = duenoService.obtenerPorId(1L);

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("11111111-1", resultado.getRut());
        assertEquals("Juan Perez", resultado.getNombre());
        verify(duenoRepository).findById(1L);
    }

    @Test
    @DisplayName("actualizar: lanza RecursoNoEncontradoException si el dueno no existe")
    void actualizar_duenoInexistente_lanzaExcepcion() {
        // Arrange
        DuenoRequestDTO dto = new DuenoRequestDTO(
                "Juan Perez", "11111111-1", "juan@mail.com", "+56911111111");
        when(duenoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> duenoService.actualizar(99L, dto));
        verify(duenoRepository, never()).save(any(Dueno.class));
    }

    @Test
    @DisplayName("actualizar: actualiza el dueno cuando existe y el RUT no esta en uso (caso feliz)")
    void actualizar_datosValidos_actualizaDueno() {
        // Arrange: edito el dueno 1 manteniendo su mismo RUT
        DuenoRequestDTO dto = new DuenoRequestDTO(
                "Juan Perez Editado", "11111111-1", "nuevo@mail.com", "+56999999999");
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(nuevoDueno(1L, "11111111-1")));
        when(duenoRepository.findByRut("11111111-1"))
                .thenReturn(Optional.of(nuevoDueno(1L, "11111111-1")));
        when(duenoRepository.save(any(Dueno.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        DuenoResponseDTO resultado = duenoService.actualizar(1L, dto);

        // Assert
        assertEquals("Juan Perez Editado", resultado.getNombre());
        assertEquals("nuevo@mail.com", resultado.getEmail());
        assertEquals("+56999999999", resultado.getTelefono());
        verify(duenoRepository).save(any(Dueno.class));
    }

    @Test
    @DisplayName("actualizar: actualiza con RUT nuevo que no usa ningun dueno (caso feliz)")
    void actualizar_rutNuevoLibre_actualizaDueno() {
        // Arrange: edito el dueno 1 y le asigno un RUT que nadie tiene
        DuenoRequestDTO dto = new DuenoRequestDTO(
                "Juan Perez", "99999999-9", "juan@mail.com", "+56911111111");
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(nuevoDueno(1L, "11111111-1")));
        when(duenoRepository.findByRut("99999999-9")).thenReturn(Optional.empty());
        when(duenoRepository.save(any(Dueno.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        DuenoResponseDTO resultado = duenoService.actualizar(1L, dto);

        // Assert
        assertEquals("99999999-9", resultado.getRut());
        verify(duenoRepository).save(any(Dueno.class));
    }

    @Test
    @DisplayName("eliminar: elimina el dueno cuando existe (caso feliz)")
    void eliminar_duenoExistente_eliminaDueno() {
        // Arrange
        when(duenoRepository.existsById(1L)).thenReturn(true);

        // Act
        duenoService.eliminar(1L);

        // Assert
        verify(duenoRepository).deleteById(1L);
    }
}
