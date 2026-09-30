package com.tienda.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.tienda.excepciones.OperacionInvalidaException;

/**
 * Representa cada artículo disponible para la venta (RF01-RF04).
 * Encapsula sus atributos: solo se accede/modifica a través de getters y setters,
 * lo que permite controlar reglas como "el stock no puede quedar negativo".
 */
public class Producto {

    private final int idProducto;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private String categoria;

    public Producto(int idProducto, String nombre, BigDecimal precio, int stock, String categoria) {
        if (idProducto <= 0) throw new OperacionInvalidaException("El id del producto debe ser positivo.");
        this.idProducto = idProducto;
        setNombre(nombre);
        setPrecio(precio);
        setStock(stock);
        setCategoria(categoria);
    }

    // ---- Comportamiento propio del producto ----

    /** Reduce el stock cuando se confirma una venta (RF14). */
    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new OperacionInvalidaException("La cantidad a reducir debe ser mayor a cero.");
        }
        if (cantidad > this.stock) {
            throw new IllegalStateException(
                "Stock insuficiente para el producto '" + nombre + "'. Disponible: " + stock);
        }
        this.stock -= cantidad;
    }

    /** Indica si hay stock suficiente para vender la cantidad solicitada (RF15). */
    public boolean tieneStockSuficiente(int cantidadSolicitada) {
        return this.stock >= cantidadSolicitada;
    }

    // ---- Getters y setters (encapsulamiento) ----

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new OperacionInvalidaException("El nombre del producto es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() < 0) {
            throw new OperacionInvalidaException("El precio no puede ser negativo.");
        }
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new OperacionInvalidaException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new OperacionInvalidaException("La categoría es obligatoria.");
        }
        this.categoria = categoria.trim();
    }

    @Override
    public String toString() {
        return String.format("[%d] %-20s $%-10s stock:%-5d categoria:%s",
                idProducto, nombre, precio, stock, categoria);
    }
}
