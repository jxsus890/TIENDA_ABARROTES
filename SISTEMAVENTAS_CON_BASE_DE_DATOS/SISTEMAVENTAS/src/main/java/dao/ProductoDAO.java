package com.tienda.dao;

import Util.ConexionSQlite;
import com.tienda.excepciones.EntidadNoEncontradaException;
import com.tienda.excepciones.PersistenciaException;
import com.tienda.modelo.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de Producto. */
public class ProductoDAO {

    public Producto insertar(String nombre, BigDecimal precio, int stock, String categoria) {
        String sql = "INSERT INTO Producto(nombre, precio, stock, categoria) VALUES(?, ?, ?, ?)";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setBigDecimal(2, precio);
            ps.setInt(3, stock);
            ps.setString(4, categoria);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("No se obtuvo el id del producto.");
                return new Producto(rs.getInt(1), nombre, precio, stock, categoria);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo registrar el producto: " + e.getMessage(), e);
        }
    }

    public List<Producto> consultarTodos() {
        String sql = "SELECT id_producto, nombre, precio, stock, categoria FROM Producto ORDER BY id_producto";
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) productos.add(mapear(rs));
            return productos;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron consultar los productos: " + e.getMessage(), e);
        }
    }

    public Producto buscarPorId(int id) {
        String sql = "SELECT id_producto, nombre, precio, stock, categoria FROM Producto WHERE id_producto = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new EntidadNoEncontradaException("Producto no encontrado: id " + id);
                return mapear(rs);
            }
        } catch (EntidadNoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el producto: " + e.getMessage(), e);
        }
    }

    public void actualizar(int id, String nombre, BigDecimal precio, int stock, String categoria) {
        String sql = "UPDATE Producto SET nombre = ?, precio = ?, stock = ?, categoria = ? WHERE id_producto = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setBigDecimal(2, precio);
            ps.setInt(3, stock);
            ps.setString(4, categoria);
            ps.setInt(5, id);
            if (ps.executeUpdate() == 0) {
                throw new EntidadNoEncontradaException("Producto no encontrado: id " + id);
            }
        } catch (EntidadNoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el producto: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM Producto WHERE id_producto = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new EntidadNoEncontradaException("Producto no encontrado: id " + id);
            }
        } catch (EntidadNoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            // También queda protegido por FK/RESTRICT.
            throw new PersistenciaException("No se pudo eliminar el producto: " + e.getMessage(), e);
        }
    }

    public boolean tieneVentas(int idProducto) {
        String sql = "SELECT 1 FROM Detalle_venta WHERE id_producto = ? LIMIT 1";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo comprobar si el producto tiene ventas: " + e.getMessage(), e);
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

    private Producto mapear(ResultSet rs) throws SQLException {
        BigDecimal precio = rs.getBigDecimal("precio");
        if (precio == null) precio = BigDecimal.ZERO;
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                precio,
                rs.getInt("stock"),
                rs.getString("categoria"));
    }
}
