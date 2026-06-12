package upb.noticias.service;

import upb.noticias.model.Noticia;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface INoticiasService extends Remote {
    String        login(String usuario, String contrasena)                            throws RemoteException;
    List<Noticia> listarNoticias()                                                    throws RemoteException;
    Noticia       buscarNoticia(String nombreUnico)                                   throws RemoteException;
    List<Noticia> buscarPorTitular(String fragmento)                                  throws RemoteException;
    String        publicarNoticia(String token, String nombreUnico,
                                  String titular, String contenido)                   throws RemoteException;
    String        modificarNoticia(String token, String nombreUnico,
                                   String nuevoTitular, String nuevoContenido)        throws RemoteException;
    String        eliminarNoticia(String token, String nombreUnico)                   throws RemoteException;
}
