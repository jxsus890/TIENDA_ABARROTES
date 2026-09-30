package com.tienda.gui;

import com.tienda.gestion.SistemaVentas;
import com.tienda.modelo.Cliente;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de Clientes: lista + formulario de alta/edición/baja (RF05-RF07).
 */
public class PanelClientes extends JPanel {

    private final SistemaVentas sistema;

    private final ModeloTablaSoloLectura modeloTabla =
        new ModeloTablaSoloLectura(new Object[]{"ID", "Nombre", "Teléfono"});
    private final JTable tabla = UIConstantes.styledTable(modeloTabla);

    private final JTextField campoNombre = UIConstantes.inputField();
    private final JTextField campoTelefono = UIConstantes.inputField();
    private Integer idSeleccionado = null;

    public PanelClientes(SistemaVentas sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(0, 16));
        setBackground(UIConstantes.BG);
        setBorder(BorderFactory.createEmptyBorder(10, 26, 22, 28));

        tabla.setRowHeight(38);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(UIConstantes.CARD);
        tabla.setForeground(UIConstantes.TEXT);
        tabla.setSelectionBackground(UIConstantes.PRIMARY);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(UIConstantes.LINE);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarSeleccionEnFormulario();
            }
        });

        add(crearPanelTabla(), BorderLayout.CENTER);
        add(construirFormulario(), BorderLayout.SOUTH);

        SwingUtilities.invokeLater(this::refrescar);
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstantes.CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                UIConstantes.crearBordeSeccion("Directorio de clientes"),
                BorderFactory.createEmptyBorder(8, 12, 12, 12)));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UIConstantes.CARD);
        scroll.setBackground(UIConstantes.CARD);
        UIConstantes.centrarCeldasTabla(tabla);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new BorderLayout(12, 10));
        panel.setBackground(UIConstantes.CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                UIConstantes.crearBordeSeccion("Cliente"),
                BorderFactory.createEmptyBorder(10, 14, 12, 14)));

        JPanel campos = new JPanel(new GridLayout(1, 2, 14, 0));
        campos.setOpaque(false);
        campos.add(crearCampo("Nombre completo", campoNombre));
        campos.add(crearCampo("Telefono", campoTelefono));
        panel.add(campos, BorderLayout.CENTER);

        JButton btnNuevo = UIConstantes.button("Limpiar / Nuevo", UIConstantes.CARD_2);
        JButton btnGuardar = UIConstantes.button("Registrar", UIConstantes.SUCCESS);
        JButton btnActualizar = UIConstantes.button("Actualizar seleccionado", UIConstantes.PRIMARY);
        JButton btnEliminar = UIConstantes.button("Eliminar seleccionado", UIConstantes.DANGER);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearCampo(String titulo, JTextField campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel etiqueta = UIConstantes.label(titulo, UIConstantes.MUTED, Font.BOLD, 12);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        idSeleccionado = (Integer) modeloTabla.getValueAt(fila, 0);
        campoNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        campoTelefono.setText(modeloTabla.getValueAt(fila, 2).toString());
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        campoNombre.setText("");
        campoTelefono.setText("");
        tabla.clearSelection();
    }

    private void registrar() {
        if (campoNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            return;
        }
        sistema.clientes().registrar(campoNombre.getText().trim(), campoTelefono.getText().trim());
        limpiarFormulario();
        refrescar();
    }

    private void actualizar() {
        if (idSeleccionado == null) {
            mostrarError("Selecciona un cliente de la tabla primero.");
            return;
        }
        try {
            sistema.clientes().actualizar(idSeleccionado, campoNombre.getText().trim(), campoTelefono.getText().trim());
            limpiarFormulario();
            refrescar();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == null) {
            mostrarError("Selecciona un cliente de la tabla primero.");
            return;
        }
        try {
            sistema.clientes().eliminar(idSeleccionado);
            limpiarFormulario();
            refrescar();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar la acción", JOptionPane.WARNING_MESSAGE);
    }

    /** Vuelve a leer los clientes desde el sistema y repinta la tabla. */
    public void refrescar() {
        modeloTabla.setRowCount(0);
        for (Cliente c : sistema.clientes().consultar()) {
            modeloTabla.addRow(new Object[]{c.getIdCliente(), c.getNombre(), c.getTelefono()});
        }
    }

    /** Alias de compatibilidad para la interfaz unificada. */
    public void refrescarClientes() {
        refrescar();
    }
}
