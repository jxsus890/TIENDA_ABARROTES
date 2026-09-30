package com.tienda.gui;

import com.tienda.app.VentanaAcceso;
import com.tienda.gestion.ItemPedido;
import com.tienda.gestion.SistemaVentas;
import com.tienda.modelo.Cliente;
import com.tienda.modelo.Producto;
import com.tienda.modelo.Venta;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Catalogo de compra para el acceso de cliente. */
public class PanelCatalogoCliente extends JPanel {
    private final SistemaVentas sistema;
    private final Integer idClienteActual;
    private final JPanel productosGrid = new JPanel(new GridLayout(0, 3, 16, 16));
    private final JPanel carritoItems = new JPanel();
    private final List<ItemPedido> carrito = new ArrayList<>();
    private final JTextField buscador = UIConstantes.inputField();
    private final JComboBox<String> categoria = new JComboBox<>();
    private final JLabel total = UIConstantes.label("Total: $0 COP", UIConstantes.TEXT, Font.BOLD, 16);
    private boolean actualizandoCategorias;

    public PanelCatalogoCliente(SistemaVentas sistema) {
        this(sistema, null);
    }

    public PanelCatalogoCliente(SistemaVentas sistema, Integer idClienteActual) {
        this.sistema = sistema;
        this.idClienteActual = idClienteActual;
        setLayout(new BorderLayout(16, 16));
        setBackground(UIConstantes.BG);
        setBorder(BorderFactory.createEmptyBorder(10, 26, 22, 28));
        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel barra = new JPanel(new BorderLayout(16, 0));
        barra.setOpaque(false);
        JLabel titulo = UIConstantes.label("Encuentra lo que necesitas", UIConstantes.TEXT, Font.BOLD, 20);
        barra.add(titulo, BorderLayout.WEST);
        buscador.setToolTipText("Buscar por nombre o categoria");
        buscador.setPreferredSize(new Dimension(360, 40));
        buscador.getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() { SwingUtilities.invokeLater(PanelCatalogoCliente.this::refrescarProductos); }
            public void insertUpdate(DocumentEvent e) { actualizar(); }
            public void removeUpdate(DocumentEvent e) { actualizar(); }
            public void changedUpdate(DocumentEvent e) { actualizar(); }
        });
        barra.add(buscador, BorderLayout.CENTER);
        JButton cuenta = UIConstantes.button("Mi cuenta", UIConstantes.PRIMARY);
        cuenta.addActionListener(e -> mostrarCuenta());
        barra.add(cuenta, BorderLayout.EAST);
        add(barra, BorderLayout.NORTH);

        productosGrid.setOpaque(false);
        JScrollPane productosScroll = new JScrollPane(productosGrid);
        productosScroll.setBorder(BorderFactory.createEmptyBorder());
        productosScroll.getViewport().setBackground(UIConstantes.BG);
        add(productosScroll, BorderLayout.CENTER);
        add(crearPanelDerecho(), BorderLayout.EAST);
    }

    private JPanel crearPanelDerecho() {
        JPanel derecho = new JPanel(new BorderLayout(0, 14));
        derecho.setPreferredSize(new Dimension(250, 0));
        derecho.setBackground(UIConstantes.SIDEBAR);
        derecho.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel filtros = new JPanel(new BorderLayout(0, 8));
        filtros.setOpaque(false);
        filtros.add(UIConstantes.label("FILTRAR CATALOGO", UIConstantes.MUTED, Font.BOLD, 11), BorderLayout.NORTH);
        categoria.addItem("Todas las categorias");
        categoria.setForeground(Color.WHITE);
        UIConstantes.styleCombo(categoria);
        categoria.addActionListener(e -> {
            if (!actualizandoCategorias) refrescarProductos();
        });
        filtros.add(categoria, BorderLayout.CENTER);
        derecho.add(filtros, BorderLayout.NORTH);

        JPanel carritoPanel = new JPanel(new BorderLayout(0, 12));
        carritoPanel.setOpaque(false);
        carritoPanel.setBorder(UIConstantes.crearBordeSeccion("Mi carrito"));
        carritoItems.setOpaque(false);
        carritoItems.setLayout(new javax.swing.BoxLayout(carritoItems, javax.swing.BoxLayout.Y_AXIS));
        JScrollPane carritoScroll = new JScrollPane(carritoItems);
        carritoScroll.setBorder(BorderFactory.createEmptyBorder());
        carritoScroll.getViewport().setOpaque(false);
        carritoScroll.setOpaque(false);
        carritoPanel.add(carritoScroll, BorderLayout.CENTER);

        JButton confirmar = UIConstantes.button("Confirmar compra", UIConstantes.SUCCESS);
        confirmar.addActionListener(e -> confirmarCompra());
        JPanel resumen = new JPanel(new BorderLayout(0, 10));
        resumen.setOpaque(false);
        resumen.add(total, BorderLayout.NORTH);
        resumen.add(confirmar, BorderLayout.SOUTH);
        carritoPanel.add(resumen, BorderLayout.SOUTH);
        derecho.add(carritoPanel, BorderLayout.CENTER);
        return derecho;
    }

    public void refrescarProductos() {
        String seleccion = (String) categoria.getSelectedItem();
        String consulta = buscador.getText().trim().toLowerCase();
        productosGrid.removeAll();
        Map<String, Boolean> categorias = new LinkedHashMap<>();
        for (Producto producto : sistema.productos().consultar()) {
            categorias.put(producto.getCategoria(), Boolean.TRUE);
            boolean coincideTexto = consulta.isEmpty()
                    || producto.getNombre().toLowerCase().contains(consulta)
                    || producto.getCategoria().toLowerCase().contains(consulta);
            boolean coincideCategoria = seleccion == null || seleccion.equals("Todas las categorias")
                    || producto.getCategoria().equals(seleccion);
            if (coincideTexto && coincideCategoria && producto.getStock() > 0) {
                productosGrid.add(crearTarjeta(producto));
            }
        }
        sincronizarCategorias(categorias.keySet());
        productosGrid.revalidate();
        productosGrid.repaint();
    }

    private void sincronizarCategorias(Iterable<String> categorias) {
        String actual = (String) categoria.getSelectedItem();
        actualizandoCategorias = true;
        try {
            categoria.removeAllItems();
            categoria.addItem("Todas las categorias");
            for (String nombre : categorias) categoria.addItem(nombre);
            if (actual != null) categoria.setSelectedItem(actual);
            if (categoria.getSelectedIndex() < 0) categoria.setSelectedIndex(0);
        } finally {
            actualizandoCategorias = false;
        }
    }

    private JPanel crearTarjeta(Producto producto) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setBackground(UIConstantes.CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstantes.LINE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tarjeta.setPreferredSize(new Dimension(210, 285));

        JLabel imagen = UIConstantes.label("▣ " + producto.getCategoria().toUpperCase(), UIConstantes.TEXT, Font.BOLD, 18);
        imagen.setHorizontalAlignment(SwingConstants.CENTER);
        imagen.setVerticalAlignment(SwingConstants.CENTER);
        imagen.setOpaque(true);
        imagen.setBackground(UIConstantes.CARD_2);
        imagen.setPreferredSize(new Dimension(0, 105));
        tarjeta.add(imagen, BorderLayout.NORTH);

        JPanel datos = new JPanel();
        datos.setOpaque(false);
        datos.setLayout(new javax.swing.BoxLayout(datos, javax.swing.BoxLayout.Y_AXIS));
        datos.add(UIConstantes.label(producto.getNombre(), UIConstantes.TEXT, Font.BOLD, 14));
        datos.add(javax.swing.Box.createVerticalStrut(4));
        datos.add(UIConstantes.label(producto.getCategoria(), UIConstantes.MUTED, Font.PLAIN, 12));
        datos.add(javax.swing.Box.createVerticalStrut(8));
        datos.add(UIConstantes.label("$" + producto.getPrecio() + " COP", UIConstantes.TEXT, Font.BOLD, 19));
        datos.add(UIConstantes.label("Disponible: " + producto.getStock(), UIConstantes.SUCCESS, Font.PLAIN, 12));
        tarjeta.add(datos, BorderLayout.CENTER);

        JButton agregar = UIConstantes.button("Agregar al carrito", UIConstantes.PRIMARY);
        agregar.setMargin(new Insets(7, 5, 7, 5));
        agregar.addActionListener(e -> agregarAlCarrito(producto));
        tarjeta.add(agregar, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void agregarAlCarrito(Producto producto) {
        for (int indice = 0; indice < carrito.size(); indice++) {
            ItemPedido item = carrito.get(indice);
            if (item.getIdProducto() == producto.getIdProducto()) {
                carrito.set(indice, new ItemPedido(item.getIdProducto(), item.getCantidad() + 1));
                refrescarCarrito();
                return;
            }
        }
        carrito.add(new ItemPedido(producto.getIdProducto(), 1));
        refrescarCarrito();
    }

    private void refrescarCarrito() {
        carritoItems.removeAll();
        BigDecimal totalCompra = BigDecimal.ZERO;
        for (ItemPedido item : carrito) {
            Producto producto = buscarProducto(item.getIdProducto());
            if (producto == null) continue;
            totalCompra = totalCompra.add(producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
            JPanel fila = new JPanel(new BorderLayout(4, 2));
            fila.setOpaque(false);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            fila.add(UIConstantes.label(producto.getNombre(), UIConstantes.TEXT, Font.PLAIN, 12), BorderLayout.CENTER);
            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            acciones.setOpaque(false);
            acciones.add(UIConstantes.label("x" + item.getCantidad(), UIConstantes.MUTED, Font.BOLD, 12));
            JButton eliminar = UIConstantes.button("X", UIConstantes.DANGER);
            eliminar.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
            eliminar.addActionListener(e -> eliminarDelCarrito(item.getIdProducto()));
            acciones.add(eliminar);
            fila.add(acciones, BorderLayout.EAST);
            carritoItems.add(fila);
            carritoItems.add(javax.swing.Box.createVerticalStrut(6));
        }
        total.setText("Total: $" + totalCompra + " COP");
        carritoItems.revalidate();
        carritoItems.repaint();
    }

    private void eliminarDelCarrito(int idProducto) {
        carrito.removeIf(item -> item.getIdProducto() == idProducto);
        refrescarCarrito();
    }

    private Producto buscarProducto(int id) {
        for (Producto producto : sistema.productos().consultar()) {
            if (producto.getIdProducto() == id) return producto;
        }
        return null;
    }

    private void confirmarCompra() {
        if (carrito.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Agrega productos al carrito antes de confirmar.", "Carrito vacio", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<Cliente> clientes = sistema.clientes().consultar();
        if (clientes.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No hay un cliente registrado para asociar la compra.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int idCliente = idClienteActual == null ? clientes.get(0).getIdCliente() : idClienteActual;
            Venta venta = sistema.ventas().registrarVenta(idCliente, new ArrayList<>(carrito));
            mostrarFactura(venta);
            carrito.clear();
            refrescarCarrito();
            refrescarProductos();
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo completar la compra", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarCuenta() {
        if (idClienteActual == null) return;
        Cliente cliente = sistema.clientes().buscarPorId(idClienteActual);
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(ventanaPadre, "Mi cuenta", JDialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(UIConstantes.BG);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstantes.LINE),
                BorderFactory.createEmptyBorder(18, 22, 20, 22)));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(UIConstantes.label("Mi cuenta", UIConstantes.TEXT, Font.BOLD, 20), BorderLayout.WEST);
        JButton cerrar = UIConstantes.button("X", UIConstantes.SIDEBAR);
        cerrar.setBorder(BorderFactory.createEmptyBorder(4, 9, 4, 9));
        cerrar.addActionListener(e -> dialogo.dispose());
        encabezado.add(cerrar, BorderLayout.EAST);
        contenido.add(encabezado, BorderLayout.NORTH);

        JPanel datos = new JPanel(new GridLayout(3, 1, 0, 10));
        datos.setOpaque(false);
        datos.add(crearDatoCuenta("Nombre completo", cliente.getNombre()));
        datos.add(crearDatoCuenta("Telefono", cliente.getTelefono()));
        datos.add(crearDatoCuenta("Cedula de ciudadania", cliente.getCedula()));
        contenido.add(datos, BorderLayout.CENTER);

        JButton eliminar = UIConstantes.button("Eliminar cuenta", UIConstantes.DANGER);
        eliminar.addActionListener(e -> eliminarCuenta(dialogo));
        JButton salir = UIConstantes.button("Salir", UIConstantes.SIDEBAR);
        salir.addActionListener(e -> cerrarSesion(dialogo));
        JButton aceptar = UIConstantes.button("Aceptar", UIConstantes.PRIMARY);
        aceptar.addActionListener(e -> dialogo.dispose());
        JPanel acciones = new JPanel(new BorderLayout(10, 0));
        acciones.setOpaque(false);
        acciones.add(eliminar, BorderLayout.WEST);
        JPanel accionesDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        accionesDerecha.setOpaque(false);
        accionesDerecha.add(salir);
        accionesDerecha.add(aceptar);
        acciones.add(accionesDerecha, BorderLayout.EAST);
        contenido.add(acciones, BorderLayout.SOUTH);

        dialogo.setContentPane(contenido);
        dialogo.setSize(430, 300);
        dialogo.setLocationRelativeTo(ventanaPadre);
        dialogo.setVisible(true);
    }

    private void cerrarSesion(JDialog dialogo) {
        Window ventanaPrincipal = SwingUtilities.getWindowAncestor(this);
        dialogo.dispose();
        if (ventanaPrincipal != null) ventanaPrincipal.dispose();
        new VentanaAcceso().setVisible(true);
    }

    private void eliminarCuenta(JDialog dialogo) {
        mostrarConfirmacionEliminar(dialogo, () -> {
            sistema.clientes().eliminar(idClienteActual);
            cerrarSesion(dialogo);
        });
    }

    private void mostrarConfirmacionEliminar(JDialog ventanaPadre, Runnable confirmar) {
        JDialog confirmacion = new JDialog(ventanaPadre, "Eliminar cuenta", JDialog.ModalityType.APPLICATION_MODAL);
        confirmacion.setUndecorated(true);

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(UIConstantes.BG);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstantes.LINE),
                BorderFactory.createEmptyBorder(16, 22, 20, 22)));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(UIConstantes.label("Eliminar cuenta", UIConstantes.TEXT, Font.BOLD, 18), BorderLayout.WEST);
        JButton cerrar = UIConstantes.button("X", UIConstantes.SIDEBAR);
        cerrar.setBorder(BorderFactory.createEmptyBorder(4, 9, 4, 9));
        cerrar.addActionListener(e -> confirmacion.dispose());
        encabezado.add(cerrar, BorderLayout.EAST);
        contenido.add(encabezado, BorderLayout.NORTH);

        JPanel mensaje = new JPanel();
        mensaje.setOpaque(false);
        mensaje.setLayout(new javax.swing.BoxLayout(mensaje, javax.swing.BoxLayout.Y_AXIS));
        JLabel advertencia = UIConstantes.label("!", UIConstantes.DANGER, Font.BOLD, 34);
        advertencia.setAlignmentX(CENTER_ALIGNMENT);
        mensaje.add(advertencia);
        mensaje.add(javax.swing.Box.createVerticalStrut(6));
        JLabel texto = UIConstantes.label("¿Desea eliminar definitivamente su cuenta?", UIConstantes.TEXT, Font.PLAIN, 14);
        texto.setHorizontalAlignment(SwingConstants.CENTER);
        texto.setAlignmentX(CENTER_ALIGNMENT);
        mensaje.add(texto);
        contenido.add(mensaje, BorderLayout.CENTER);

        JButton no = UIConstantes.button("No", UIConstantes.SIDEBAR);
        no.addActionListener(e -> confirmacion.dispose());
        JButton si = UIConstantes.button("Si", UIConstantes.DANGER);
        si.addActionListener(e -> {
            confirmacion.dispose();
            confirmar.run();
        });
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(no);
        acciones.add(si);
        contenido.add(acciones, BorderLayout.SOUTH);

        confirmacion.setContentPane(contenido);
        confirmacion.setSize(440, 220);
        confirmacion.setLocationRelativeTo(ventanaPadre);
        confirmacion.setVisible(true);
    }

    private JPanel crearDatoCuenta(String titulo, String valor) {
        JPanel dato = new JPanel(new BorderLayout(0, 3));
        dato.setOpaque(false);
        dato.add(UIConstantes.label(titulo, UIConstantes.MUTED, Font.BOLD, 12), BorderLayout.NORTH);
        dato.add(UIConstantes.label(valor, UIConstantes.TEXT, Font.PLAIN, 15), BorderLayout.CENTER);
        return dato;
    }

    private void mostrarFactura(Venta venta) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(ventanaPadre, "Factura de compra", JDialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(UIConstantes.BG);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstantes.LINE, 1),
                BorderFactory.createEmptyBorder(16, 22, 20, 22)));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(UIConstantes.label("Factura de compra", UIConstantes.TEXT, Font.BOLD, 18), BorderLayout.WEST);
        JButton cerrar = UIConstantes.button("X", UIConstantes.SIDEBAR);
        cerrar.setBorder(BorderFactory.createEmptyBorder(4, 9, 4, 9));
        cerrar.addActionListener(e -> dialogo.dispose());
        encabezado.add(cerrar, BorderLayout.EAST);
        contenido.add(encabezado, BorderLayout.NORTH);

        JPanel factura = new JPanel(new BorderLayout(0, 10));
        factura.setOpaque(false);
        factura.add(UIConstantes.label("✓  Compra confirmada", UIConstantes.SUCCESS, Font.BOLD, 16), BorderLayout.NORTH);

        JPanel datos = new JPanel(new GridLayout(2, 2, 8, 4));
        datos.setOpaque(false);
        datos.add(UIConstantes.label("Factura #" + venta.getIdVenta(), UIConstantes.TEXT, Font.BOLD, 13));
        datos.add(UIConstantes.label("Fecha: " + venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), UIConstantes.MUTED, Font.PLAIN, 12));
        datos.add(UIConstantes.label("Cliente: " + venta.getCliente().getNombre(), UIConstantes.TEXT, Font.PLAIN, 12));
        datos.add(UIConstantes.label("ID: " + venta.getCliente().getIdCliente(), UIConstantes.MUTED, Font.PLAIN, 12));
        factura.add(datos, BorderLayout.CENTER);

        JTextArea detalle = new JTextArea();
        detalle.setEditable(false);
        detalle.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detalle.setForeground(UIConstantes.TEXT);
        detalle.setBackground(UIConstantes.INPUT);
        detalle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstantes.LINE),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        StringBuilder lineas = new StringBuilder("PRODUCTO                         CANT.       SUBTOTAL\n");
        lineas.append("-------------------------------------------------------\n");
        venta.getDetalles().forEach(item -> lineas.append(String.format("%-32s %3d   $%s COP%n",
            item.getProducto().getNombre(), item.getCantidad(), item.getSubtotal())));
        lineas.append("-------------------------------------------------------\n");
        lineas.append(String.format("TOTAL: $%s COP", venta.getTotal()));
        detalle.setText(lineas.toString());
        factura.add(detalle, BorderLayout.SOUTH);
        contenido.add(factura, BorderLayout.CENTER);

        JButton aceptar = UIConstantes.button("Aceptar", UIConstantes.PRIMARY);
        aceptar.addActionListener(e -> dialogo.dispose());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(aceptar);
        contenido.add(acciones, BorderLayout.SOUTH);

        dialogo.setContentPane(contenido);
        dialogo.setSize(620, 390);
        dialogo.setLocationRelativeTo(ventanaPadre);
        dialogo.setVisible(true);
    }
}
