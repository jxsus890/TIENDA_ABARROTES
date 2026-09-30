package com.tienda.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Detalle histórico de una venta. El precio queda congelado. */
public class DetalleVenta {

    private final int idDetalle;
    private final int idVenta;
    private final Producto producto;
    private final int cantidad;
    private final BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetalleVenta(int idDetalle, int idVenta, Producto producto, int cantidad) {
        this(idDetalle, idVenta, producto, cantidad, producto.getPrecio(), null);
    }

    /** Constructor para reconstruir desde SQLite conservando el precio histórico. */
    public DetalleVenta(int idDetalle, int idVenta, Producto producto, int cantidad,
                        BigDecimal precioUnitario, BigDecimal subtotal) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario.setScale(2, RoundingMode.HALF_UP);
        if (subtotal == null) {
            calcularSubtotal();
        } else {
            this.subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public final void calcularSubtotal() {
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int getIdDetalle() { return idDetalle; }
    public int getIdVenta() { return idVenta; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }

    @Override
    public String toString() {
        return String.format("  - %-20s x%-4d @ $%-10s = $%s",
                producto.getNombre(), cantidad, precioUnitario, subtotal);
    }
}
