package com.tienda.dao;

import Util.ConexionSQlite;
import com.tienda.excepciones.EntidadNoEncontradaException;
import com.tienda.excepciones.OperacionInvalidaException;
import com.tienda.excepciones.PersistenciaException;
import com.tienda.excepciones.StockInsuficienteException;
import com.tienda.gestion.ItemPedido;
import com.tienda.modelo.Cliente;
import com.tienda.modelo.DetalleVenta;
import com.tienda.modelo.Producto;
import com.tienda.modelo.Venta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistencia de ventas. La creación de Venta + detalles + descuento de stock
 * ocurre dentro de UNA transacción SQLite (BEGIN/COMMIT/ROLLBACK).
 */
public class VentaDAO {

    public Venta registrarVenta(int idCliente, List<ItemPedido> items) {
        if (items == null || items.isEmpty()) {
            throw new OperacionInvalidaException("La venta debe contener al menos un producto.");
        }

        LocalDateTime fecha = LocalDateTime.now();

        try (Connection conn = ConexionSQlite.conectar()) {
            conn.setAutoCommit(false);
            try {
                Cliente cliente = buscarCliente(conn, idCliente);

                int idVenta = insertarVentaInicial(conn, idCliente, fecha);
                Venta venta = new Venta(idVenta, cliente, fecha);

                for (ItemPedido item : items) {
                    if (item == null || item.getCantidad() <= 0) {
                        throw new OperacionInvalidaException("La cantidad de cada producto debe ser mayor que cero.");
                    }

                    Producto producto = buscarProducto(conn, item.getIdProducto());
                    if (producto == null) {
                        throw new EntidadNoEncontradaException("Producto no encontrado: id " + item.getIdProducto());
                    }

                    // Se comprueba y descuenta de forma atómica. Si otra operación
                    // hubiera cambiado el stock, la condición evita dejarlo negativo.
                    int filas = descontarStock(conn, producto.getIdProducto(), item.getCantidad());
                    if (filas != 1) {
                        throw new StockInsuficienteException(
                                "Stock insuficiente para '" + producto.getNombre() + "'. Disponible: " + producto.getStock());
                    }

                    DetalleVenta detalle = venta.agregarDetalle(producto, item.getCantidad());
                    insertarDetalle(conn, detalle);
                }

                if (!venta.esValida()) {
                    throw new OperacionInvalidaException("La venta debe contener al menos un producto.");
                }

                actualizarTotal(conn, idVenta, venta.getTotal());
                conn.commit();
                return venta;
            } catch (RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
                throw ex;
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
                throw new PersistenciaException("La venta fue cancelada y se hizo rollback: " + ex.getMessage(), ex);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo procesar la venta: " + ex.getMessage(), ex);
        }
    }

    public Venta buscarPorId(int idVenta) {
        String sqlVenta = """
            SELECT v.id_venta, v.fecha, v.total,
                   c.id_cliente, c.nombre, c.cedula, c.telefono
            FROM Venta v
            JOIN Cliente c ON c.id_cliente = v.id_cliente
            WHERE v.id_venta = ?
            """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sqlVenta)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new EntidadNoEncontradaException("Venta no encontrada: id " + idVenta);
                Venta venta = mapearVenta(rs);
                cargarDetalles(conn, Map.of(idVenta, venta));
                return venta;
            }
        } catch (EntidadNoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar la venta: " + e.getMessage(), e);
        }
    }

    public List<Venta> consultarTodas() {
        String sqlVentas = """
            SELECT v.id_venta, v.fecha, v.total,
                   c.id_cliente, c.nombre, c.cedula, c.telefono
            FROM Venta v
            JOIN Cliente c ON c.id_cliente = v.id_cliente
            ORDER BY v.id_venta
            """;

        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sqlVentas);
             ResultSet rs = ps.executeQuery()) {
            Map<Integer, Venta> ventas = new LinkedHashMap<>();
            while (rs.next()) {
                Venta venta = mapearVenta(rs);
                ventas.put(venta.getIdVenta(), venta);
            }
            cargarDetalles(conn, ventas);
            return new ArrayList<>(ventas.values());
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron consultar las ventas: " + e.getMessage(), e);
        }
    }

    public List<Integer> idsProductoConVentas() {
        String sql = "SELECT DISTINCT id_producto FROM Detalle_venta ORDER BY id_producto";
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ids.add(rs.getInt(1));
            return ids;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron consultar los productos vendidos: " + e.getMessage(), e);
        }
    }

    public boolean existeVentaParaCliente(int idCliente) {
        String sql = "SELECT 1 FROM Venta WHERE id_cliente = ? LIMIT 1";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo comprobar el historial del cliente: " + e.getMessage(), e);
        }
    }

    private Cliente buscarCliente(Connection conn, int idCliente) throws SQLException {
        String sql = "SELECT id_cliente, nombre, cedula, telefono FROM Cliente WHERE id_cliente = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new EntidadNoEncontradaException("Cliente no encontrado: id " + idCliente);
                String cedula = rs.getString("cedula");
                return new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"), cedula == null ? "" : cedula, rs.getString("telefono"));
            }
        }
    }

    private Producto buscarProducto(Connection conn, int idProducto) throws SQLException {
        String sql = "SELECT id_producto, nombre, precio, stock, categoria FROM Producto WHERE id_producto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock"),
                        rs.getString("categoria"));
            }
        }
    }

    private int insertarVentaInicial(Connection conn, int idCliente, LocalDateTime fecha) throws SQLException {
        String sql = "INSERT INTO Venta(fecha, id_cliente, total) VALUES(?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fecha.toString());
            ps.setInt(2, idCliente);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("No se obtuvo el id de la venta.");
                return rs.getInt(1);
            }
        }
    }

    private int descontarStock(Connection conn, int idProducto, int cantidad) throws SQLException {
        String sql = "UPDATE Producto SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);
            return ps.executeUpdate();
        }
    }

    private void insertarDetalle(Connection conn, DetalleVenta detalle) throws SQLException {
        String sql = """
            INSERT INTO Detalle_venta(id_venta, id_producto, cantidad, precio_unitario, subtotal)
            VALUES(?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, detalle.getIdVenta());
            ps.setInt(2, detalle.getProducto().getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setBigDecimal(4, detalle.getPrecioUnitario());
            ps.setBigDecimal(5, detalle.getSubtotal());
            ps.executeUpdate();
        }
    }

    private void actualizarTotal(Connection conn, int idVenta, BigDecimal total) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Venta SET total = ? WHERE id_venta = ?")) {
            ps.setBigDecimal(1, total);
            ps.setInt(2, idVenta);
            ps.executeUpdate();
        }
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        String cedula = rs.getString("cedula");
        Cliente cliente = new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre"),
                cedula == null ? "" : cedula,
                rs.getString("telefono"));
        LocalDateTime fecha = LocalDateTime.parse(rs.getString("fecha"));
        Venta venta = new Venta(rs.getInt("id_venta"), cliente, fecha);
        return venta;
    }

    private void cargarDetalles(Connection conn, Map<Integer, Venta> ventas) throws SQLException {
        if (ventas.isEmpty()) return;
        String sql = """
            SELECT d.id_detalle, d.id_venta, d.id_producto, d.cantidad,
                   d.precio_unitario, d.subtotal,
                   p.nombre, p.precio, p.stock, p.categoria
            FROM Detalle_venta d
            JOIN Producto p ON p.id_producto = d.id_producto
            ORDER BY d.id_detalle
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Venta venta = ventas.get(rs.getInt("id_venta"));
                if (venta == null) continue;
                Producto producto = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock"),
                        rs.getString("categoria"));
                DetalleVenta detalle = new DetalleVenta(
                        rs.getInt("id_detalle"),
                        rs.getInt("id_venta"),
                        producto,
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getBigDecimal("subtotal"));
                venta.agregarDetallePersistido(detalle);
            }
        }
    }
}
