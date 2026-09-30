package com.tienda.gestion;

/**
 * Objeto simple de transporte de datos: representa "quiero comprar
 * <cantidad> unidades del producto <idProducto>" antes de que exista
 * la Venta. No es una entidad del dominio, solo entrada para GestorVentas.
 */
public class ItemPedido {

    private final int idProducto;
    private final int cantidad;

    public ItemPedido(int idProducto, int cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }
}
