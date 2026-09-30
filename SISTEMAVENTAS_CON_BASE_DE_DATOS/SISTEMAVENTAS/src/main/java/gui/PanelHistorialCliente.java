package com.tienda.gui;

import com.tienda.gestion.SistemaVentas;
import com.tienda.modelo.DetalleVenta;
import com.tienda.modelo.Venta;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;

/** Consulta de todas las ventas realizadas desde la cuenta cliente. */
public class PanelHistorialCliente extends JPanel {
    private final SistemaVentas sistema;
    private final DefaultTableModel ventasModel;
    private final JTable ventasTabla;
    private final JTextArea detalle;
    private Integer idClienteActual;

    public PanelHistorialCliente(SistemaVentas sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout(0, 16));
        setBackground(UIConstantes.BG);
        setBorder(BorderFactory.createEmptyBorder(10, 26, 22, 28));

        JLabel titulo = UIConstantes.label("Todas las ventas realizadas", UIConstantes.TEXT, Font.BOLD, 20);
        add(titulo, BorderLayout.NORTH);

        ventasModel = new DefaultTableModel(new String[]{"Factura", "Fecha", "Cliente", "Total (COP)"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        ventasTabla = UIConstantes.styledTable(ventasModel);
        ventasTabla.setRowHeight(38);
        ventasTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ventasTabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalle();
        });

        JScrollPane tablaScroll = new JScrollPane(ventasTabla);
        tablaScroll.setBorder(BorderFactory.createEmptyBorder());
        tablaScroll.getViewport().setBackground(UIConstantes.CARD);

        JPanel tablaPanel = new JPanel(new BorderLayout());
        tablaPanel.setBackground(UIConstantes.CARD);
        tablaPanel.setBorder(BorderFactory.createCompoundBorder(
                UIConstantes.crearBordeSeccion("Historial de ventas"),
                BorderFactory.createEmptyBorder(8, 12, 12, 12)));
        tablaPanel.add(tablaScroll, BorderLayout.CENTER);

        detalle = new JTextArea();
        detalle.setEditable(false);
        detalle.setLineWrap(false);
        detalle.setFont(new Font("Monospaced", Font.PLAIN, 13));
        detalle.setForeground(UIConstantes.TEXT);
        detalle.setBackground(UIConstantes.INPUT);
        detalle.setBorder(BorderFactory.createCompoundBorder(
                UIConstantes.crearBordeSeccion("Detalle de factura"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel contenido = new JPanel(new GridLayout(1, 2, 16, 0));
        contenido.setOpaque(false);
        contenido.add(tablaPanel);
        contenido.add(new JScrollPane(detalle));
        add(contenido, BorderLayout.CENTER);
    }

    public void setIdClienteActual(Integer idClienteActual) {
        this.idClienteActual = idClienteActual;
    }

    public void refrescarVentas() {
        ventasModel.setRowCount(0);
        java.util.List<Venta> ventas = idClienteActual == null
                ? sistema.ventas().consultarTodas()
                : sistema.ventas().consultarPorCliente(idClienteActual);
        for (Venta venta : ventas) {
            ventasModel.addRow(new Object[]{
                    "#" + venta.getIdVenta(),
                    venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    venta.getCliente().getNombre(),
                    "$" + venta.getTotal()
            });
        }
        UIConstantes.centrarCeldasTabla(ventasTabla);
        if (ventasModel.getRowCount() == 0) detalle.setText("Aun no hay ventas realizadas.");
    }

    private void mostrarDetalle() {
        int fila = ventasTabla.getSelectedRow();
        if (fila < 0) {
            detalle.setText("");
            return;
        }
        java.util.List<Venta> ventas = idClienteActual == null
                ? sistema.ventas().consultarTodas()
                : sistema.ventas().consultarPorCliente(idClienteActual);
        if (fila >= ventas.size()) return;
        Venta venta = ventas.get(fila);
        StringBuilder texto = new StringBuilder();
        texto.append("FACTURA #").append(venta.getIdVenta()).append("\n");
        texto.append("Fecha: ").append(venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        texto.append("Cliente: ").append(venta.getCliente().getNombre()).append("\n");
        texto.append("========================================\n");
        for (DetalleVenta item : venta.getDetalles()) {
            texto.append(String.format("%-22s x%-3d $%s\n",
                    item.getProducto().getNombre(), item.getCantidad(), item.getSubtotal()));
        }
        texto.append("========================================\n");
        texto.append("TOTAL: $").append(venta.getTotal()).append(" COP");
        detalle.setText(texto.toString());
    }
}
