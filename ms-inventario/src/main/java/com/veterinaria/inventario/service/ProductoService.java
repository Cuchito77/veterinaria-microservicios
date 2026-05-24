package com.veterinaria.inventario.service;

import com.veterinaria.inventario.dto.ProductoRequestDTO;
import com.veterinaria.inventario.dto.ProductoResponseDTO;
import com.veterinaria.inventario.exception.RecursoNoEncontradoException;
import com.veterinaria.inventario.exception.StockInsuficienteException;
import com.veterinaria.inventario.model.Producto;
import com.veterinaria.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════
// Capa SERVICE de productos.
// Incluye la regla de negocio mas importante de este MS:
// descontar stock validando que no quede negativo.
// ═══════════════════════════════════════════════════

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    private ProductoResponseDTO mapToDTO(Producto p) {
        return new ProductoResponseDTO(p.getId(), p.getNombre(),
                p.getCategoria(), p.getPrecio(), p.getStock());
    }

    public List<ProductoResponseDTO> obtenerTodos() {
        log.info("Listando todos los productos");
        return productoRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + id));
        return mapToDTO(producto);
    }

    public List<ProductoResponseDTO> obtenerPorCategoria(String categoria) {
        log.info("Listando productos de la categoria: {}", categoria);
        return productoRepository.findByCategoria(categoria).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductoResponseDTO guardar(ProductoRequestDTO dto) {
        Producto producto = new Producto(null, dto.getNombre(),
                dto.getCategoria(), dto.getPrecio(), dto.getStock());
        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado con id: {}", guardado.getId());
        return mapToDTO(guardado);
    }

    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + id));
        existente.setNombre(dto.getNombre());
        existente.setCategoria(dto.getCategoria());
        existente.setPrecio(dto.getPrecio());
        existente.setStock(dto.getStock());
        log.info("Producto actualizado con id: {}", id);
        return mapToDTO(productoRepository.save(existente));
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
        log.info("Producto eliminado con id: {}", id);
    }

    // ── REGLA DE NEGOCIO: descontar stock ───────────────
    // Este metodo lo invoca ms-citas (vvia WebClient) cuando
    // una cita consume un producto del inventario.
    public ProductoResponseDTO descontarStock(Long id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con id: " + id));

        if (producto.getStock() < cantidad) {
            log.warn("Stock insuficiente para producto {}: disponible={}, solicitado={}",
                    id, producto.getStock(), cantidad);
            throw new StockInsuficienteException(
                    "Stock insuficiente. Disponible: " + producto.getStock()
                            + ", solicitado: " + cantidad);
        }

        producto.setStock(producto.getStock() - cantidad);
        Producto actualizado = productoRepository.save(producto);
        log.info("Stock descontado en producto {}: -{} unidades, nuevo stock={}",
                id, cantidad, actualizado.getStock());
        return mapToDTO(actualizado);
    }
}
