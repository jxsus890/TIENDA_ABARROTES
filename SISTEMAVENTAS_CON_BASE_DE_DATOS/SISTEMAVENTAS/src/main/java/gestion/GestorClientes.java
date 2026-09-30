package com.tienda.gestion;

import com.tienda.dao.ClienteDAO;
import com.tienda.dao.VentaDAO;
import com.tienda.excepciones.OperacionInvalidaException;
import com.tienda.modelo.Cliente;

import java.util.List;

/** Lógica de negocio de clientes; la persistencia la maneja ClienteDAO. */
public class GestorClientes {

    private final ClienteDAO dao = new ClienteDAO();
    private final VentaDAO ventaDAO = new VentaDAO();

    public Cliente registrar(String nombre, String telefono) {
        return registrar(nombre, "", telefono);
    }

    public Cliente registrar(String nombre, String cedula, String telefono) {
        validar(nombre);
        return dao.insertar(nombre.trim(), cedula == null ? "" : cedula.trim(), telefono == null ? "" : telefono.trim());
    }

    public List<Cliente> consultar() {
        return List.copyOf(dao.consultarTodos());
    }

    public Cliente buscarPorId(int idCliente) {
        return dao.buscarPorId(idCliente);
    }

    public Cliente buscarPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) return null;
        return dao.buscarPorCedula(cedula.trim());
    }

    public void actualizar(int idCliente, String nombre, String telefono) {
        Cliente existente = dao.buscarPorId(idCliente);
        actualizar(idCliente, nombre, existente.getCedula(), telefono);
    }

    public void actualizar(int idCliente, String nombre, String cedula, String telefono) {
        validar(nombre);
        dao.actualizar(idCliente, nombre.trim(), cedula == null ? "" : cedula.trim(), telefono == null ? "" : telefono.trim());
    }

    public void eliminar(int idCliente) {
        dao.buscarPorId(idCliente);
        if (ventaDAO.existeVentaParaCliente(idCliente)) {
            throw new OperacionInvalidaException("No se puede eliminar el cliente porque tiene ventas registradas.");
        }
        dao.eliminar(idCliente);
    }

    private void validar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new OperacionInvalidaException("El nombre del cliente es obligatorio.");
        }
    }
}
