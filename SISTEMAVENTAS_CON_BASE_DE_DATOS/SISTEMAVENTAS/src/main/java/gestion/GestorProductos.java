package com.tienda.gestion;

import com.tienda.dao.ProductoDAO;
import com.tienda.excepciones.EntidadNoEncontradaException;
import com.tienda.excepciones.OperacionInvalidaException;
import com.tienda.modelo.Producto;

import java.math.BigDecimal;
import java.util.List;

/** Lógica de negocio de productos; la persistencia la maneja ProductoDAO. */
public class GestorProductos {

    private final ProductoDAO dao = new ProductoDAO();

    public Producto registrar(String nombre, BigDecimal precio, int stock, String categoria) {
        validar(nombre, precio, stock, categoria);
        return dao.insertar(nombre.trim(), precio, stock, categoria.trim());
    }

    public List<Producto> consultar() {
        return List.copyOf(dao.consultarTodos());
    }

    public Producto buscarPorId(int idProducto) {
        return dao.buscarPorId(idProducto);
    }

    public void actualizar(int idProducto, String nombre, BigDecimal precio, int stock, String categoria) {
        validar(nombre, precio, stock, categoria);
        dao.actualizar(idProducto, nombre.trim(), precio, stock, categoria.trim());
    }

    public void eliminar(int idProducto, List<Integer> idsProductoConVentas) {
        // Se conserva la firma que usa la GUI.
        if (!dao.consultarTodos().stream().anyMatch(p -> p.getIdProducto() == idProducto)) {
            throw new EntidadNoEncontradaException("Producto no encontrado: id " + idProducto);
        }
        if (idsProductoConVentas != null && idsProductoConVentas.contains(idProducto)) {
            throw new OperacionInvalidaException(
                    "No se puede eliminar el producto id " + idProducto + ": tiene ventas registradas.");
        }
        dao.eliminar(idProducto);
    }

    private void validar(String nombre, BigDecimal precio, int stock, String categoria) {
        if (nombre == null || nombre.isBlank()) {
            throw new OperacionInvalidaException("El nombre del producto es obligatorio.");
        }
        if (precio == null || precio.signum() < 0) {
            throw new OperacionInvalidaException("El precio no puede ser negativo.");
        }
        if (stock < 0) {
            throw new OperacionInvalidaException("El stock no puede ser negativo.");
        }
        if (categoria == null || categoria.isBlank()) {
            throw new OperacionInvalidaException("La categoría es obligatoria.");
        }
    }
}
