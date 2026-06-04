package com.veterinaria.usuarios.exception;

// ═══════════════════════════════════════════════════
// Se lanza al intentar crear/actualizar un recurso con
// un valor unico ya existente (ej: RUT repetido).
// El GlobalExceptionHandler la traduce a HTTP 409 Conflict.
// ═══════════════════════════════════════════════════

public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
