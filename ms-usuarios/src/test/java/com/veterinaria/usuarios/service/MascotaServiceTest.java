package com.veterinaria.usuarios.service;

import com.veterinaria.usuarios.dto.MascotaRequestDTO;
import com.veterinaria.usuarios.dto.MascotaResponseDTO;
import com.veterinaria.usuarios.exception.RecursoNoEncontradoException;
import com.veterinaria.usuarios.model.Dueno;
import com.veterinaria.usuarios.model.Mascota;
import com.veterinaria.usuarios.repository.DuenoRepository;
import com.veterinaria.usuarios.repository.MascotaRepository;
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
// Pruebas unitarias de MascotaService (JUnit 5 + Mockito).
// Estructura AAA: Arrange (given) - Act (when) - Assert (then)
// ═══════════════════════════════════════════════════

@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private DuenoRepository duenoRepository;

    @InjectMocks
    private MascotaService mascotaService;

    private Dueno nuevoDueno(Long id) {
        return new Dueno(id, "Juan Perez", "11111111-1", "juan@mail.com", "+56911111111");
    }

    private Mascota nuevaMascota(Long id, String nombre, Dueno dueno) {
        return new Mascota(id, nombre, "Perro", "Labrador", 3, dueno);
    }

    @Test
    @DisplayName("obtenerTodas: devuelve la lista de mascotas mapeada a DTO")
    void obtenerTodas_devuelveLista() {
        // Arrange
        Dueno dueno = nuevoDueno(1L);
        when(mascotaRepository.findAll()).thenReturn(List.of(
                nuevaMascota(1L, "Firulais", dueno),
                nuevaMascota(2L, "Bobby", dueno)));

        // Act
        List<MascotaResponseDTO> resultado = mascotaService.obtenerTodas();

        // Assert
        assertEquals(2, resultado.size());
        verify(mascotaRepository).findAll();
    }

    @Test
    @DisplayName("obtenerPorId: devuelve la mascota cuando existe")
    void obtenerPorId_cuandoExiste_devuelveDTO() {
        // Arrange
        Dueno dueno = nuevoDueno(1L);
        when(mascotaRepository.findById(1L))
                .thenReturn(Optional.of(nuevaMascota(1L, "Firulais", dueno)));

        // Act
        MascotaResponseDTO resultado = mascotaService.obtenerPorId(1L);

        // Assert
        assertEquals(1L, resultado.getId());
        assertEquals("Firulais", resultado.getNombre());
        assertEquals(1L, resultado.getDuenoId());
        assertEquals("Juan Perez", resultado.getDuenoNombre());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepcion cuando la mascota no existe")
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        // Arrange
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> mascotaService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("guardar: crea la mascota cuando el dueno existe (caso feliz)")
    void guardar_duenoExistente_creaMascota() {
        // Arrange
        Dueno dueno = nuevoDueno(1L);
        MascotaRequestDTO dto = new MascotaRequestDTO(
                "Firulais", "Perro", "Labrador", 3, 1L);
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(dueno));
        when(mascotaRepository.save(any(Mascota.class)))
                .thenReturn(nuevaMascota(5L, "Firulais", dueno));

        // Act
        MascotaResponseDTO resultado = mascotaService.guardar(dto);

        // Assert
        assertEquals(5L, resultado.getId());
        assertEquals(1L, resultado.getDuenoId());
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    @DisplayName("guardar: lanza RecursoNoEncontradoException si el dueno no existe (regla de negocio)")
    void guardar_duenoInexistente_lanzaExcepcion() {
        // Arrange: la mascota apunta a un dueno que no esta en la BD
        MascotaRequestDTO dto = new MascotaRequestDTO(
                "Firulais", "Perro", "Labrador", 3, 99L);
        when(duenoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> mascotaService.guardar(dto));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    @DisplayName("actualizar: lanza RecursoNoEncontradoException si la mascota no existe")
    void actualizar_mascotaInexistente_lanzaExcepcion() {
        // Arrange
        MascotaRequestDTO dto = new MascotaRequestDTO(
                "Firulais", "Perro", "Labrador", 4, 1L);
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> mascotaService.actualizar(99L, dto));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    @DisplayName("eliminar: lanza RecursoNoEncontradoException si la mascota no existe")
    void eliminar_mascotaInexistente_lanzaExcepcion() {
        // Arrange
        when(mascotaRepository.existsById(99L)).thenReturn(false);

        // Act + Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> mascotaService.eliminar(99L));
        verify(mascotaRepository, never()).deleteById(any());
    }
}
