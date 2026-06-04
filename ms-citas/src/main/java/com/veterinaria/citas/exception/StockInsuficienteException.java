package com.veterinaria.citas.exception;

// ═══════════════════════════════════════════════════
// Se lanza cuando ms-inventario rechaza el descuento de
// stock por no haber unidades suficientes.
// El GlobalExceptionHandler la traduce a HTTP 400.
// ═══════════════════════════════════════════════════

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
