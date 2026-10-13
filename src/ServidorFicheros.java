import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Servidor real: copia los archivos y mantiene sus metadatos mediante el nombre interno. */
public final class ServidorFicheros implements Servidor {
    private static final String SEPARADOR = "__";
    private final Path directorioAlmacen;

    public ServidorFicheros() throws IOException {
        this(Path.of("almacen_servidor"));
    }

    public ServidorFicheros(Path directorioAlmacen) throws IOException {
        if (directorioAlmacen == null) {
            throw new IllegalArgumentException("La carpeta de almacenamiento no puede ser nula.");
        }
        this.directorioAlmacen = directorioAlmacen.toAbsolutePath().normalize();
        Files.createDirectories(this.directorioAlmacen);
    }

    @Override
    public Fichero subir(Path archivoOrigen) throws IOException {
        System.out.println("[Servidor real] Ejecutando la operacion de subida...");
        if (archivoOrigen == null || !Files.isRegularFile(archivoOrigen)) {
            throw new NoSuchFileException("El archivo de origen no existe o no es un archivo normal.");
        }

        Path origen = archivoOrigen.toAbsolutePath().normalize();
        String nombreOriginal = origen.getFileName().toString();
        if (nombreOriginal.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacio.");
        }

        String nombreSeguro = limpiarNombre(nombreOriginal);
        int nuevoId = obtenerSiguienteIdentificador();
        Path destino = directorioAlmacen.resolve(nuevoId + SEPARADOR + nombreSeguro).normalize();
        if (!destino.startsWith(directorioAlmacen)) {
            throw new IOException("La ruta de destino no es valida.");
        }

        Files.copy(origen, destino, StandardCopyOption.COPY_ATTRIBUTES);
        Fichero fichero = new Fichero(nuevoId, nombreSeguro, Files.size(destino));
        System.out.printf("[Servidor real] Archivo almacenado correctamente con ID %d.%n", nuevoId);
        return fichero;
    }

    @Override
    public Path descargar(int identificador, Path carpetaDestino) throws IOException {
        System.out.println("[Servidor real] Ejecutando la operacion de descarga...");
        if (identificador <= 0) {
            throw new IllegalArgumentException("El identificador debe ser un numero mayor que cero.");
        }
        if (carpetaDestino == null) {
            throw new IllegalArgumentException("La carpeta de destino no puede estar vacia.");
        }

        Path archivoAlmacenado = buscarRutaPorId(identificador);
        if (archivoAlmacenado == null) {
            throw new NoSuchFileException("No existe un archivo con identificador " + identificador + ".");
        }

        String nombreOriginal = extraerNombre(archivoAlmacenado.getFileName().toString());
        Path carpeta = carpetaDestino.toAbsolutePath().normalize();
        Files.createDirectories(carpeta);
        Path destino = carpeta.resolve(nombreOriginal).normalize();
        if (!destino.startsWith(carpeta)) {
            throw new IOException("La ruta de descarga no es valida.");
        }
        if (archivoAlmacenado.toAbsolutePath().normalize().equals(destino)) {
            throw new IOException("El destino coincide con el archivo almacenado en el servidor.");
        }

        Files.copy(archivoAlmacenado, destino, StandardCopyOption.REPLACE_EXISTING);
        System.out.printf("[Servidor real] Archivo %d copiado a: %s%n", identificador, destino);
        return destino;
    }

    @Override
    public List<Fichero> listar() throws IOException {
        System.out.println("[Servidor real] Consultando el almacenamiento de archivos...");
        List<Fichero> resultado = new ArrayList<>();
        try (DirectoryStream<Path> elementos = Files.newDirectoryStream(directorioAlmacen)) {
            for (Path ruta : elementos) {
                if (!Files.isRegularFile(ruta)) {
                    continue;
                }
                String nombreInterno = ruta.getFileName().toString();
                int separador = nombreInterno.indexOf(SEPARADOR);
                if (separador <= 0) {
                    continue;
                }
                try {
                    int id = Integer.parseInt(nombreInterno.substring(0, separador));
                    String nombreOriginal = nombreInterno.substring(separador + SEPARADOR.length());
                    if (id > 0 && !nombreOriginal.isBlank()) {
                        resultado.add(new Fichero(id, nombreOriginal, Files.size(ruta)));
                    }
                } catch (NumberFormatException ignorado) {
                    // Ignora cualquier archivo que no respete el formato interno ID__nombre.
                }
            }
        }
        resultado.sort(Comparator.comparingInt(Fichero::getIdentificador));
        System.out.printf("[Servidor real] Se encontraron %d archivo(s).%n", resultado.size());
        return resultado;
    }

    private int obtenerSiguienteIdentificador() throws IOException {
        int mayorId = 0;
        try (DirectoryStream<Path> elementos = Files.newDirectoryStream(directorioAlmacen)) {
            for (Path ruta : elementos) {
                if (!Files.isRegularFile(ruta)) {
                    continue;
                }
                String nombre = ruta.getFileName().toString();
                int separador = nombre.indexOf(SEPARADOR);
                if (separador <= 0) {
                    continue;
                }
                try {
                    mayorId = Math.max(mayorId, Integer.parseInt(nombre.substring(0, separador)));
                } catch (NumberFormatException ignorado) {
                    // No considera archivos ajenos al formato del servidor.
                }
            }
        }
        if (mayorId == Integer.MAX_VALUE) {
            throw new IOException("No se pueden asignar mas identificadores de archivo.");
        }
        return mayorId + 1;
    }

    private Path buscarRutaPorId(int identificador) throws IOException {
        String prefijo = identificador + SEPARADOR;
        try (DirectoryStream<Path> elementos = Files.newDirectoryStream(directorioAlmacen)) {
            for (Path ruta : elementos) {
                if (Files.isRegularFile(ruta) && ruta.getFileName().toString().startsWith(prefijo)) {
                    return ruta;
                }
            }
        }
        return null;
    }

    private static String extraerNombre(String nombreInterno) {
        int separador = nombreInterno.indexOf(SEPARADOR);
        return separador < 0 ? nombreInterno : nombreInterno.substring(separador + SEPARADOR.length());
    }

    private static String limpiarNombre(String nombre) {
        // Evita separadores de ruta y caracteres problemáticos en sistemas Windows y Unix.
        String limpio = nombre.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_").trim();
        return limpio.isBlank() ? "archivo_sin_nombre" : limpio;
    }
}
