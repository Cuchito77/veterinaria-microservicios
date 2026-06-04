package com.veterinaria.login.exception;

// ═══════════════════════════════════════════════════
// Se lanza cuando se intenta crear/actualizar un recurso
// con un valor unico ya existente (ej: username repetido).
// El GlobalExceptionHandler la traduce a HTTP 409 Conflict.
// ═══════════════════════════════════════════════════

public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
