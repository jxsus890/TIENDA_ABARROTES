# Sistema de Ventas - SQLite

Proyecto Java Swing + Maven + SQLite.

## Persistencia

La aplicación usa `tienda.db` mediante JDBC con el driver Xerial SQLite declarado en `pom.xml`.
La conexión central está en `Util.ConexionSQlite` y activa `PRAGMA foreign_keys = ON` en cada conexión.

La capa de gestión ya no guarda productos, clientes y ventas en `ArrayList` como fuente de verdad: utiliza DAOs (`com.tienda.dao`) para leer/escribir SQLite.

## Flujo de una venta

1. Se valida cliente y productos.
2. Se inicia la operación de persistencia.
3. Se registra la cabecera de `Venta`.
4. Se descuenta el stock con condición `stock >= cantidad`.
5. Se registra cada `Detalle_venta`, conservando el precio histórico.
6. Se actualiza el total.
7. Se hace `COMMIT`.
8. Ante cualquier error se ejecuta `ROLLBACK`.

## Primera ejecución en NetBeans

1. Abre `mavenproject4` como proyecto Maven.
2. Deja que NetBeans descargue las dependencias Maven.
3. Ejecuta `com.tienda.app.Main`.
4. Registra un producto y un cliente.
5. Cierra completamente el programa.
6. Vuelve a abrirlo: los datos deben seguir apareciendo.

La base `tienda.db` se guarda en la carpeta `SistemaVentas` dentro del directorio del usuario del sistema. La aplicación muestra la ruta exacta al iniciar.

## Nota sobre la base anterior

La aplicación intenta agregar automáticamente la columna `cedula` si la tabla `Cliente` ya existía sin esa columna. Si tienes una base de pruebas que no necesites conservar, puedes respaldarla y eliminar `tienda.db` para que el esquema limpio se cree de nuevo.

## Recomendación para la entrega

No uses las clases/ventanas antiguas que cargaban datos de demostración automáticamente. El flujo principal debe ser `Main -> VentanaAcceso -> MainDiseno` para trabajar siempre contra SQLite.


## Base de datos SQLite
La base incluida está en `database/tienda.db`. La aplicación usa este archivo cuando se ejecuta desde la carpeta del proyecto en NetBeans. Haz una copia de seguridad de `database/tienda.db` antes de reemplazar o modificar el proyecto.
