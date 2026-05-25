package com.veterinaria.login.exception;

//Sirve para retornar mensaje personaizado al buscar algo en la base de datos y no se encuentra

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
