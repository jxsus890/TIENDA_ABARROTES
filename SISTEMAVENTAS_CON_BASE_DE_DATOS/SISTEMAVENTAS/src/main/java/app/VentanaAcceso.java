package com.tienda.app;

import com.tienda.gestion.SistemaVentas;
import com.tienda.gui.UIConstantes;
import com.tienda.modelo.Cliente;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/** Ventana inicial para elegir el tipo de usuario. */
public class VentanaAcceso extends JFrame {

    private final SistemaVentas sistema = new SistemaVentas();

    public VentanaAcceso() {
        super("Acceso - Sistema de Ventas");
        construirInterfaz();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 350);
        setMinimumSize(new java.awt.Dimension(560, 350));
        setResizable(true);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new javax.swing.BoxLayout(contenido, javax.swing.BoxLayout.Y_AXIS));
        contenido.setBackground(UIConstantes.BG);
        contenido.setBorder(new EmptyBorder(28, 42, 28, 42));

        contenido.add(javax.swing.Box.createVerticalGlue());

        JLabel titulo = UIConstantes.label("¿Cómo desea ingresar?", UIConstantes.TEXT, Font.BOLD, 26);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        contenido.add(titulo);
        contenido.add(javax.swing.Box.createVerticalStrut(32));

        JPanel opciones = new JPanel(new GridLayout(1, 2, 18, 0));
        opciones.setOpaque(false);
        JButton administrador = crearBoton("⚙ Administrador", UIConstantes.PRIMARY);
        JButton cliente = crearBoton("👤 Cliente", UIConstantes.SUCCESS);
        administrador.addActionListener(e -> pedirDatos(true));
        cliente.addActionListener(e -> pedirDatos(false));
        opciones.add(administrador);
        opciones.add(cliente);
        opciones.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 70));
        contenido.add(opciones);
        contenido.add(javax.swing.Box.createVerticalStrut(22));

        JLabel indicacion = UIConstantes.label("Seleccione un rol para continuar", UIConstantes.MUTED, Font.PLAIN, 14);
        indicacion.setHorizontalAlignment(SwingConstants.CENTER);
        indicacion.setAlignmentX(CENTER_ALIGNMENT);
        contenido.add(indicacion);

        contenido.add(javax.swing.Box.createVerticalStrut(18));

        JLabel separador = UIConstantes.label("¿Cliente nuevo?", UIConstantes.MUTED, Font.PLAIN, 13);
        separador.setHorizontalAlignment(SwingConstants.CENTER);
        separador.setAlignmentX(CENTER_ALIGNMENT);
        contenido.add(separador);
        contenido.add(javax.swing.Box.createVerticalStrut(8));

        JButton crearCuenta = crearBoton("📝 Crear cuenta", UIConstantes.PRIMARY);
        crearCuenta.setAlignmentX(CENTER_ALIGNMENT);
        crearCuenta.setMaximumSize(new java.awt.Dimension(260, 48));
        crearCuenta.addActionListener(e -> pedirDatosRegistro());
        contenido.add(crearCuenta);

        contenido.add(javax.swing.Box.createVerticalGlue());

        setContentPane(contenido);
    }

    private JButton crearBoton(String texto, java.awt.Color color) {
        JButton boton = UIConstantes.button(texto, color);
        boton.setFont(new Font("SansSerif", Font.BOLD, 16));
        boton.setHorizontalAlignment(SwingConstants.CENTER);
        boton.setBorder(new EmptyBorder(18, 10, 18, 10));
        return boton;
    }

    /** Ventana de acceso (login): exige que la cuenta ya exista para clientes. */
    private void pedirDatos(boolean administrador) {
        JDialog dialogo = new JDialog(this, administrador ? "Datos del administrador" : "Ingresar como cliente", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setSize(560, 380);
        dialogo.setResizable(false);
        dialogo.setLocationRelativeTo(this);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBackground(UIConstantes.BG);
        contenido.setBorder(new EmptyBorder(22, 28, 22, 28));
        contenido.add(UIConstantes.label(administrador ? "Datos del administrador" : "Ingresar como cliente", UIConstantes.TEXT, Font.BOLD, 22), BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(3, 1, 0, 8));
        campos.setOpaque(false);
        JTextField nombre = UIConstantes.inputField();
        JTextField telefono = UIConstantes.inputField();
        JTextField cedula = UIConstantes.inputField();
        configurarSoloNumeros(telefono, 10);
        configurarSoloNumeros(cedula, 10);
        campos.add(crearCampo("Nombre completo", nombre));
        campos.add(crearCampo("Telefono", telefono));
        campos.add(crearCampo("Cedula de ciudadania", cedula));
        contenido.add(campos, BorderLayout.CENTER);

        JButton continuar = crearBoton("Continuar", UIConstantes.SUCCESS);
        continuar.addActionListener(e -> continuarConDatos(dialogo, administrador, nombre, telefono, cedula));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        acciones.add(continuar);
        contenido.add(acciones, BorderLayout.SOUTH);
        dialogo.setContentPane(contenido);
        dialogo.setVisible(true);
    }

    private void continuarConDatos(JDialog dialogo, boolean administrador, JTextField nombre,
            JTextField telefono, JTextField cedula) {
            String nombreIngresado = nombre.getText().trim();
            String telefonoIngresado = telefono.getText().trim();
            String cedulaIngresada = cedula.getText().trim();
            if (nombreIngresado.isEmpty()) {
                mostrarAlerta(dialogo, "El nombre es obligatorio.", "Datos incompletos");
                return;
            }
            if (!telefonoIngresado.matches("\\d{10}")) {
                mostrarAlerta(dialogo, "El telefono debe contener exactamente 10 numeros.", "Telefono invalido");
                return;
            }
            if (!cedulaIngresada.matches("\\d{6,10}")) {
                mostrarAlerta(dialogo, "La cedula debe contener entre 6 y 10 numeros.", "Cedula invalida");
                return;
            }

            if (!administrador) {
                Cliente cuenta = sistema.clientes().buscarPorCedula(cedulaIngresada);
                if (cuenta == null) {
                    mostrarAlerta(dialogo, "No existe una cuenta de cliente con esa cedula. Debe crear una cuenta primero.", "Cuenta no encontrada");
                    return;
                }
                if (!cuenta.getNombre().equalsIgnoreCase(nombreIngresado)) {
                    mostrarAlerta(dialogo, "El nombre no coincide con la cuenta registrada para esa cedula.", "Datos incorrectos");
                    return;
                }
                new MainDiseno(false, cuenta.getNombre(), cuenta.getCedula(), telefonoIngresado).setVisible(true);
                dialogo.dispose();
                dispose();
                return;
            }

            new MainDiseno(true, nombreIngresado, cedulaIngresada, telefonoIngresado).setVisible(true);
            dialogo.dispose();
            dispose();
    }

    /** Ventana de registro (obligatoria para clientes nuevos). */
    private void pedirDatosRegistro() {
        JDialog dialogo = new JDialog(this, "Crear cuenta de cliente", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setSize(560, 380);
        dialogo.setResizable(false);
        dialogo.setLocationRelativeTo(this);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBackground(UIConstantes.BG);
        contenido.setBorder(new EmptyBorder(22, 28, 22, 28));
        contenido.add(UIConstantes.label("Crear cuenta de cliente", UIConstantes.TEXT, Font.BOLD, 22), BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(3, 1, 0, 8));
        campos.setOpaque(false);
        JTextField nombre = UIConstantes.inputField();
        JTextField telefono = UIConstantes.inputField();
        JTextField cedula = UIConstantes.inputField();
        configurarSoloNumeros(telefono, 10);
        configurarSoloNumeros(cedula, 10);
        campos.add(crearCampo("Nombre completo", nombre));
        campos.add(crearCampo("Telefono", telefono));
        campos.add(crearCampo("Cedula de ciudadania", cedula));
        contenido.add(campos, BorderLayout.CENTER);

        JButton registrar = crearBoton("Crear cuenta", UIConstantes.PRIMARY);
        registrar.addActionListener(e -> registrarCuenta(dialogo, nombre, telefono, cedula));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        acciones.add(registrar);
        contenido.add(acciones, BorderLayout.SOUTH);
        dialogo.setContentPane(contenido);
        dialogo.setVisible(true);
    }

    private void registrarCuenta(JDialog dialogo, JTextField nombre, JTextField telefono, JTextField cedula) {
        String nombreIngresado = nombre.getText().trim();
        String telefonoIngresado = telefono.getText().trim();
        String cedulaIngresada = cedula.getText().trim();
        if (nombreIngresado.isEmpty()) {
            mostrarAlerta(dialogo, "El nombre es obligatorio.", "Datos incompletos");
            return;
        }
        if (!telefonoIngresado.matches("\\d{10}")) {
            mostrarAlerta(dialogo, "El telefono debe contener exactamente 10 numeros.", "Telefono invalido");
            return;
        }
        if (!cedulaIngresada.matches("\\d{6,10}")) {
            mostrarAlerta(dialogo, "La cedula debe contener entre 6 y 10 numeros.", "Cedula invalida");
            return;
        }
        if (sistema.clientes().buscarPorCedula(cedulaIngresada) != null) {
            mostrarAlerta(dialogo, "Ya existe una cuenta con esa cedula. Use la opcion Cliente para ingresar.", "Cuenta existente");
            return;
        }
        sistema.clientes().registrar(nombreIngresado, cedulaIngresada, telefonoIngresado);
        new MainDiseno(false, nombreIngresado, cedulaIngresada, telefonoIngresado).setVisible(true);
        dialogo.dispose();
        dispose();
    }

    private void mostrarAlerta(Component padre, String mensaje, String titulo) {
        JDialog alerta = new JDialog(javax.swing.SwingUtilities.getWindowAncestor(padre), titulo,
                Dialog.ModalityType.APPLICATION_MODAL);
        alerta.setSize(390, 185);
        alerta.setResizable(false);
        alerta.setLocationRelativeTo(padre);

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(UIConstantes.BG);
        contenido.setBorder(new EmptyBorder(22, 26, 20, 26));

        JLabel texto = UIConstantes.label(mensaje, UIConstantes.TEXT, Font.PLAIN, 14);
        contenido.add(texto, BorderLayout.CENTER);

        JButton aceptar = crearBoton("Aceptar", UIConstantes.SUCCESS);
        aceptar.addActionListener(e -> alerta.dispose());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(aceptar);
        contenido.add(acciones, BorderLayout.SOUTH);

        alerta.setContentPane(contenido);
        alerta.getRootPane().setDefaultButton(aceptar);
        alerta.setVisible(true);
    }

    private JPanel crearCampo(String titulo, JTextField campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setOpaque(false);
        panel.add(UIConstantes.label(titulo, UIConstantes.MUTED, Font.BOLD, 12), BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        return panel;
    }

    private void configurarSoloNumeros(JTextField campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass bypass, int offset, int longitud, String texto, AttributeSet atributos)
                    throws BadLocationException {
                String actual = bypass.getDocument().getText(0, bypass.getDocument().getLength());
                String nuevoTexto = texto == null ? "" : texto;
                String resultado = actual.substring(0, offset) + nuevoTexto
                        + actual.substring(offset + longitud);
                if (!resultado.matches("\\d*") || (maximo > 0 && resultado.length() > maximo)) return;
                bypass.replace(offset, longitud, texto, atributos);
            }
        });
    }
}