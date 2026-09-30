package com.tienda.excepciones;

/**
 * Se lanza cuando se intenta vender una cantidad de producto
 * mayor al stock disponible (RF15).
 */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
