package com.tienda.gui;

import javax.swing.*;
import java.awt.*;

public class PantallaInicio extends JFrame {

    private final Color bg = new Color(8, 25, 40);
    private final Color panelBg = new Color(11, 34, 52);
    private final Color borderAdmin = new Color(118, 157, 194);
    private final Color borderCliente = new Color(107, 183, 145);
    private final Color adminButton = new Color(103, 155, 197);
    private final Color clienteButton = new Color(106, 181, 145);
    private final Color text = new Color(232, 239, 247);

    public PantallaInicio() {
        super("Sistema de Ventas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setContentPane(construirPanel());
    }

    private JPanel construirPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);
        root.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(bg);
        contenido.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Sistema de Ventas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 64));
        titulo.setForeground(text);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel subtitulo = new JLabel("Selecciona cómo deseas ingresar");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 30));
        subtitulo.setForeground(new Color(217, 227, 239));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setHorizontalAlignment(SwingConstants.LEFT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));

        JPanel cards = new JPanel(new GridLayout(1, 2, 45, 0));
        cards.setOpaque(false);
        cards.add(cardAdministrador());
        cards.add(cardCliente());

        contenido.add(titulo);
        contenido.add(subtitulo);
        contenido.add(cards);
        root.add(contenido, BorderLayout.CENTER);
        return root;
    }

    private JPanel cardAdministrador() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(panelBg);
        card.setBorder(BorderFactory.createLineBorder(borderAdmin, 2));
        card.setPreferredSize(new Dimension(450, 320));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));

        JLabel title = new JLabel("Administrador");
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(text);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel("Gestiona productos, clientes y ventas");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 28));
        desc.setForeground(new Color(219, 230, 240));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        info.add(title);
        info.add(desc);

        JButton ingresar = new JButton("Ingresar");
        ingresar.setBackground(adminButton);
        ingresar.setForeground(text);
        ingresar.setFocusPainted(false);
        ingresar.setBorderPainted(false);
        ingresar.setFont(new Font("SansSerif", Font.BOLD, 36));
        ingresar.setPreferredSize(new Dimension(0, 90));
        ingresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ingresar.addActionListener(e -> {
            dispose();
            new com.tienda.gui.VentanaPrincipal().setVisible(true);
        });

        card.add(info, BorderLayout.CENTER);
        card.add(ingresar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel cardCliente() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(panelBg);
        card.setBorder(BorderFactory.createLineBorder(borderCliente, 2));
        card.setPreferredSize(new Dimension(450, 320));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));

        JLabel title = new JLabel("Cliente");
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(text);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel("Consulta productos y realiza compras");
        desc.setFont(new Font("SansSerif", Font.PLAIN, 28));
        desc.setForeground(new Color(219, 230, 240));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        desc.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        info.add(title);
        info.add(desc);

        JButton ingresar = new JButton("Ingresar");
        ingresar.setBackground(clienteButton);
        ingresar.setForeground(text);
        ingresar.setFocusPainted(false);
        ingresar.setBorderPainted(false);
        ingresar.setFont(new Font("SansSerif", Font.BOLD, 36));
        ingresar.setPreferredSize(new Dimension(0, 90));
        ingresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ingresar.addActionListener(e -> {
            dispose();
            new com.tienda.gui.VentanaCliente().setVisible(true);
        });

        card.add(info, BorderLayout.CENTER);
        card.add(ingresar, BorderLayout.SOUTH);
        return card;
    }
}
