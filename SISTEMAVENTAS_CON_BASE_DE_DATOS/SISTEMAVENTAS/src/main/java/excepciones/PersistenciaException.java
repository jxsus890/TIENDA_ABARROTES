package com.tienda.excepciones;

/** Error técnico ocurrido al acceder a la base de datos. */
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenciaException(String message) {
        super(message);
    }
}
