package com.tienda.dao;

import Util.ConexionSQlite;
import com.tienda.excepciones.EntidadNoEncontradaException;
import com.tienda.excepciones.PersistenciaException;
import com.tienda.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de Cliente. */
public class ClienteDAO {

    public Cliente insertar(String nombre, String cedula, String telefono) {
        String sql = "INSERT INTO Cliente(nombre, cedula, telefono) VALUES(?, ?, ?)";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            if (cedula == null || cedula.isBlank()) ps.setNull(2, java.sql.Types.VARCHAR);
            else ps.setString(2, cedula);
            ps.setString(3, telefono);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("No se obtuvo el id del cliente.");
                return new Cliente(rs.getInt(1), nombre, cedula == null ? "" : cedula, telefono);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo registrar el cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> consultarTodos() {
        String sql = "SELECT id_cliente, nombre, cedula, telefono FROM Cliente ORDER BY id_cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) clientes.add(mapear(rs));
            return clientes;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron consultar los clientes: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT id_cliente, nombre, cedula, telefono FROM Cliente WHERE id_cliente = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new EntidadNoEncontradaException("Cliente no encontrado: id " + id);
                return mapear(rs);
            }
        } catch (EntidadNoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorCedula(String cedula) {
        String sql = "SELECT id_cliente, nombre, cedula, telefono FROM Cliente WHERE cedula = ? LIMIT 1";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el cliente por cédula: " + e.getMessage(), e);
        }
    }

    public void actualizar(int id, String nombre, String cedula, String telefono) {
        String sql = "UPDATE Cliente SET nombre = ?, cedula = ?, telefono = ? WHERE id_cliente = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, cedula);
            ps.setString(3, telefono);
            ps.setInt(4, id);
            if (ps.executeUpdate() == 0) {
                throw new EntidadNoEncontradaException("Cliente no encontrado: id " + id);
            }
        } catch (EntidadNoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el cliente: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM Cliente WHERE id_cliente = ?";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new EntidadNoEncontradaException("Cliente no encontrado: id " + id);
            }
        } catch (EntidadNoEncontradaException e) {
            throw e;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el cliente: " + e.getMessage(), e);
        }
    }

    public boolean tieneVentas(int idCliente) {
        String sql = "SELECT 1 FROM Venta WHERE id_cliente = ? LIMIT 1";
        try (Connection conn = ConexionSQlite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo comprobar si el cliente tiene ventas: " + e.getMessage(), e);
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        String cedula = rs.getString("cedula");
        return new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"), cedula == null ? "" : cedula, rs.getString("telefono"));
    }
}
