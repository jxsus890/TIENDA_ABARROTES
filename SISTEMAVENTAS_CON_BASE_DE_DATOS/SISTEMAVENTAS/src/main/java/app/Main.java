package com.tienda.app;

import com.tienda.gestion.GestorVentas;
import com.tienda.gestion.ItemPedido;
import com.tienda.gestion.SistemaVentas;
import com.tienda.modelo.Cliente;
import com.tienda.modelo.Producto;
import com.tienda.modelo.Venta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Punto de entrada: menú de consola que ejercita las clases de negocio.
 * Esta clase NO contiene reglas de negocio, solo entrada/salida: toda la
 * lógica vive en com.tienda.modelo y com.tienda.gestion.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final SistemaVentas sistema = new SistemaVentas();

    public static void main(String[] args) {
        Util.ConexionSQlite.crearTablas();
        System.out.println("Base SQLite utilizada por el sistema: " + Util.ConexionSQlite.rutaBD());
        if (args.length > 0 && "--consola".equalsIgnoreCase(args[0])) {
            ejecutarConsola();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException ignored) {
                // Se mantiene el aspecto predeterminado si el del sistema no está disponible.
            }
            new VentanaAcceso().setVisible(true);
        });
    }

    /** Mantiene disponible el flujo de consola de la entrega original. */
    private static void ejecutarConsola() {
        cargarDatosDeEjemplo();
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            try {
                ejecutar(opcion);
            } catch (RuntimeException ex) {
                System.out.println("Operación no realizada: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n===== SISTEMA DE VENTAS =====");
        System.out.println("1. Registrar producto");
        System.out.println("2. Consultar productos");
        System.out.println("3. Actualizar producto");
        System.out.println("4. Eliminar producto");
        System.out.println("5. Registrar cliente");
        System.out.println("6. Consultar clientes");
        System.out.println("7. Registrar venta");
        System.out.println("8. Consultar detalle de una venta");
        System.out.println("9. Listar todas las ventas");
        System.out.println("0. Salir");
    }

    private static void ejecutar(int opcion) {
        switch (opcion) {
            case 1 -> registrarProducto();
            case 2 -> consultarProductos();
            case 3 -> actualizarProducto();
            case 4 -> eliminarProducto();
            case 5 -> registrarCliente();
            case 6 -> consultarClientes();
            case 7 -> registrarVenta();
            case 8 -> consultarDetalleVenta();
            case 9 -> listarVentas();
            case 0 -> { /* salir */ }
            default -> System.out.println("Opción no válida.");
        }
    }

    // ---- Productos ----

    private static void registrarProducto() {
        String nombre = leerTexto("Nombre: ");
        BigDecimal precio = new BigDecimal(leerTexto("Precio: "));
        int stock = leerEntero("Stock: ");
        String categoria = leerTexto("Categoría: ");
        Producto p = sistema.productos().registrar(nombre, precio, stock, categoria);
        System.out.println("Registrado: " + p);
    }

    private static void consultarProductos() {
        sistema.productos().consultar().forEach(System.out::println);
    }

    private static void actualizarProducto() {
        int id = leerEntero("Id de producto: ");
        String nombre = leerTexto("Nuevo nombre: ");
        BigDecimal precio = new BigDecimal(leerTexto("Nuevo precio: "));
        int stock = leerEntero("Nuevo stock: ");
        String categoria = leerTexto("Nueva categoría: ");
        sistema.productos().actualizar(id, nombre, precio, stock, categoria);
        System.out.println("Producto actualizado.");
    }

    private static void eliminarProducto() {
        int id = leerEntero("Id de producto a eliminar: ");
        sistema.productos().eliminar(id, sistema.ventas().idsProductoConVentas());
        System.out.println("Producto eliminado.");
    }

    // ---- Clientes ----

    private static void registrarCliente() {
        String nombre = leerTexto("Nombre: ");
        String telefono = leerTexto("Teléfono: ");
        Cliente c = sistema.clientes().registrar(nombre, telefono);
        System.out.println("Registrado: " + c);
    }

    private static void consultarClientes() {
        sistema.clientes().consultar().forEach(System.out::println);
    }

    // ---- Ventas ----

    private static void registrarVenta() {
        int idCliente = leerEntero("Id de cliente: ");
        List<ItemPedido> items = new ArrayList<>();
        String seguir;
        do {
            int idProducto = leerEntero("  Id de producto: ");
            int cantidad = leerEntero("  Cantidad: ");
            items.add(new ItemPedido(idProducto, cantidad));
            seguir = leerTexto("¿Agregar otro producto? (s/n): ");
        } while (seguir.equalsIgnoreCase("s"));

        Venta venta = sistema.ventas().registrarVenta(idCliente, items);
        System.out.println("Venta registrada:\n" + venta);
    }

    private static void consultarDetalleVenta() {
        int idVenta = leerEntero("Id de venta: ");
        Venta venta = sistema.ventas().consultarDetalle(idVenta);
        System.out.println(venta);
    }

    private static void listarVentas() {
        GestorVentas gv = sistema.ventas();
        gv.consultarTodas().forEach(v -> System.out.println(v));
    }

    // ---- Datos de ejemplo para probar el sistema sin cargar todo a mano ----

    private static void cargarDatosDeEjemplo() {
        sistema.productos().registrar("Jarrón cerámico", new BigDecimal("45.00"), 10, "Arte");
        sistema.productos().registrar("Cuadro abstracto", new BigDecimal("120.00"), 5, "Arte");
        sistema.productos().registrar("Cojín decorativo", new BigDecimal("18.50"), 20, "Hogar");
        sistema.clientes().registrar("María López", "3001234567");
        sistema.clientes().registrar("Carlos Ruiz", "3009876543");
        System.out.println("Datos de ejemplo cargados (3 productos, 2 clientes).");
    }

    // ---- Utilidades de lectura ----

    private static String leerTexto(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int leerEntero(String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextInt()) {
            System.out.print("Ingresa un número válido: ");
            sc.next();
        }
        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }
}
