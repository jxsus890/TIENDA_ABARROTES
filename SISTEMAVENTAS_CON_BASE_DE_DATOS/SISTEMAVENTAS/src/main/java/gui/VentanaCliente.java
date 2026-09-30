package com.tienda.gui;

import com.tienda.gestion.SistemaVentas;
import com.tienda.modelo.Producto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VentanaCliente extends JFrame {

    private final SistemaVentas sistema = new SistemaVentas();
    private final Map<Integer, Integer> carrito = new LinkedHashMap<>();
    private final JPanel catalogoPanel = new JPanel(new GridLayout(0, 3, 20, 20));
    private final JPanel carritoPanel = new JPanel();
    private final JLabel totalLabel = new JLabel("Total: $ 0,00");
    private final JLabel cantidadCarrito = new JLabel("0 artículo(s)");
    private final JTextField txtBusqueda = new JTextField();
    private final JComboBox<String> comboCategoria = new JComboBox<>(new String[]{"Todas", "Limpieza", "Alimentos", "Hogar", "Tecnología"});

    public VentanaCliente() {
        super("Sistema de Ventas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1440, 900);
        setLocationRelativeTo(null);
        setContentPane(construirPantalla());
    }

    private JPanel construirPantalla() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(8, 24, 38));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        root.add(barraSuperior(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(18, 0));
        centro.setBackground(new Color(8, 24, 38));

        JPanel catalogoWrap = new JPanel(new BorderLayout());
        catalogoWrap.setOpaque(false);

        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setOpaque(false);
        filtros.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        JLabel buscaLabel = new JLabel("Busquedas relacionadas: hogar - tecnología - alimentos - ofertas");
        buscaLabel.setForeground(new Color(168, 192, 220));
        buscaLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        buscaLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JPanel filtrosRight = new JPanel(new BorderLayout(14, 0));
        filtrosRight.setOpaque(false);

        comboCategoria.setPreferredSize(new Dimension(160, 38));
        comboCategoria.setFont(new Font("SansSerif", Font.PLAIN, 20));
        comboCategoria.setBackground(new Color(20, 42, 62));
        comboCategoria.setForeground(new Color(231, 239, 247));
        comboCategoria.getEditor().getEditorComponent().setBackground(new Color(20, 42, 62));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(118, 162, 196));
        btnBuscar.setForeground(new Color(233, 239, 246));
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(95, 140, 182), 1),
            BorderFactory.createEmptyBorder(8, 24, 8, 24))
        );
        btnBuscar.addActionListener(e -> renderCatalogo());

        filtrosRight.add(comboCategoria, BorderLayout.CENTER);
        filtrosRight.add(btnBuscar, BorderLayout.EAST);

        JPanel filtroBar = new JPanel(new BorderLayout());
        filtroBar.setOpaque(false);
        filtroBar.add(buscaLabel, BorderLayout.WEST);
        filtroBar.add(filtrosRight, BorderLayout.EAST);

        JLabel tituloCatalogo = new JLabel("Productos disponibles");
        tituloCatalogo.setForeground(new Color(239, 242, 250));
        tituloCatalogo.setFont(new Font("SansSerif", Font.BOLD, 52));
        tituloCatalogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        catalogoWrap.add(filtroBar, BorderLayout.NORTH);
        catalogoWrap.add(tituloCatalogo, BorderLayout.CENTER);
        catalogoWrap.add(catalogoPanel, BorderLayout.SOUTH);

        catalogoPanel.setOpaque(false);
        catalogoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JScrollPane scrollCatalogo = new JScrollPane(catalogoPanel);
        scrollCatalogo.setBorder(BorderFactory.createEmptyBorder());
        scrollCatalogo.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollCatalogo.getViewport().setBackground(new Color(8, 24, 38));
        scrollCatalogo.setBackground(new Color(8, 24, 38));
        scrollCatalogo.getVerticalScrollBar().setUnitIncrement(16);
        scrollCatalogo.getHorizontalScrollBar().setUnitIncrement(16);
        aplicarEstiloScroll(scrollCatalogo, new Color(13, 40, 62), new Color(10, 29, 46), new Color(120, 170, 202));

        JPanel carritoWrap = new JPanel(new BorderLayout());
        carritoWrap.setPreferredSize(new Dimension(380, 0));
        carritoWrap.setBackground(new Color(8, 24, 38));

        JPanel carritoHeader = new JPanel(new BorderLayout());
        carritoHeader.setOpaque(false);
        carritoHeader.setBorder(BorderFactory.createEmptyBorder(8, 8, 18, 8));

        JLabel tituloCarrito = new JLabel("Tu carrito");
        tituloCarrito.setForeground(new Color(233, 239, 246));
        tituloCarrito.setFont(new Font("SansSerif", Font.BOLD, 30));
        carritoHeader.add(tituloCarrito, BorderLayout.WEST);

        cantidadCarrito.setForeground(new Color(120, 220, 180));
        cantidadCarrito.setFont(new Font("SansSerif", Font.BOLD, 20));
        carritoHeader.add(cantidadCarrito, BorderLayout.EAST);

        carritoPanel.setLayout(new BoxLayout(carritoPanel, BoxLayout.Y_AXIS));
        carritoPanel.setBackground(new Color(12, 34, 52));
        carritoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(109, 156, 191), 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12))
        );
        carritoPanel.setPreferredSize(new Dimension(340, 420));

        JScrollPane scrollCarrito = new JScrollPane(carritoPanel);
        scrollCarrito.setBorder(BorderFactory.createEmptyBorder());
        scrollCarrito.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollCarrito.getViewport().setBackground(new Color(12, 34, 52));
        scrollCarrito.setBackground(new Color(12, 34, 52));
        scrollCarrito.getVerticalScrollBar().setUnitIncrement(16);
        aplicarEstiloScroll(scrollCarrito, new Color(12, 34, 52), new Color(9, 25, 40), new Color(120, 170, 202));

        JPanel footerCarrito = new JPanel(new BorderLayout());
        footerCarrito.setOpaque(false);
        footerCarrito.setBorder(BorderFactory.createEmptyBorder(16, 6, 6, 6));

        totalLabel.setForeground(new Color(233, 239, 246));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        footerCarrito.add(totalLabel, BorderLayout.NORTH);

        JButton btnComprar = new JButton("Finalizar compra");
        btnComprar.setBackground(new Color(86, 191, 150));
        btnComprar.setForeground(new Color(255, 255, 255));
        btnComprar.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnComprar.setFocusPainted(false);
        btnComprar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 162, 126), 1),
            BorderFactory.createEmptyBorder(16, 12, 16, 12))
        );
        btnComprar.addActionListener(e -> finalizarCompra());
        footerCarrito.add(btnComprar, BorderLayout.SOUTH);

        carritoWrap.add(carritoHeader, BorderLayout.NORTH);
        carritoWrap.add(scrollCarrito, BorderLayout.CENTER);
        carritoWrap.add(footerCarrito, BorderLayout.SOUTH);

        catalogoWrap.add(scrollCatalogo, BorderLayout.CENTER);

        centro.add(catalogoWrap, BorderLayout.CENTER);
        centro.add(carritoWrap, BorderLayout.EAST);

        root.add(centro, BorderLayout.CENTER);

        renderCatalogo();
        renderCarrito();
        return root;
    }

    private JPanel barraSuperior() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(11, 32, 48));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(95, 140, 182), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12))
        );

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);

        JLabel icono = new JLabel("🛒");
        icono.setFont(new Font("SansSerif", Font.BOLD, 30));
        icono.setForeground(new Color(232, 239, 247));

        JLabel titulo = new JLabel("Sistema de Ventas");
        titulo.setForeground(new Color(232, 239, 247));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 38));
        brand.add(icono);
        brand.add(titulo);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        actions.setOpaque(false);

        txtBusqueda.setPreferredSize(new Dimension(290, 42));
        txtBusqueda.setFont(new Font("SansSerif", Font.PLAIN, 22));
        txtBusqueda.setForeground(new Color(230, 240, 248));
        txtBusqueda.setBackground(new Color(17, 43, 64));
        txtBusqueda.setCaretColor(new Color(230, 240, 248));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(96, 137, 170), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8))
        );
        txtBusqueda.setBackground(new Color(18, 45, 66));
        txtBusqueda.setToolTipText("Buscar productos");

        JButton buscar = new JButton("Buscar");
        buscar.setBackground(new Color(118, 162, 196));
        buscar.setForeground(new Color(233, 239, 246));
        buscar.setFont(new Font("SansSerif", Font.BOLD, 20));
        buscar.setFocusPainted(false);
        buscar.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        buscar.addActionListener(e -> renderCatalogo());

        JButton miCuenta = new JButton("Mi cuenta");
        miCuenta.setContentAreaFilled(false);
        miCuenta.setBorderPainted(false);
        miCuenta.setFocusPainted(false);
        miCuenta.setForeground(new Color(233, 239, 246));
        miCuenta.setFont(new Font("SansSerif", Font.PLAIN, 20));
        miCuenta.addActionListener(e -> JOptionPane.showMessageDialog(this, "Pantalla de cliente activa."));

        JButton misCompras = new JButton("Mis compras");
        misCompras.setContentAreaFilled(false);
        misCompras.setBorderPainted(false);
        misCompras.setFocusPainted(false);
        misCompras.setForeground(new Color(233, 239, 246));
        misCompras.setFont(new Font("SansSerif", Font.PLAIN, 20));
        misCompras.addActionListener(e -> JOptionPane.showMessageDialog(this, "Aún no tienes compras registradas."));

        actions.add(txtBusqueda);
        actions.add(buscar);
        actions.add(miCuenta);
        actions.add(misCompras);

        bar.add(brand, BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    private void aplicarEstiloScroll(JScrollPane scrollPane, Color scrollbarTrackColor, Color bgColor, Color scrollbarThumbColor) {
        scrollPane.getVerticalScrollBar().setBackground(scrollbarTrackColor);
        scrollPane.getHorizontalScrollBar().setBackground(scrollbarTrackColor);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = scrollbarThumbColor;
                this.trackColor = scrollbarTrackColor;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
        });

        scrollPane.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = scrollbarThumbColor;
                this.trackColor = scrollbarTrackColor;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
        });

        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
    }

    private void renderCatalogo() {
        catalogoPanel.removeAll();
        String texto = txtBusqueda.getText() == null ? "" : txtBusqueda.getText().trim().toLowerCase();
        String categoriaSeleccionada = (String) comboCategoria.getSelectedItem();

        List<Producto> productos = sistema.productos().consultar();
        for (Producto producto : productos) {
            boolean coincideTexto = texto.isEmpty() || producto.getNombre().toLowerCase().contains(texto);
            boolean coincideCategoria = "Todas".equals(categoriaSeleccionada) || producto.getCategoria().equalsIgnoreCase(categoriaSeleccionada);
            if (!coincideTexto || !coincideCategoria) {
                continue;
            }

            catalogoPanel.add(crearTarjetaProducto(producto));
        }

        if (catalogoPanel.getComponentCount() == 0) {
            JLabel vacio = new JLabel("No hay productos para esta búsqueda");
            vacio.setForeground(new Color(208, 224, 236));
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 24));
            vacio.setHorizontalAlignment(SwingConstants.CENTER);
            catalogoPanel.add(vacio);
        }

        catalogoPanel.revalidate();
        catalogoPanel.repaint();
    }

    private JPanel crearTarjetaProducto(Producto producto) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 8));
        card.setBackground(new Color(23, 72, 96));
        card.setBorder(BorderFactory.createLineBorder(new Color(116, 158, 190), 2));
        card.setPreferredSize(new Dimension(280, 330));

        JLabel imagen = new JLabel("◉", SwingConstants.CENTER);
        imagen.setForeground(new Color(232, 239, 247));
        imagen.setFont(new Font("SansSerif", Font.PLAIN, 58));
        imagen.setOpaque(true);
        imagen.setBackground(new Color(66, 116, 150));
        imagen.setPreferredSize(new Dimension(0, 145));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel nombre = new JLabel(producto.getNombre());
        nombre.setForeground(new Color(229, 239, 247));
        nombre.setFont(new Font("SansSerif", Font.BOLD, 25));
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel precio = new JLabel("$ " + formatearPrecio(producto.getPrecio()));
        precio.setForeground(new Color(82, 214, 167));
        precio.setFont(new Font("SansSerif", Font.BOLD, 24));
        precio.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel estrellas = new JLabel("★★★★☆");
        estrellas.setForeground(new Color(255, 192, 70));
        estrellas.setFont(new Font("SansSerif", Font.PLAIN, 18));
        estrellas.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(nombre);
        info.add(precio);
        info.add(estrellas);

        JButton agregar = new JButton("Agregar al carrito");
        agregar.setBackground(new Color(116, 164, 200));
        agregar.setForeground(new Color(232, 239, 247));
        agregar.setFont(new Font("SansSerif", Font.BOLD, 22));
        agregar.setFocusPainted(false);
        agregar.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        agregar.addActionListener(e -> {
            carrito.put(producto.getIdProducto(), carrito.getOrDefault(producto.getIdProducto(), 0) + 1);
            renderCarrito();
        });

        card.add(imagen, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(agregar, BorderLayout.SOUTH);
        return card;
    }

    private void renderCarrito() {
        carritoPanel.removeAll();

        int totalArticulos = 0;
        BigDecimal total = BigDecimal.ZERO;

        if (carrito.isEmpty()) {
            JLabel vacio = new JLabel("Tu carrito está vacio");
            vacio.setForeground(new Color(220, 231, 244));
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 24));
            vacio.setHorizontalAlignment(SwingConstants.CENTER);
            vacio.setBorder(BorderFactory.createEmptyBorder(80, 0, 80, 0));
            carritoPanel.add(vacio);
        } else {
            for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
                Producto producto = sistema.productos().buscarPorId(entry.getKey());
                int cantidad = entry.getValue();
                totalArticulos += cantidad;
                total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));

                JPanel item = new JPanel(new BorderLayout());
                item.setOpaque(false);
                item.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JLabel nombre = new JLabel(cantidad + " x " + producto.getNombre());
                nombre.setForeground(new Color(231, 239, 247));
                nombre.setFont(new Font("SansSerif", Font.PLAIN, 20));

                JLabel precio = new JLabel("$ " + formatearPrecio(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad))));
                precio.setForeground(new Color(112, 210, 168));
                precio.setFont(new Font("SansSerif", Font.BOLD, 20));

                item.add(nombre, BorderLayout.CENTER);
                item.add(precio, BorderLayout.EAST);
                carritoPanel.add(item);
            }
        }

        cantidadCarrito.setText(totalArticulos + " artículo(s)");
        totalLabel.setText("Total: $ " + formatearPrecio(total));

        carritoPanel.revalidate();
        carritoPanel.repaint();
    }

    private void finalizarCompra() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tu carrito está vacío.");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        StringBuilder texto = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            Producto producto = sistema.productos().buscarPorId(entry.getKey());
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(entry.getValue()));
            total = total.add(subtotal);
            texto.append(producto.getNombre()).append(" x").append(entry.getValue()).append(" - $ ")
                .append(formatearPrecio(subtotal)).append("\n");
        }

        String mensaje = "Compra realizada correctamente.\n\n" + texto + "\nTotal: $ " + formatearPrecio(total);
        JOptionPane.showMessageDialog(this, mensaje, "Compra registrada", JOptionPane.INFORMATION_MESSAGE);
        carrito.clear();
        renderCarrito();
    }

    private String formatearPrecio(BigDecimal valor) {
        return String.format("%,.2f", valor.setScale(2, java.math.RoundingMode.HALF_UP));
    }
}
