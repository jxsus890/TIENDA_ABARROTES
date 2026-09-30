package com.tienda.excepciones;

/**
 * Se lanza cuando se viola una regla de negocio, por ejemplo:
 * - Intentar guardar una venta sin productos (RF18).
 * - Intentar eliminar un producto que ya tiene ventas asociadas (RF04).
 */
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
