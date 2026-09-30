package com.tienda.gestion;

import Util.ConexionSQlite;

/** Fachada del sistema; inicializa/verifica la base de datos al arrancar. */
public class SistemaVentas {

    private final GestorProductos gestorProductos;
    private final GestorClientes gestorClientes;
    private final GestorVentas gestorVentas;

    public SistemaVentas() {
        ConexionSQlite.crearTablas();
        gestorProductos = new GestorProductos();
        gestorClientes = new GestorClientes();
        gestorVentas = new GestorVentas(gestorClientes, gestorProductos);
    }

    public GestorProductos productos() {
        return gestorProductos;
    }

    public GestorClientes clientes() {
        return gestorClientes;
    }

    public GestorVentas ventas() {
        return gestorVentas;
    }
}
