package com.tienda.excepciones;

/**
 * Se lanza cuando se busca un Producto, Cliente o Venta
 * por un id que no existe en el sistema.
 */
public class EntidadNoEncontradaException extends RuntimeException {

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
