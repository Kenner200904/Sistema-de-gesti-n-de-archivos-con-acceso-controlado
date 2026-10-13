/** Representa los metadatos de un archivo almacenado en el servidor. */
public final class Fichero {
    private final int identificador;
    private final String nombre;
    private final long tamanoBytes;

    public Fichero(int identificador, String nombre, long tamanoBytes) {
        if (identificador <= 0) {
            throw new IllegalArgumentException("El identificador debe ser mayor que cero.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacio.");
        }
        if (tamanoBytes < 0) {
            throw new IllegalArgumentException("El tamano no puede ser negativo.");
        }
        this.identificador = identificador;
        this.nombre = nombre;
        this.tamanoBytes = tamanoBytes;
    }

    public int getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Nombre: %s | Tamano: %d bytes", identificador, nombre, tamanoBytes);
    }
}
