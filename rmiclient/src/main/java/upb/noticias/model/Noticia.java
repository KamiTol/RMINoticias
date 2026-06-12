package upb.noticias.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de dominio. Serializable para poder viajar por RMI.
 */
public class Noticia implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String nombreUnico;
    private String titular;
    private final String fechaCreacion;
    private String ultimaFechaActualizacion;
    private final String autor;
    private String contenido;

    public Noticia(String nombreUnico, String titular, String autor, String contenido) {
        this.nombreUnico = nombreUnico;
        this.titular = titular;
        this.autor = autor;
        this.contenido = contenido;
        String ahora = LocalDateTime.now().format(FMT);
        this.fechaCreacion = ahora;
        this.ultimaFechaActualizacion = ahora;
    }

    // ── Getters ──────────────────────────────────────────────
    public String getNombreUnico()               { return nombreUnico; }
    public String getTitular()                   { return titular; }
    public String getFechaCreacion()             { return fechaCreacion; }
    public String getUltimaFechaActualizacion()  { return ultimaFechaActualizacion; }
    public String getAutor()                     { return autor; }
    public String getContenido()                 { return contenido; }

    // ── Setters (registran timestamp automáticamente) ────────
    public void setTitular(String titular) {
        this.titular = titular;
        this.ultimaFechaActualizacion = LocalDateTime.now().format(FMT);
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
        this.ultimaFechaActualizacion = LocalDateTime.now().format(FMT);
    }

    @Override
    public String toString() {
        return String.format(
            "╔══ %s%n║ Titular  : %s%n║ Autor    : %s%n║ Creado   : %s%n║ Actualiz.: %s%n║ Contenido: %s%n╚══",
            nombreUnico, titular, autor, fechaCreacion, ultimaFechaActualizacion, contenido
        );
    }
}
