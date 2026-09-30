package Util;

import com.tienda.excepciones.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Punto único de acceso a SQLite. La base queda en el directorio de trabajo
 * del proyecto (tienda.db), por lo que los datos sobreviven al cierre de la app.
 */
public final class ConexionSQlite {

    /**
     * Base de datos incluida con el proyecto. Al ejecutar desde NetBeans,
     * user.dir normalmente corresponde a la carpeta del proyecto.
     */
    private static final Path RUTA_DB =
            Path.of(System.getProperty("user.dir"), "database", "tienda.db")
                .toAbsolutePath().normalize();

    private static final String URL = "jdbc:sqlite:" + RUTA_DB.toString();

    private ConexionSQlite() {
    }

    /**
     * Abre una conexión y activa las claves foráneas para ESA conexión.
     */
    public static Connection conectar() {
        try {
            try {
                Files.createDirectories(RUTA_DB.getParent());
            } catch (IOException e) {
                throw new PersistenciaException("No se pudo crear la carpeta de la base de datos: " + e.getMessage(), e);
            }
            Connection conexion = DriverManager.getConnection(URL);
            try (Statement st = conexion.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            return conexion;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible conectar con SQLite: " + e.getMessage(), e);
        }
    }

    /** Devuelve la ruta exacta de la base que está usando la aplicación. */
    public static String rutaBD() {
        return RUTA_DB.toAbsolutePath().toString();
    }

    /** Crea el esquema una sola vez y aplica restricciones básicas de integridad. */
    public static void crearTablas() {
        try (Connection conn = conectar(); Statement st = conn.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Producto (
                    id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    precio DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
                    stock INTEGER NOT NULL CHECK (stock >= 0),
                    categoria TEXT NOT NULL
                )
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Cliente (
                    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    cedula TEXT,
                    telefono TEXT
                )
                """);

            // Compatibilidad con la BD que ya tenías: agrega cedula si la tabla antigua no la tenía.
            if (!columnaExiste(conn, "Cliente", "cedula")) {
                st.executeUpdate("ALTER TABLE Cliente ADD COLUMN cedula TEXT");
            }

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Venta (
                    id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha TEXT NOT NULL,
                    id_cliente INTEGER NOT NULL,
                    total DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
                    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente) ON DELETE RESTRICT
                )
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Detalle_venta (
                    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_venta INTEGER NOT NULL,
                    id_producto INTEGER NOT NULL,
                    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
                    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0),
                    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
                    FOREIGN KEY (id_venta) REFERENCES Venta(id_venta) ON DELETE RESTRICT,
                    FOREIGN KEY (id_producto) REFERENCES Producto(id_producto) ON DELETE RESTRICT
                )
                """);
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible crear/verificar el esquema SQLite: " + e.getMessage(), e);
        }
    }

    private static boolean columnaExiste(Connection conn, String tabla, String columna) throws SQLException {
        try (Statement st = conn.createStatement();
             var rs = st.executeQuery("PRAGMA table_info(" + tabla + ")")) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
            return false;
        }
    }
}
