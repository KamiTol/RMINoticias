package upb.noticias.service;

import upb.noticias.model.Noticia;
import upb.noticias.model.Sesion;
import upb.noticias.repository.NoticiaRepository;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio RMI.
 * Logs mínimos: solo errores críticos y operaciones de escritura.
 */
public class NoticiasService extends UnicastRemoteObject implements INoticiasService {

    private static final long serialVersionUID = 1L;

    private final NoticiaRepository noticiaRepo;
    private final AuthService authService;

    public NoticiasService(NoticiaRepository noticiaRepo, AuthService authService)
            throws RemoteException {
        super();
        this.noticiaRepo = noticiaRepo;
        this.authService = authService;
    }

    @Override
    public String login(String usuario, String contrasena) throws RemoteException {
        return authService.login(usuario, contrasena);
    }

    @Override
    public List<Noticia> listarNoticias() throws RemoteException {
        return noticiaRepo.listarTodas();
    }

    @Override
    public Noticia buscarNoticia(String nombreUnico) throws RemoteException {
        return noticiaRepo.buscarPorNombre(nombreUnico).orElse(null);
    }

    @Override
    public List<Noticia> buscarPorTitular(String fragmento) throws RemoteException {
        return noticiaRepo.buscarPorTitular(fragmento);
    }

    @Override
    public String publicarNoticia(String token, String nombreUnico,
                                  String titular, String contenido) throws RemoteException {
        Optional<Sesion> sesion = authService.validarToken(token);
        if (sesion.isEmpty()) return "ERROR: No autenticado o sesión inválida.";

        Noticia nueva = new Noticia(nombreUnico, titular, sesion.get().getUsername(), contenido);
        if (!noticiaRepo.guardar(nueva))
            return "ERROR: Ya existe una noticia con nombre único '" + nombreUnico + "'.";

        System.out.printf("[PUBLICAR] %s por %s%n", nombreUnico, sesion.get().getUsername());
        return "OK: Noticia '" + nombreUnico + "' publicada.";
    }

    @Override
    public String modificarNoticia(String token, String nombreUnico,
                                   String nuevoTitular, String nuevoContenido) throws RemoteException {
        Optional<Sesion> sesion = authService.validarToken(token);
        if (sesion.isEmpty()) return "ERROR: No autenticado o sesión inválida.";

        Optional<Noticia> opt = noticiaRepo.buscarPorNombre(nombreUnico);
        if (opt.isEmpty()) return "ERROR: Noticia '" + nombreUnico + "' no encontrada.";

        Noticia noticia = opt.get();
        Sesion s = sesion.get();

        if (!s.esAdmin() && !noticia.getAutor().equals(s.getUsername()))
            return "ERROR: Solo el autor o un administrador puede modificar esta noticia.";

        if (nuevoTitular   != null && !nuevoTitular.isBlank())   noticia.setTitular(nuevoTitular);
        if (nuevoContenido != null && !nuevoContenido.isBlank()) noticia.setContenido(nuevoContenido);

        System.out.printf("[MODIFICAR] %s por %s%n", nombreUnico, s.getUsername());
        return "OK: Noticia '" + nombreUnico + "' modificada.";
    }

    @Override
    public String eliminarNoticia(String token, String nombreUnico) throws RemoteException {
        Optional<Sesion> sesion = authService.validarToken(token);
        if (sesion.isEmpty()) return "ERROR: No autenticado o sesión inválida.";

        Optional<Noticia> opt = noticiaRepo.buscarPorNombre(nombreUnico);
        if (opt.isEmpty()) return "ERROR: Noticia '" + nombreUnico + "' no encontrada.";

        Sesion s = sesion.get();
        if (!s.esAdmin() && !opt.get().getAutor().equals(s.getUsername()))
            return "ERROR: Solo el autor o un administrador puede eliminar esta noticia.";

        noticiaRepo.eliminar(nombreUnico);
        System.out.printf("[ELIMINAR] %s por %s%n", nombreUnico, s.getUsername());
        return "OK: Noticia '" + nombreUnico + "' eliminada.";
    }
}
