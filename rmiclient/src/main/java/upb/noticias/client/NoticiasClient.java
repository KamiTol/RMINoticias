package upb.noticias.client;

import upb.noticias.model.Noticia;
import upb.noticias.service.INoticiasService;

import java.util.List;

/**
 * Proxy del cliente RMI. Sin mensajes de consola; los errores se propagan
 * como excepciones para que la UI los maneje.
 */
public class NoticiasClient {

    private final INoticiasService servicio;

    public NoticiasClient(ConexionRMI conexion) {
        this.servicio = conexion.getServicio();
    }

    public String login(String usuario, String contrasena) throws Exception {
        return servicio.login(usuario, contrasena);
    }

    public List<Noticia> listarNoticias() throws Exception {
        return servicio.listarNoticias();
    }

    public Noticia buscarNoticia(String nombreUnico) throws Exception {
        return servicio.buscarNoticia(nombreUnico);
    }

    public List<Noticia> buscarPorTitular(String fragmento) throws Exception {
        return servicio.buscarPorTitular(fragmento);
    }

    public String publicarNoticia(String token, String nombreUnico,
                                  String titular, String contenido) throws Exception {
        return servicio.publicarNoticia(token, nombreUnico, titular, contenido);
    }

    public String modificarNoticia(String token, String nombreUnico,
                                   String nuevoTitular, String nuevoContenido) throws Exception {
        return servicio.modificarNoticia(token, nombreUnico, nuevoTitular, nuevoContenido);
    }

    public String eliminarNoticia(String token, String nombreUnico) throws Exception {
        return servicio.eliminarNoticia(token, nombreUnico);
    }
}
