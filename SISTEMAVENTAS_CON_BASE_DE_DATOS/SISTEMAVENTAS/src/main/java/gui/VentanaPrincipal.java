package com.tienda.gui;

import com.tienda.gestion.SistemaVentas;

import javax.swing.*;
import java.awt.Component;
import java.math.BigDecimal;

/**
 * Ventana principal de la aplicación de escritorio. Agrupa las tres
 * pantallas en pestanas y se encarga de refrescar cada una cuando gana el
 * foco o cuando otra pestaña modificó datos que le afectan (p. ej. una
 * venta reduce el stock que muestra la pestaña de Productos).
 */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        super("Sistema de Ventas");

        SistemaVentas sistema = new SistemaVentas();

        PanelProductos panelProductos = new PanelProductos(sistema);
        PanelClientes panelClientes = new PanelClientes(sistema);
        PanelVentas panelVentas = new PanelVentas(sistema, panelProductos::refrescar);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Productos", panelProductos);
        pestanas.addTab("Clientes", panelClientes);
        pestanas.addTab("Ventas", panelVentas);

        // Al entrar a cada pestaña, se refresca por si otra pestaña cambió datos.
        pestanas.addChangeListener(e -> {
            Component seleccionado = pestanas.getSelectedComponent();
            if (seleccionado == panelProductos) panelProductos.refrescar();
            if (seleccionado == panelClientes) panelClientes.refrescar();
            if (seleccionado == panelVentas) panelVentas.refrescar();
        });

        setContentPane(pestanas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
    }


}
