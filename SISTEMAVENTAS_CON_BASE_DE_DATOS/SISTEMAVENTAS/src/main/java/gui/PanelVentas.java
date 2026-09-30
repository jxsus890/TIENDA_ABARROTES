package com.tienda.gui;

import com.tienda.gestion.ItemPedido;
import com.tienda.gestion.SistemaVentas;
import com.tienda.excepciones.EntidadNoEncontradaException;
import com.tienda.modelo.Cliente;
import com.tienda.modelo.DetalleVenta;
import com.tienda.modelo.Producto;
import com.tienda.modelo.Venta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;

public class PanelVentas extends JPanel {
    private final SistemaVentas sistema;
    private final Runnable alRefrescarProductos;
    private JComboBox<Cliente> saleClient;
    private JComboBox<Producto> saleProduct;
    private JSpinner saleQuantity;
    private DefaultTableModel cartModel, salesModel;
    private JTable cartTable, salesTable;
    private final List<ItemPedido> cart = new ArrayList<>();
    private JTextArea saleDetail;
    private JLabel cartTotal;

    public PanelVentas(SistemaVentas sistema) {
        this(sistema, null);
    }

    /**
     * Constructor compatible con la ventana principal de la versión anterior:
     * permite refrescar el panel de productos después de registrar una venta.
     */
    public PanelVentas(SistemaVentas sistema, Runnable alRefrescarProductos) {
        this.sistema = sistema;
        this.alRefrescarProductos = alRefrescarProductos;
        this.setLayout(new BorderLayout(0, 15));
        this.setBackground(UIConstantes.BG);
        this.setBorder(new EmptyBorder(10, 26, 22, 28));
        construirUI();
    }

    private void construirUI() {
        JPanel top = new JPanel(new GridLayout(1, 2, 15, 0));
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 270));

        // --- FORMULARIO NUEVA VENTA ---
        JPanel newSale = new JPanel(new BorderLayout(10, 15));
        newSale.setBackground(UIConstantes.CARD);
        newSale.setBorder(BorderFactory.createCompoundBorder(UIConstantes.crearBordeSeccion("🧾 Nueva Venta"), new EmptyBorder(10, 15, 15, 15)));

        JPanel saleInputs = new JPanel(new GridLayout(3, 1, 0, 15));
        saleInputs.setOpaque(false);

        saleClient = new JComboBox<>();
        saleProduct = new JComboBox<>();
        saleQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        
        UIConstantes.styleCombo(saleClient); 
        UIConstantes.styleCombo(saleProduct);
        UIConstantes.styleSpinner(saleQuantity); // Aquí aplicamos el spinner oscuro

        // RENDERER CLIENTE: Muestra ÚNICAMENTE el nombre
        saleClient.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(5, 8, 5, 8));
                setBackground(isSelected ? UIConstantes.PRIMARY : UIConstantes.INPUT);
                setForeground(Color.WHITE);
                if (value instanceof Cliente cliente) setText(cliente.getNombre()); // SOLO MUESTRA NOMBRE
                return this;
            }
        });

        // RENDERER PRODUCTO: Muestra nombre y precio
        saleProduct.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(5, 8, 5, 8));
                setBackground(isSelected ? UIConstantes.PRIMARY : UIConstantes.INPUT);
                setForeground(Color.WHITE);
                if (value instanceof Producto p) {
                    setText(p.getNombre() + " - $" + p.getPrecio() + " COP");
                }
                return this;
            }
        });

        saleInputs.add(crearCampo("👤 Cliente:", saleClient));
        saleInputs.add(crearCampo("📦 Producto:", saleProduct));
        
        saleInputs.add(crearCampo("🔢 Cantidad:", saleQuantity));

        JButton add = UIConstantes.button("➕ Agregar al carrito", UIConstantes.PRIMARY);
        add.addActionListener(e -> agregarAlCarrito());
        
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setOpaque(false); btnWrap.add(add);

        newSale.add(saleInputs, BorderLayout.CENTER);
        newSale.add(btnWrap, BorderLayout.SOUTH);

        // --- CARRITO ---
        JPanel cartCard = new JPanel(new BorderLayout(0, 10));
        cartCard.setBackground(UIConstantes.CARD);
        cartCard.setBorder(BorderFactory.createCompoundBorder(UIConstantes.crearBordeSeccion("🛒 Carrito Actual"), new EmptyBorder(10, 15, 15, 15)));

        cartModel = new DefaultTableModel(new String[]{"Producto", "Cant", "Subtotal"}, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = UIConstantes.styledTable(cartModel); cartTable.setRowHeight(32);
        JScrollPane scrollCart = new JScrollPane(cartTable); scrollCart.getViewport().setBackground(UIConstantes.CARD);
        scrollCart.setBorder(BorderFactory.createEmptyBorder());
        cartCard.add(scrollCart, BorderLayout.CENTER);

        cartTotal = UIConstantes.label("Total: $0.00 COP", UIConstantes.TEXT, Font.BOLD, 18);
        JButton btnQuitar = UIConstantes.button("🗑 Quitar producto", UIConstantes.DANGER);
        btnQuitar.addActionListener(e -> quitarDelCarrito());
        JButton btnRegistrar = UIConstantes.button("💾 Confirmar Venta", UIConstantes.SUCCESS);
        btnRegistrar.addActionListener(e -> registrarVenta());
        
        JPanel cartButtons = new JPanel(new GridLayout(1, 2, 10, 0)); cartButtons.setOpaque(false);
        cartButtons.add(btnQuitar);
        cartButtons.add(btnRegistrar);
        JPanel cartActions = new JPanel();
        cartActions.setOpaque(false);
        cartActions.setLayout(new BoxLayout(cartActions, BoxLayout.Y_AXIS));
        cartTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartActions.add(cartTotal);
        cartActions.add(Box.createVerticalStrut(8));
        cartActions.add(cartButtons);
        cartCard.add(cartActions, BorderLayout.SOUTH);

        top.add(newSale); top.add(cartCard);

        // --- HISTORIAL ---
        JPanel history = new JPanel(new BorderLayout());
        history.setBackground(UIConstantes.CARD);
        history.setBorder(BorderFactory.createCompoundBorder(UIConstantes.crearBordeSeccion("📂 Historial de Ventas"), new EmptyBorder(10, 15, 15, 15)));

        salesModel = new DefaultTableModel(new String[]{"ID", "Fecha", "Cliente", "Total (COP)"}, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        salesTable = UIConstantes.styledTable(salesModel); salesTable.setRowHeight(35);
        salesTable.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) mostrarDetalleVenta(); });

        saleDetail = new JTextArea(); saleDetail.setEditable(false); saleDetail.setFont(new Font("Monospaced", Font.PLAIN, 13));
        saleDetail.setForeground(UIConstantes.TEXT); saleDetail.setBackground(UIConstantes.INPUT); 
        saleDetail.setBorder(BorderFactory.createCompoundBorder(UIConstantes.crearBordeSeccion("🔍 Detalles"), new EmptyBorder(10,10,10,10)));

        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0)); center.setOpaque(false);
        JScrollPane scrollSales = new JScrollPane(salesTable); scrollSales.getViewport().setBackground(UIConstantes.CARD);
        scrollSales.setBorder(BorderFactory.createEmptyBorder());
        center.add(scrollSales); center.add(new JScrollPane(saleDetail));
        history.add(center, BorderLayout.CENTER);

        this.add(top, BorderLayout.NORTH); this.add(history, BorderLayout.CENTER);
    }

    private JPanel crearCampo(String titulo, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 3)); p.setOpaque(false);
        p.add(UIConstantes.label(titulo, UIConstantes.MUTED, Font.BOLD, 12), BorderLayout.NORTH); 
        p.add(comp, BorderLayout.CENTER); return p;
    }

    private void agregarAlCarrito() {
        try {
            saleQuantity.commitEdit();
        } catch (ParseException ignored) {
            saleQuantity.setValue(saleQuantity.getValue());
        }
        Producto product = (Producto) saleProduct.getSelectedItem(); 
        if (product == null) { JOptionPane.showMessageDialog(this, "Debe seleccionar un producto.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        
        int quantity = (Integer) saleQuantity.getValue();
        if (quantity <= 0) { JOptionPane.showMessageDialog(this, "Cantidad mayor a 0.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }

        cart.add(new ItemPedido(product.getIdProducto(), quantity));
        cartModel.addRow(new Object[]{product.getNombre(), quantity, "$" + product.getPrecio().multiply(BigDecimal.valueOf(quantity))});
        actualizarTotalCarrito();
        UIConstantes.centrarCeldasTabla(cartTable);
    }

    private void quitarDelCarrito() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del carrito para quitarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cart.remove(selectedRow);
        cartModel.removeRow(selectedRow);
        actualizarTotalCarrito();
    }

    private void registrarVenta() {
        Cliente cliente = (Cliente) saleClient.getSelectedItem(); 
        if (cliente == null || cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente y agregue productos.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            Venta sale = sistema.ventas().registrarVenta(cliente.getIdCliente(), new ArrayList<>(cart));
            JOptionPane.showMessageDialog(this, "✅ Venta registrada con éxito!\nTotal: $" + sale.getTotal() + " COP");
            cart.clear(); cartModel.setRowCount(0); actualizarTotalCarrito(); refrescarVentas();
            if (alRefrescarProductos != null) alRefrescarProductos.run();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDetalleVenta() {
        int row = salesTable.getSelectedRow(); if (row < 0) { saleDetail.setText(""); return; }
        try {
            int id = Integer.parseInt(salesModel.getValueAt(row, 0).toString());
            Venta sale = sistema.ventas().consultarDetalle(id);
            StringBuilder sb = new StringBuilder();
            sb.append("VENTA #").append(sale.getIdVenta()).append("\n==========================\n");
            for (DetalleVenta d : sale.getDetalles()) {
                sb.append(String.format(" %-15s x%d\n  Subtotal: $%s\n", d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal()));
            }
            sb.append("==========================\nTOTAL: $").append(sale.getTotal()).append(" COP");
            saleDetail.setText(sb.toString());
        } catch (NumberFormatException | EntidadNoEncontradaException e) {
            saleDetail.setText("Error cargando detalles.");
        }
    }

    private void actualizarTotalCarrito() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String val = cartModel.getValueAt(i, 2).toString().replace("$", "");
            total = total.add(new BigDecimal(val));
        }
        cartTotal.setText("Total: $" + total.setScale(2) + " COP");
    }

    /** Alias de compatibilidad para las ventanas que usan el nombre refrescar(). */
    public void refrescar() {
        refrescarDatos();
    }

    public void refrescarDatos() {
        saleClient.removeAllItems(); for (Cliente c : sistema.clientes().consultar()) saleClient.addItem(c);
        saleProduct.removeAllItems(); for (Producto p : sistema.productos().consultar()) if (p.getStock() > 0) saleProduct.addItem(p);
        refrescarVentas();
    }

    private void refrescarVentas() {
        salesModel.setRowCount(0);
        for (Venta v : sistema.ventas().consultarTodas()) {
            salesModel.addRow(new Object[]{v.getIdVenta(), v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")), v.getCliente().getNombre(), "$" + v.getTotal()});
        }
        UIConstantes.centrarCeldasTabla(salesTable);
    }
}