import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/** Intermediario que valida solicitudes y delega las operaciones al servidor real. */
public final class Proxy implements Servidor {
    private final Servidor servidorReal;

    public Proxy() throws IOException {
        this(new ServidorFicheros());
    }

    public Proxy(Servidor servidorReal) {
        if (servidorReal == null) {
            throw new IllegalArgumentException("Debe proporcionarse un servidor real.");
        }
        this.servidorReal = servidorReal;
    }

    @Override
    public Fichero subir(Path archivoOrigen) throws IOException {
        System.out.println("[Proxy] Solicitud de subida recibida. Validando datos...");
        if (archivoOrigen == null || archivoOrigen.toString().isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacia.");
        }
        if (!java.nio.file.Files.isRegularFile(archivoOrigen)) {
            throw new NoSuchFileException("No se encontro el archivo indicado: " + archivoOrigen);
        }
        System.out.println("[Proxy] Validacion superada. Delegando al servidor real.");
        Fichero resultado = servidorReal.subir(archivoOrigen);
        System.out.println("[Proxy] La solicitud de subida termino correctamente.");
        return resultado;
    }

    @Override
    public Path descargar(int identificador, Path carpetaDestino) throws IOException {
        System.out.println("[Proxy] Solicitud de descarga recibida. Validando datos...");
        if (identificador <= 0) {
            throw new IllegalArgumentException("El identificador debe ser un numero mayor que cero.");
        }
        if (carpetaDestino == null || carpetaDestino.toString().isBlank()) {
            throw new IllegalArgumentException("La carpeta de destino no puede estar vacia.");
        }
        System.out.println("[Proxy] Datos validos. Solicitando la descarga al servidor real.");
        try {
            Path ruta = servidorReal.descargar(identificador, carpetaDestino);
            System.out.println("[Proxy] Descarga completada.");
            return ruta;
        } catch (NoSuchFileException e) {
            System.out.printf("[Proxy] Solicitud rechazada: no se encontro el archivo con ID %d.%n", identificador);
            throw e;
        }
    }

    @Override
    public List<Fichero> listar() throws IOException {
        System.out.println("[Proxy] Solicitud de listado recibida. Delegando al servidor real.");
        List<Fichero> archivos = servidorReal.listar();
        System.out.println("[Proxy] Listado recibido correctamente.");
        return archivos;
    }
}
