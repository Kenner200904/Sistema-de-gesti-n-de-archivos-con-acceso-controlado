import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Scanner;

/** Cliente de consola. Solo conoce la interfaz Servidor y trabaja a través del Proxy. */
public final class Cliente {
    private static final Path CARPETA_DESCARGAS_PREDETERMINADA = Path.of("descargas");

    private Cliente() { }

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            Servidor servidor = new Proxy();
            System.out.println("========================================");
            System.out.println(" SISTEMA DE GESTION DE ARCHIVOS");
            System.out.println(" El cliente accede mediante el Proxy");
            System.out.println("========================================");

            boolean ejecutando = true;
            while (ejecutando) {
                mostrarMenu();
                System.out.print("Seleccione una opcion: ");
                String opcion = entrada.nextLine().trim();

                try {
                    switch (opcion) {
                        case "1" -> subirArchivo(entrada, servidor);
                        case "2" -> descargarArchivo(entrada, servidor);
                        case "3" -> mostrarListado(servidor);
                        case "4" -> {
                            System.out.println("[Cliente] Saliendo del sistema. Hasta pronto");
                            ejecutando = false;
                        }
                        default -> System.out.println("[Cliente] Opcion no valida. Seleccione del 1 al 4.");
                    }
                } catch (NoSuchFileException e) {
                    System.out.println("[Cliente] Operacion no completada: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("[Cliente] Dato invalido: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("[Cliente] Error de archivo: " + e.getMessage());
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("No fue posible iniciar el servidor de archivos: " + e.getMessage());
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n--------------- MENU -----------------");
        System.out.println("1. Subir un archivo");
        System.out.println("2. Descargar un archivo por ID");
        System.out.println("3. Mostrar listado de archivos");
        System.out.println("4. Salir");
        System.out.println("--------------------------------------");
    }

    private static void subirArchivo(Scanner entrada, Servidor servidor) throws IOException {
        System.out.print("Ruta del archivo que desea subir: ");
        String rutaTexto = entrada.nextLine().trim();
        if (rutaTexto.isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacia.");
        }
        Fichero fichero = servidor.subir(Path.of(rutaTexto));
        System.out.println("[Cliente] Archivo subido correctamente:");
        System.out.println("         " + fichero);
    }

    private static void descargarArchivo(Scanner entrada, Servidor servidor) throws IOException {
        System.out.print("Identificador del archivo: ");
        String idTexto = entrada.nextLine().trim();
        if (idTexto.isEmpty()) {
            throw new IllegalArgumentException("Debe introducir un identificador.");
        }

        final int identificador;
        try {
            identificador = Integer.parseInt(idTexto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El identificador debe ser un numero entero.");
        }

        System.out.print("Carpeta de destino (Enter para usar 'descargas'): ");
        String carpetaTexto = entrada.nextLine().trim();
        Path carpetaDestino = carpetaTexto.isEmpty()
                ? CARPETA_DESCARGAS_PREDETERMINADA
                : Path.of(carpetaTexto);

        Path destino = servidor.descargar(identificador, carpetaDestino);
        System.out.println("[Cliente] Archivo descargado en: " + destino);
    }

    private static void mostrarListado(Servidor servidor) throws IOException {
        System.out.println("[Cliente] Listado de archivos:");
        List<Fichero> archivos = servidor.listar();
        if (archivos.isEmpty()) {
            System.out.println("No hay archivos almacenados.");
            return;
        }
        System.out.printf("%-8s %-35s %12s%n", "ID", "NOMBRE", "TAMANO (bytes)");
        System.out.println("-".repeat(60));
        for (Fichero fichero : archivos) {
            System.out.printf("%-8d %-35s %12d%n",
                    fichero.getIdentificador(), fichero.getNombre(), fichero.getTamanoBytes());
        }
    }
}
