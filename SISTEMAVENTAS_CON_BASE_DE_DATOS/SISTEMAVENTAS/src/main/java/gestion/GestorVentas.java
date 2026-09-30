package com.tienda.gestion;

import com.tienda.dao.VentaDAO;
import com.tienda.modelo.Venta;

import java.util.List;

/** Orquesta ventas y delega la persistencia transaccional a VentaDAO. */
public class GestorVentas {

    private final VentaDAO dao = new VentaDAO();

    // Se mantiene este constructor para no romper la GUI existente.
    public GestorVentas(GestorClientes gestorClientes, GestorProductos gestorProductos) {
    }

    public Venta registrarVenta(int idCliente, List<ItemPedido> items) {
        return dao.registrarVenta(idCliente, items);
    }

    public Venta buscarPorId(int idVenta) {
        return dao.buscarPorId(idVenta);
    }

    public Venta consultarDetalle(int idVenta) {
        return buscarPorId(idVenta);
    }

    public List<Venta> consultarTodas() {
        return List.copyOf(dao.consultarTodas());
    }

    public List<Integer> idsProductoConVentas() {
        return dao.idsProductoConVentas();
    }

    public List<Venta> consultarPorCliente(int idCliente) {
        return dao.consultarTodas().stream()
                .filter(v -> v.getCliente().getIdCliente() == idCliente)
                .toList();
    }
}
