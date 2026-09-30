package com.tienda.app;

import com.tienda.gestion.SistemaVentas;
import java.math.BigDecimal;

/**
 * Catálogo típico de una tienda de barrio en Barranquilla.
 * Ejecutar UNA sola vez: clic derecho -> Run File (Shift+F6 en NetBeans).
 * Puedes borrar o comentar las líneas que no necesites antes de correrla.
 */
public class SembrarProductosBarrio {
    public static void main(String[] args) {
        SistemaVentas sistema = new SistemaVentas();

        // ===== BEBIDAS =====
        sistema.productos().registrar("Coca-Cola 400ml", new BigDecimal("2600"), 45, "Bebidas");
        sistema.productos().registrar("Coca-Cola 1.5L", new BigDecimal("6200"), 22, "Bebidas");
        sistema.productos().registrar("Pepsi 400ml", new BigDecimal("2400"), 30, "Bebidas");
        sistema.productos().registrar("Agua Cristal 600ml", new BigDecimal("1800"), 60, "Bebidas");
        sistema.productos().registrar("Jugo Hit Mango 200ml", new BigDecimal("1500"), 50, "Bebidas");
        sistema.productos().registrar("Jugo Tutti Frutti 200ml", new BigDecimal("1500"), 38, "Bebidas");
        sistema.productos().registrar("Malta Uva 355ml", new BigDecimal("2900"), 24, "Bebidas");
        sistema.productos().registrar("Cerveza Aguila 330ml", new BigDecimal("3300"), 70, "Bebidas");
        sistema.productos().registrar("Cerveza Costena 330ml", new BigDecimal("3000"), 65, "Bebidas");
        sistema.productos().registrar("Cerveza Club Colombia 330ml", new BigDecimal("3800"), 40, "Bebidas");
        sistema.productos().registrar("Gatorade 500ml", new BigDecimal("4500"), 18, "Bebidas");
        sistema.productos().registrar("Cafe instantaneo Colcafe sobre", new BigDecimal("500"), 90, "Bebidas");

        // ===== SNACKS =====
        sistema.productos().registrar("Papas Margarita 32g", new BigDecimal("2400"), 55, "Snacks");
        sistema.productos().registrar("Platanitos Nacional 25g", new BigDecimal("2500"), 48, "Snacks");
        sistema.productos().registrar("Doritos Nacho 34g", new BigDecimal("3900"), 33, "Snacks");
        sistema.productos().registrar("Chicharrones Fritolay", new BigDecimal("3200"), 27, "Snacks");
        sistema.productos().registrar("Mani Comapan 30g", new BigDecimal("1900"), 40, "Snacks");

        // ===== DULCES Y GALLETAS =====
        sistema.productos().registrar("Bon Bon Bum", new BigDecimal("300"), 120, "Dulces");
        sistema.productos().registrar("Chocolatina Jet", new BigDecimal("1900"), 80, "Dulces");
        sistema.productos().registrar("Chocolatina Choclito", new BigDecimal("1200"), 65, "Dulces");
        sistema.productos().registrar("Galletas Ducales", new BigDecimal("2400"), 50, "Dulces");
        sistema.productos().registrar("Galletas Saltin Noel", new BigDecimal("2600"), 42, "Dulces");
        sistema.productos().registrar("Galletas Festival", new BigDecimal("2200"), 38, "Dulces");
        sistema.productos().registrar("Chicles Trident", new BigDecimal("1600"), 60, "Dulces");
        sistema.productos().registrar("Boliche costeno", new BigDecimal("500"), 100, "Dulces");

        // ===== LACTEOS =====
        sistema.productos().registrar("Leche entera Colanta 1L", new BigDecimal("4400"), 25, "Lacteos");
        sistema.productos().registrar("Leche deslactosada Alqueria 1L", new BigDecimal("4900"), 18, "Lacteos");
        sistema.productos().registrar("Yogurt Alpina 200ml", new BigDecimal("2900"), 32, "Lacteos");
        sistema.productos().registrar("Queso costeno x250g", new BigDecimal("9800"), 15, "Lacteos");
        sistema.productos().registrar("Kumis Alqueria 1L", new BigDecimal("5200"), 12, "Lacteos");
        sistema.productos().registrar("Mantequilla Ranchera x250g", new BigDecimal("7100"), 14, "Lacteos");

        // ===== PANADERIA =====
        sistema.productos().registrar("Pan frances unidad", new BigDecimal("600"), 90, "Panaderia");
        sistema.productos().registrar("Pan de bono unidad", new BigDecimal("1500"), 40, "Panaderia");
        sistema.productos().registrar("Arepa de huevo", new BigDecimal("2800"), 35, "Panaderia");
        sistema.productos().registrar("Butifarra x4", new BigDecimal("6500"), 22, "Panaderia");
        sistema.productos().registrar("Mogolla unidad", new BigDecimal("1200"), 30, "Panaderia");

        // ===== ABARROTES / VIVERES =====
        sistema.productos().registrar("Arroz Diana x500g", new BigDecimal("2700"), 60, "Abarrotes");
        sistema.productos().registrar("Aceite Gourmet 1L", new BigDecimal("10200"), 20, "Abarrotes");
        sistema.productos().registrar("Azucar Manuelita x1kg", new BigDecimal("4300"), 35, "Abarrotes");
        sistema.productos().registrar("Sal marina x500g", new BigDecimal("1600"), 45, "Abarrotes");
        sistema.productos().registrar("Panela cuadrada x500g", new BigDecimal("3300"), 28, "Abarrotes");
        sistema.productos().registrar("Cafe Sello Rojo x250g", new BigDecimal("9200"), 18, "Abarrotes");
        sistema.productos().registrar("Chocolate Corona x250g", new BigDecimal("7500"), 16, "Abarrotes");
        sistema.productos().registrar("Atun Van Camps x160g", new BigDecimal("6500"), 24, "Abarrotes");
        sistema.productos().registrar("Sardinas Cunit", new BigDecimal("4900"), 26, "Abarrotes");
        sistema.productos().registrar("Pasta La Munieca x250g", new BigDecimal("2200"), 40, "Abarrotes");
        sistema.productos().registrar("Huevos AA x30 (cubeta)", new BigDecimal("16500"), 10, "Abarrotes");
        sistema.productos().registrar("Lentejas x500g", new BigDecimal("3800"), 22, "Abarrotes");
        sistema.productos().registrar("Frijol cargamanto x500g", new BigDecimal("5200"), 18, "Abarrotes");
        sistema.productos().registrar("Maiz para arepa x500g", new BigDecimal("2900"), 20, "Abarrotes");

        // ===== ASEO PERSONAL =====
        sistema.productos().registrar("Jabon Rey de bano", new BigDecimal("2300"), 50, "Aseo Personal");
        sistema.productos().registrar("Shampoo Savital sachet", new BigDecimal("900"), 70, "Aseo Personal");
        sistema.productos().registrar("Crema dental Colgate 75ml", new BigDecimal("4700"), 35, "Aseo Personal");
        sistema.productos().registrar("Papel higienico Familia x4", new BigDecimal("6400"), 30, "Aseo Personal");
        sistema.productos().registrar("Desodorante Rexona", new BigDecimal("9200"), 20, "Aseo Personal");
        sistema.productos().registrar("Toallas higienicas Nosotras x8", new BigDecimal("5400"), 25, "Aseo Personal");
        sistema.productos().registrar("Maquina de afeitar Bic", new BigDecimal("2100"), 40, "Aseo Personal");

        // ===== ASEO DEL HOGAR =====
        sistema.productos().registrar("Detergente Fab x500g", new BigDecimal("4400"), 28, "Aseo Hogar");
        sistema.productos().registrar("Jabon Rey de lavar", new BigDecimal("2600"), 33, "Aseo Hogar");
        sistema.productos().registrar("Limpido x500ml", new BigDecimal("2900"), 30, "Aseo Hogar");
        sistema.productos().registrar("Escoba plastica", new BigDecimal("9800"), 12, "Aseo Hogar");
        sistema.productos().registrar("Esponjilla Brillo x3", new BigDecimal("1300"), 45, "Aseo Hogar");
        sistema.productos().registrar("Bolsas de basura x10", new BigDecimal("3200"), 38, "Aseo Hogar");

        // ===== CIGARRILLOS Y LICORES =====
        sistema.productos().registrar("Cigarrillos Marlboro cajetilla", new BigDecimal("8900"), 25, "Cigarrillos y Licores");
        sistema.productos().registrar("Cigarrillos Boston cajetilla", new BigDecimal("6500"), 30, "Cigarrillos y Licores");
        sistema.productos().registrar("Ron Medellin media", new BigDecimal("19500"), 10, "Cigarrillos y Licores");
        sistema.productos().registrar("Aguardiente Nectar media", new BigDecimal("17200"), 12, "Cigarrillos y Licores");

        // ===== CONGELADOS Y EMBUTIDOS =====
        sistema.productos().registrar("Salchicha Ranchera x500g", new BigDecimal("7000"), 18, "Congelados y Embutidos");
        sistema.productos().registrar("Chorizo Zenu x250g", new BigDecimal("5400"), 20, "Congelados y Embutidos");
        sistema.productos().registrar("Mortadela Zenu x250g", new BigDecimal("4800"), 22, "Congelados y Embutidos");
        sistema.productos().registrar("Jamon Zenu x250g", new BigDecimal("7400"), 16, "Congelados y Embutidos");

        // ===== MASCOTAS =====
        sistema.productos().registrar("Concentrado Dogourmet x1kg", new BigDecimal("9200"), 14, "Mascotas");
        sistema.productos().registrar("Concentrado Friskies x500g", new BigDecimal("6800"), 10, "Mascotas");

        // ===== PAPELERIA Y VARIOS =====
        sistema.productos().registrar("Cuaderno 100 hojas", new BigDecimal("3300"), 15, "Papeleria");
        sistema.productos().registrar("Lapicero Bic", new BigDecimal("900"), 40, "Papeleria");
        sistema.productos().registrar("Velas x paquete", new BigDecimal("1600"), 25, "Papeleria");
        sistema.productos().registrar("Fosforos", new BigDecimal("1200"), 30, "Papeleria");
        sistema.productos().registrar("Pilas Duracell AA x2", new BigDecimal("6700"), 18, "Papeleria");
        sistema.productos().registrar("Encendedor Bic", new BigDecimal("2500"), 35, "Papeleria");

        System.out.println("Productos insertados. Total en la BD: " + sistema.productos().consultar().size());
    }
}
