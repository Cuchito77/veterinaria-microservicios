package com.veterinaria.inventario.repository;

import com.veterinaria.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Query Method: productos por categoria
    List<Producto> findByCategoria(String categoria);
}
