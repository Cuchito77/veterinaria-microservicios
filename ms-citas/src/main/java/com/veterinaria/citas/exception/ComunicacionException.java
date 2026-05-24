package com.veterinaria.citas.exception;

// Excepcion propia para fallos al comunicarse con otro
// microservicio (ms-usuarios o ms-inventario).
public class ComunicacionException extends RuntimeException {
    public ComunicacionException(String mensaje) {
        super(mensaje);
    }
}
