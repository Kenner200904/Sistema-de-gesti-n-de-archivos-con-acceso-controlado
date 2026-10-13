# Implementación del patrón Proxy en Java

Aplicación de consola para subir archivos, consultar el listado y descargarlos por identificador.

## ¿Cómo funciona el patrón Proxy?

El cliente se comunica mediante la interfaz `Servidor` con el objeto `Proxy`, sin acceder directamente a `ServidorFicheros`. El Proxy valida las solicitudes, muestra cuándo interviene y delega las operaciones al servidor real. Así se centraliza el control de acceso y el cliente queda desacoplado de la implementación concreta del servidor.

## Ejecución

Se requiere JDK 17 o superior.

- **Windows:** ejecuta `compilar_y_ejecutar.bat`.
- **Linux/macOS:** ejecuta `./compilar_y_ejecutar.sh`.
