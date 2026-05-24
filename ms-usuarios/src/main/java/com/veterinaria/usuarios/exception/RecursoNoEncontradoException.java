package com.veterinaria.usuarios.exception;

// Excepcion propia para representar "recurso no encontrado".
// La lanza la capa de servicio y la traduce a HTTP 404
// el GlobalExceptionHandler.
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
