package com.tienda.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UIConstantes {
    public static final Color BG = new Color(7, 19, 34);
    public static final Color SIDEBAR = new Color(12, 31, 53);
    public static final Color CARD = new Color(14, 34, 57);
    public static final Color CARD_2 = new Color(17, 41, 67);
    public static final Color INPUT = new Color(9, 27, 46);
    public static final Color LINE = new Color(37, 72, 105);
    public static final Color PRIMARY = new Color(61, 132, 184);
    public static final Color SUCCESS = new Color(55, 139, 108);
    public static final Color DANGER = new Color(161, 76, 87);
    public static final Color TEXT = new Color(240, 246, 252);
    public static final Color MUTED = new Color(153, 178, 202);

    public static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 17);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);

    public static javax.swing.border.Border crearBordeSeccion(String titulo) {
        return BorderFactory.createTitledBorder(
                new LineBorder(LINE, 1, true), " " + titulo + " ",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, TEXT
        );
    }

    public static JLabel label(String text, Color color, int style, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(color); l.setFont(new Font("SansSerif", style, size));
        return l;
    }

    public static JButton button(String text, Color background) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(FONT_BOLD); b.setForeground(Color.WHITE); b.setBackground(background);
        b.setContentAreaFilled(false); b.setFocusPainted(false); b.setBorder(new EmptyBorder(9, 15, 9, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // FIX BUSCADOR BUGUEADO: Forzamos texto y cursor BLANCO
    public static JTextField inputField() {
        JTextField f = new JTextField(); 
        f.setFont(FONT_REG); 
        f.setForeground(Color.WHITE); 
        f.setBackground(INPUT);
        f.setCaretColor(Color.WHITE); 
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(LINE, 1), new EmptyBorder(8, 10, 8, 10)));
        return f;
    }

    public static void styleCombo(JComboBox<?> combo) {
        combo.setFont(FONT_REG); combo.setForeground(Color.WHITE); combo.setBackground(INPUT); combo.setBorder(new LineBorder(LINE, 1));
        combo.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setBackground(LINE); button.setForeground(TEXT); button.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                button.setFocusPainted(false); button.setContentAreaFilled(false); button.setOpaque(true);
                return button;
            }
            @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(INPUT); g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });
    }

    // FIX SPINNER VENTAS: Botones oscuros personalizados
    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(FONT_REG);
        spinner.setBorder(new LineBorder(LINE, 1));
        spinner.setPreferredSize(new Dimension(112, 32));
        spinner.setMinimumSize(new Dimension(112, 32));
        spinner.setUI(new BasicSpinnerUI() {
            @Override protected Component createNextButton() {
                return crearBotonSpinner(spinner, SwingConstants.NORTH);
            }
            @Override protected Component createPreviousButton() {
                return crearBotonSpinner(spinner, SwingConstants.SOUTH);
            }
        });
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            editor.getTextField().setBackground(INPUT);
            editor.getTextField().setForeground(Color.WHITE);
            editor.getTextField().setCaretColor(Color.WHITE);
            editor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
            editor.getTextField().setColumns(4);
            editor.getTextField().setMargin(new Insets(0, 8, 0, 8));
        }
    }

    private static JButton crearBotonSpinner(JSpinner spinner, int direction) {
        BasicArrowButton button = new BasicArrowButton(
                direction, LINE, CARD_2, TEXT, TEXT);
        button.setBackground(LINE);
        button.setForeground(TEXT);
        button.setBorder(new LineBorder(LINE, 1));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.addActionListener(e -> {
            Object value = direction == SwingConstants.NORTH
                    ? spinner.getNextValue()
                    : spinner.getPreviousValue();
            if (value != null) {
                spinner.setValue(value);
            }
        });
        return button;
    }

    // FIX TABLAS CENTRADAS: Aplicamos alineación a TODAS las columnas
    public static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_REG); table.setForeground(TEXT); table.setBackground(CARD);
        table.setSelectionForeground(Color.WHITE); table.setSelectionBackground(PRIMARY); table.setGridColor(LINE);
        table.setShowVerticalLines(false); table.setShowHorizontalLines(true); table.setRowMargin(0);
        table.setIntercellSpacing(new Dimension(0, 1)); table.setFillsViewportHeight(true);

        // Cabecera centrada
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBackground(CARD_2); headerRenderer.setForeground(TEXT); headerRenderer.setFont(FONT_BOLD);
        headerRenderer.setBorder(BorderFactory.createCompoundBorder(new LineBorder(LINE, 1), new EmptyBorder(0, 10, 0, 10)));
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));

        return table;
    }

    // Método auxiliar para garantizar el centrado de las celdas
    public static void centrarCeldasTabla(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}