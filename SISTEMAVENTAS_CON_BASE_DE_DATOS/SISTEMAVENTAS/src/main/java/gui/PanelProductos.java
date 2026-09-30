package com.tienda.gui;

import com.tienda.gestion.SistemaVentas;
import com.tienda.modelo.Producto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Pantalla de Productos: lista + formulario de alta/edición/baja (RF01-RF04).
 * No contiene reglas de negocio: valida solo el formato de la entrada y
 * delega todo en {@link com.tienda.gestion.GestorProductos}.
 */
public class PanelProductos extends JPanel {

    private final SistemaVentas sistema;

    private final ModeloTablaSoloLectura modeloTabla =
        new ModeloTablaSoloLectura(new Object[]{"ID", "Nombre", "Precio", "Stock", "Categoría"});
    private final JTable tabla = UIConstantes.styledTable(modeloTabla);

    private final JTextField campoNombre = UIConstantes.inputField();
    private final JTextField campoPrecio = UIConstantes.inputField();
    private final JTextField campoStock = UIConstantes.inputField();
    private final JTextField campoCategoria = UIConstantes.inputField();
    private Integer idSeleccionado = null;

    public PanelProductos(SistemaVentas sistema) {
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
                UIConstantes.crearBordeSeccion("Inventario de productos"),
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
                UIConstantes.crearBordeSeccion("Producto"),
                BorderFactory.createEmptyBorder(10, 14, 12, 14)));

        JPanel campos = new JPanel(new GridLayout(2, 2, 14, 8));
        campos.setOpaque(false);
        campos.add(crearCampo("Nombre", campoNombre));
        campos.add(crearCampo("Precio", campoPrecio));
        campos.add(crearCampo("Stock", campoStock));
        campos.add(crearCampo("Categoria", campoCategoria));
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
        panel.add(UIConstantes.label(titulo, UIConstantes.MUTED, Font.BOLD, 12), BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        idSeleccionado = (Integer) modeloTabla.getValueAt(fila, 0);
        campoNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        campoPrecio.setText(modeloTabla.getValueAt(fila, 2).toString().replace("$", ""));
        campoStock.setText(modeloTabla.getValueAt(fila, 3).toString());
        campoCategoria.setText(modeloTabla.getValueAt(fila, 4).toString());
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        campoNombre.setText("");
        campoPrecio.setText("");
        campoStock.setText("");
        campoCategoria.setText("");
        tabla.clearSelection();
    }

    private void registrar() {
        try {
            sistema.productos().registrar(
                campoNombre.getText().trim(),
                new BigDecimal(campoPrecio.getText().trim()),
                Integer.parseInt(campoStock.getText().trim()),
                campoCategoria.getText().trim());
            limpiarFormulario();
            refrescar();
        } catch (NumberFormatException ex) {
            mostrarError("Precio y stock deben ser numéricos.");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void actualizar() {
        if (idSeleccionado == null) {
            mostrarError("Selecciona un producto de la tabla primero.");
            return;
        }
        try {
            sistema.productos().actualizar(
                idSeleccionado,
                campoNombre.getText().trim(),
                new BigDecimal(campoPrecio.getText().trim()),
                Integer.parseInt(campoStock.getText().trim()),
                campoCategoria.getText().trim());
            limpiarFormulario();
            refrescar();
        } catch (NumberFormatException ex) {
            mostrarError("Precio y stock deben ser numéricos.");
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == null) {
            mostrarError("Selecciona un producto de la tabla primero.");
            return;
        }
        try {
            sistema.productos().eliminar(idSeleccionado, sistema.ventas().idsProductoConVentas());
            limpiarFormulario();
            refrescar();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar la acción", JOptionPane.WARNING_MESSAGE);
    }

    /** Vuelve a leer los productos desde el sistema y repinta la tabla. */
    public void refrescar() {
        modeloTabla.setRowCount(0);
        for (Producto p : sistema.productos().consultar()) {
            modeloTabla.addRow(new Object[]{
                p.getIdProducto(), p.getNombre(), "$" + p.getPrecio(), p.getStock(), p.getCategoria()
            });
        }
    }

    /** Alias de compatibilidad para la interfaz unificada. */
    public void refrescarProductos() {
        refrescar();
    }
}
