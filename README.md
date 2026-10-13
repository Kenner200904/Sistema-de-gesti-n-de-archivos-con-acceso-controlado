# Implementación del patrón Proxy en Java

## Descripción
Aplicación de consola para gestionar archivos mediante el patrón estructural **Proxy**. El cliente crea `Proxy` y usa la interfaz `Servidor`; la construcción de `ServidorFicheros` queda encapsulada dentro del Proxy, de modo que el cliente no necesita conocer ni invocar la clase real. El servidor real guarda copias de los archivos en `almacen_servidor/`.

## Requisitos
- JDK 17 o superior.
- No requiere bibliotecas externas.

## Clases
- `Servidor.java`: interfaz común con las operaciones de subir, descargar y listar.
- `ServidorFicheros.java`: servidor real. Copia los archivos y consulta el almacenamiento local.
- `Proxy.java`: intermediario que valida solicitudes, registra su intervención y delega las operaciones.
- `Fichero.java`: metadatos del archivo: identificador, nombre y tamaño en bytes.
- `Cliente.java`: menú interactivo de consola; usa únicamente la interfaz `Servidor` a través del Proxy.

## Compilar y ejecutar
Desde la carpeta raíz del proyecto:

### Linux / macOS
```bash
chmod +x compilar_y_ejecutar.sh
./compilar_y_ejecutar.sh
```

### Windows (CMD o PowerShell)
```bat
compilar_y_ejecutar.bat
```

También puede ejecutarse manualmente desde la raíz:

```bash
javac -d out src/*.java
java -cp out Cliente
```

## Uso
1. Seleccione **1** y escriba la ruta de un archivo existente para subir una copia al servidor.
2. Seleccione **2**, introduzca el ID y especifique la carpeta de descarga. Si deja la carpeta vacía, se utiliza `descargas/`.
3. Seleccione **3** para consultar los archivos disponibles.
4. Seleccione **4** para salir.

Los archivos se almacenan localmente en `almacen_servidor/` con un prefijo interno basado en el ID. Las descargas se copian a la carpeta indicada. Si ya existe allí un archivo con el mismo nombre, se reemplaza.

## ¿Cómo funciona Proxy en esta solución?
El patrón Proxy proporciona un objeto intermediario con la misma interfaz que el objeto real. `Cliente` recibe un `Servidor` que en tiempo de ejecución es un `Proxy`. Antes de delegar, el Proxy valida los datos de entrada y muestra mensajes de seguimiento. Después reenvía la petición a `ServidorFicheros`, que ejecuta la operación real. Así se centraliza el control de las solicitudes y el cliente queda desacoplado de la clase concreta del servidor.

## Validaciones incluidas
- Ruta de subida vacía o inexistente.
- Identificador de descarga no numérico, no positivo o inexistente.
- Carpeta de destino vacía/inválida.
- Nombre de archivo normalizado antes de guardarlo en el almacén.

## Diagramas y pruebas
- `diagramas/diagrama_clases.puml`: fuente editable del diagrama de clases.
- `diagramas/diagrama_clases.dot`, `diagramas/diagrama_clases.svg` y `diagramas/diagrama_clases.png`: otras fuentes y versiones renderizadas del diagrama de clases.
- `diagramas/diagrama_secuencia.puml`: fuente editable del diagrama de secuencia.
- `diagramas/diagrama_secuencia.png`: diagrama de secuencia renderizado.
- `capturas/`: evidencia gráfica de una ejecución exitosa y otra con entradas inválidas.
- `pruebas/salida_prueba_exitosa.txt` y `pruebas/salida_prueba_validaciones.txt`: registros de salida de consola de las dos pruebas.
- `pruebas/verificar_pruebas.sh`: script para repetir las pruebas automatizadas en Linux/macOS; compila, ejecuta las operaciones y comprueba la copia descargada.

## Nota sobre el alcance
La aplicación copia archivos locales hacia una carpeta que representa el almacenamiento del servidor. Es una simulación local de un servidor de archivos, sin red, cuentas de usuario ni autenticación. El Proxy controla y valida las solicitudes dentro de este ejemplo didáctico.
