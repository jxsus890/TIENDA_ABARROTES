package com.tienda.modelo;

import com.tienda.excepciones.OperacionInvalidaException;
import com.tienda.excepciones.StockInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Entidad de venta y reglas de cálculo. */
public class Venta {

    private final int idVenta;
    private final LocalDateTime fecha;
    private final Cliente cliente;
    private final List<DetalleVenta> detalles = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO.setScale(2);
    private int contadorDetalle = 1;

    public Venta(int idVenta, Cliente cliente) {
        this(idVenta, cliente, LocalDateTime.now());
    }

    /** Constructor para reconstruir una venta persistida. */
    public Venta(int idVenta, Cliente cliente, LocalDateTime fecha) {
        this.idVenta = idVenta;
        this.cliente = cliente;
        this.fecha = fecha;
    }

    /** Devuelve el detalle creado para facilitar su persistencia. */
    public DetalleVenta agregarDetalle(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new OperacionInvalidaException("La cantidad debe ser mayor a cero.");
        }
        if (!producto.tieneStockSuficiente(cantidad)) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para '" + producto.getNombre() + "'. Disponible: " + producto.getStock());
        }
        DetalleVenta detalle = new DetalleVenta(contadorDetalle++, idVenta, producto, cantidad);
        detalles.add(detalle);
        calcularTotal();
        return detalle;
    }

    /** Agrega una línea ya existente en BD sin recalcular usando el precio actual del producto. */
    public void agregarDetallePersistido(DetalleVenta detalle) {
        detalles.add(detalle);
        contadorDetalle = Math.max(contadorDetalle, detalle.getIdDetalle() + 1);
        calcularTotal();
    }

    public final void calcularTotal() {
        total = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean esValida() { return !detalles.isEmpty(); }
    public int getIdVenta() { return idVenta; }
    public LocalDateTime getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public List<DetalleVenta> getDetalles() { return Collections.unmodifiableList(detalles); }
    public BigDecimal getTotal() { return total; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Venta #%d | %s | Cliente: %s | Total: $%s%n",
                idVenta, fecha, cliente.getNombre(), total));
        for (DetalleVenta d : detalles) sb.append(d).append(System.lineSeparator());
        return sb.toString();
    }
}
