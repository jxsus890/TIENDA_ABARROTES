package com.tienda.app;

import com.tienda.gestion.SistemaVentas;
import com.tienda.gui.UIConstantes;
import com.tienda.gui.PanelCatalogoCliente;
import com.tienda.gui.PanelClientes;
import com.tienda.gui.PanelHistorialCliente;
import com.tienda.gui.PanelProductos;
import com.tienda.gui.PanelVentas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainDiseno extends JFrame {
    private final boolean administrador;
    private final Integer idClienteActual;
    private final String nombreUsuario;
    private final String cedulaUsuario;
    private final String telefonoUsuario;
    private final SistemaVentas sistema = new SistemaVentas();
    private final CardLayout pagesLayout = new CardLayout();
    private final JPanel pages = new JPanel(pagesLayout);
    
    private PanelProductos panelProductos;
    private PanelClientes panelClientes;
    private PanelVentas panelVentas;
    private PanelCatalogoCliente panelCatalogoCliente;
    private PanelHistorialCliente panelHistorialCliente;
    
    private final List<JButton> navigationButtons = new ArrayList<>();
    private JLabel pageTitle;
    private JLabel pageSubtitle;

    public MainDiseno() {
        this(true);
    }

    public MainDiseno(boolean administrador) {
        this(administrador, null, null, null);
    }

    public MainDiseno(boolean administrador, String nombre, String cedula, String telefono) {
        this.administrador = administrador;
        this.nombreUsuario = nombre;
        this.cedulaUsuario = cedula;
        this.telefonoUsuario = telefono;
        if (!administrador && nombre != null && cedula != null) {
            com.tienda.modelo.Cliente clienteExistente = sistema.clientes().buscarPorCedula(cedula);
            idClienteActual = clienteExistente != null ? clienteExistente.getIdCliente() : null;
        } else {
            idClienteActual = null;
        }
        setTitle("Sistema de Ventas - " + (administrador ? "Administrador" : "Cliente"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1180, 750);
        setLocationRelativeTo(null);

        construirInterfaz();
        if (administrador) {
            mostrarPagina("productos", "Productos", "Gestiona el inventario de tu tienda", 0);
        } else {
            mostrarPagina("catalogo", "Catalogo", "Compra tus productos favoritos", 0);
        }
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstantes.BG);
        root.add(crearSidebar(), BorderLayout.WEST);
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(UIConstantes.BG);
        mainContent.add(crearTopbar(), BorderLayout.NORTH);

        panelProductos = new PanelProductos(sistema);
        panelClientes = new PanelClientes(sistema);
        panelVentas = new PanelVentas(sistema);
        panelCatalogoCliente = new PanelCatalogoCliente(sistema, idClienteActual);
        panelHistorialCliente = new PanelHistorialCliente(sistema);
        panelHistorialCliente.setIdClienteActual(idClienteActual);
        
        pages.setBackground(UIConstantes.BG);
        if (administrador) {
            pages.add(panelProductos, "productos");
            pages.add(panelClientes, "clientes");
            pages.add(panelVentas, "ventas");
        } else {
            pages.add(panelCatalogoCliente, "catalogo");
            pages.add(panelHistorialCliente, "historial");
        }
        
        mainContent.add(pages, BorderLayout.CENTER);
        root.add(mainContent, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(UIConstantes.SIDEBAR);
        sidebar.setBorder(new EmptyBorder(22, 14, 18, 14));

        JPanel top = new JPanel(); top.setOpaque(false); top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        
        // LOGO CON CARRITO
        JLabel logo = UIConstantes.label("🛒 SISTEMA", UIConstantes.TEXT, Font.BOLD, 18);
        JLabel brand = UIConstantes.label("Ventas Pro", UIConstantes.PRIMARY, Font.BOLD, 26);
        
        top.add(logo);
        top.add(brand);
        top.add(Box.createVerticalStrut(30));
        top.add(UIConstantes.label("MENÚ PRINCIPAL", UIConstantes.MUTED, Font.BOLD, 11));

        if (administrador) {
            addNavButton(top, "📦 Productos", "productos");
            addNavButton(top, "👥 Clientes", "clientes");
            addNavButton(top, "🧾 Ventas", "ventas");
        } else {
            addNavButton(top, "🛍 Catalogo", "catalogo");
            addNavButton(top, "🧾 Mis compras", "historial");
        }
        sidebar.add(top, BorderLayout.NORTH);
        return sidebar;
    }

    private void addNavButton(JPanel parent, String text, String page) {
        JButton b = UIConstantes.button(text, UIConstantes.SIDEBAR);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.addActionListener(e -> {
            int index = page.equals("productos") ? 0 : page.equals("clientes") ? 1 : page.equals("ventas") ? 2 : page.equals("catalogo") ? 0 : 1;
            String title = page.equals("productos") ? "📦 Productos" : page.equals("clientes") ? "👥 Clientes" : page.equals("ventas") ? "🧾 Ventas" : page.equals("catalogo") ? "🛍 Catalogo" : "🧾 Mis compras";
            String subtitle = page.equals("productos") ? "Gestión de inventario y precios" : page.equals("clientes") ? "Directorio y registro de clientes" : page.equals("ventas") ? "Terminal de punto de venta" : page.equals("catalogo") ? "Compra tus productos favoritos" : "Consulta todas tus ventas";
            mostrarPagina(page, title, subtitle, index);
        });
        navigationButtons.add(b);
        parent.add(b);
        parent.add(Box.createVerticalStrut(5));
    }

    private JPanel crearTopbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIConstantes.BG); top.setBorder(new EmptyBorder(22, 26, 10, 28));
        JPanel titles = new JPanel(); titles.setOpaque(false); titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        pageTitle = UIConstantes.label("Inicio", UIConstantes.TEXT, Font.BOLD, 28);
        pageSubtitle = UIConstantes.label("Bienvenido, " + (administrador ? "administrador" : "cliente"), UIConstantes.MUTED, Font.PLAIN, 14);
        titles.add(pageTitle); titles.add(pageSubtitle);
        top.add(titles, BorderLayout.WEST);
        if (administrador) {
            JButton cuenta = UIConstantes.button("Mi cuenta", UIConstantes.PRIMARY);
            cuenta.addActionListener(e -> mostrarCuentaAdministrador());
            top.add(cuenta, BorderLayout.EAST);
        }
        return top;
    }

    private void mostrarPagina(String page, String title, String subtitle, int selectedIndex) {
        pagesLayout.show(pages, page);
        pageTitle.setText(title); pageSubtitle.setText(subtitle);
        
        for (int i = 0; i < navigationButtons.size(); i++) {
            navigationButtons.get(i).setBackground(i == selectedIndex ? UIConstantes.CARD : UIConstantes.SIDEBAR);
        }
        
        if (page.equals("productos") && panelProductos != null) panelProductos.refrescarProductos();
        if (page.equals("clientes") && panelClientes != null) panelClientes.refrescarClientes();
        if (page.equals("ventas") && panelVentas != null) panelVentas.refrescarDatos();
        if (page.equals("catalogo") && panelCatalogoCliente != null) panelCatalogoCliente.refrescarProductos();
        if (page.equals("historial") && panelHistorialCliente != null) panelHistorialCliente.refrescarVentas();
    }

    private void mostrarCuentaAdministrador() {
        JDialog dialogo = new JDialog(this, "Mi cuenta", JDialog.ModalityType.APPLICATION_MODAL);
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
        datos.add(crearDatoCuenta("Nombre completo", nombreUsuario));
        datos.add(crearDatoCuenta("Telefono", telefonoUsuario));
        datos.add(crearDatoCuenta("Cedula de ciudadania", cedulaUsuario));
        contenido.add(datos, BorderLayout.CENTER);

        JButton eliminar = UIConstantes.button("Eliminar cuenta", UIConstantes.DANGER);
        eliminar.addActionListener(e -> mostrarConfirmacionEliminarAdministrador(dialogo));
        JButton salir = UIConstantes.button("Salir", UIConstantes.SIDEBAR);
        salir.addActionListener(e -> cerrarSesionAdministrador(dialogo));
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
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private JPanel crearDatoCuenta(String titulo, String valor) {
        JPanel dato = new JPanel(new BorderLayout(0, 3));
        dato.setOpaque(false);
        dato.add(UIConstantes.label(titulo, UIConstantes.MUTED, Font.BOLD, 12), BorderLayout.NORTH);
        dato.add(UIConstantes.label(valor == null ? "No registrado" : valor, UIConstantes.TEXT, Font.PLAIN, 15), BorderLayout.CENTER);
        return dato;
    }

    private void cerrarSesionAdministrador(JDialog dialogo) {
        dialogo.dispose();
        dispose();
        new VentanaAcceso().setVisible(true);
    }

    private void mostrarConfirmacionEliminarAdministrador(JDialog cuenta) {
        JDialog confirmacion = new JDialog(cuenta, "Eliminar cuenta", JDialog.ModalityType.APPLICATION_MODAL);
        confirmacion.setUndecorated(true);
        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(UIConstantes.BG);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstantes.LINE),
                BorderFactory.createEmptyBorder(16, 22, 20, 22)));
        contenido.add(UIConstantes.label("Eliminar cuenta", UIConstantes.TEXT, Font.BOLD, 18), BorderLayout.NORTH);
        JLabel mensaje = UIConstantes.label("¿Desea eliminar definitivamente su cuenta?", UIConstantes.TEXT, Font.PLAIN, 14);
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        contenido.add(mensaje, BorderLayout.CENTER);
        JButton no = UIConstantes.button("No", UIConstantes.SIDEBAR);
        no.addActionListener(e -> confirmacion.dispose());
        JButton si = UIConstantes.button("Si", UIConstantes.DANGER);
        si.addActionListener(e -> {
            confirmacion.dispose();
            cerrarSesionAdministrador(cuenta);
        });
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(no);
        acciones.add(si);
        contenido.add(acciones, BorderLayout.SOUTH);
        confirmacion.setContentPane(contenido);
        confirmacion.setSize(440, 200);
        confirmacion.setLocationRelativeTo(cuenta);
        confirmacion.setVisible(true);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new VentanaAcceso().setVisible(true));
    }
}