package com.tienda.gui;

import javax.swing.table.DefaultTableModel;

/**
 * DefaultTableModel donde ninguna celda es editable directamente por el
 * usuario: toda modificación de datos pasa por los formularios y, de ahí,
 * por las clases de gestión (GestorProductos, GestorClientes, GestorVentas).
 * Así la tabla nunca queda desincronizada del modelo de negocio real.
 */
public class ModeloTablaSoloLectura extends DefaultTableModel {

    public ModeloTablaSoloLectura(Object[] columnas) {
        super(columnas, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
