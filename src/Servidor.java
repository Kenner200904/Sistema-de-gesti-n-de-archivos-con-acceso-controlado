import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Interfaz común para el servidor real y su Proxy. */
public interface Servidor {
    Fichero subir(Path archivoOrigen) throws IOException;

    Path descargar(int identificador, Path carpetaDestino) throws IOException;

    List<Fichero> listar() throws IOException;
}
