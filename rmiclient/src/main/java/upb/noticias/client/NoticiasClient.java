package upb.noticias.client;

import upb.noticias.model.Noticia;
import upb.noticias.service.INoticiasService;

import java.util.List;

/**
 * Proxy cliente: encapsula todas las llamadas al servicio remoto.
 * Maneja excepciones RMI y expone una API limpia a la capa de presentación.
 */
public class NoticiasClient {

    private final INoticiasService servicio;

    public NoticiasClient(ConexionRMI conexion) {
        this.servicio = conexion.getServicio();
    }

    // ── Autenticación ──────────────────────────────────────────

    public String login(String usuario, String contrasena) {
        try { return servicio.login(usuario, contrasena); }
        catch (Exception e) { System.err.println("Error en login: " + e.getMessage()); return null; }
    }

    // ── Lectura ────────────────────────────────────────────────

    public List<Noticia> listarNoticias() {
        try { return servicio.listarNoticias(); }
        catch (Exception e) { System.err.println("Error: " + e.getMessage()); return null; }
    }

    public Noticia buscarNoticia(String nombreUnico) {
        try { return servicio.buscarNoticia(nombreUnico); }
        catch (Exception e) { System.err.println("Error: " + e.getMessage()); return null; }
    }

    public List<Noticia> buscarPorTitular(String fragmento) {
        try { return servicio.buscarPorTitular(fragmento); }
        catch (Exception e) { System.err.println("Error: " + e.getMessage()); return null; }
    }

    // ── Escritura ──────────────────────────────────────────────

    public String publicarNoticia(String token, String nombreUnico,
                                  String titular, String contenido) {
        try { return servicio.publicarNoticia(token, nombreUnico, titular, contenido); }
        catch (Exception e) { return "ERROR: " + e.getMessage(); }
    }

    public String modificarNoticia(String token, String nombreUnico,
                                   String nuevoTitular, String nuevoContenido) {
        try { return servicio.modificarNoticia(token, nombreUnico, nuevoTitular, nuevoContenido); }
        catch (Exception e) { return "ERROR: " + e.getMessage(); }
    }

    public String eliminarNoticia(String token, String nombreUnico) {
        try { return servicio.eliminarNoticia(token, nombreUnico); }
        catch (Exception e) { return "ERROR: " + e.getMessage(); }
    }
}
