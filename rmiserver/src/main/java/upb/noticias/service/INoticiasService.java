package upb.noticias.service;

import upb.noticias.model.Noticia;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Contrato RMI del sistema de noticias.
 * Esta interfaz se comparte (copia) en el rmiclient.
 */
public interface INoticiasService extends Remote {

    // ── Autenticación ─────────────────────────────────────────
    /** Retorna el token de sesión, o null si las credenciales son incorrectas. */
    String login(String usuario, String contrasena) throws RemoteException;

    // ── Lectura (sin sesión) ──────────────────────────────────
    List<Noticia> listarNoticias()                          throws RemoteException;
    Noticia       buscarNoticia(String nombreUnico)         throws RemoteException;
    List<Noticia> buscarPorTitular(String fragmento)        throws RemoteException;

    // ── Escritura (requieren token) ───────────────────────────
    String publicarNoticia(String token, String nombreUnico,
                           String titular, String contenido)  throws RemoteException;

    String modificarNoticia(String token, String nombreUnico,
                            String nuevoTitular, String nuevoContenido) throws RemoteException;

    String eliminarNoticia(String token, String nombreUnico) throws RemoteException;
}
