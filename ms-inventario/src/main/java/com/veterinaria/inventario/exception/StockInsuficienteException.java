package com.veterinaria.inventario.exception;

// Excepcion propia para cuando se intenta descontar mas
// stock del disponible. Se traduce a HTTP 400.
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
