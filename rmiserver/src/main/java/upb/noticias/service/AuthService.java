package upb.noticias.service;

import upb.noticias.model.Sesion;
import upb.noticias.model.Usuario;
import upb.noticias.repository.SesionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Gestiona usuarios y sesiones.
 * Separa la responsabilidad de autenticación del servicio de noticias.
 */
public class AuthService {

    // Usuarios hardcodeados (en producción: cargar desde BD o archivo)
    private static final Map<String, Usuario> USUARIOS = Map.of(
        "admin",  new Usuario("admin",  "admin123",  Usuario.Rol.ADMIN),
        "maria",  new Usuario("maria",  "maria123",  Usuario.Rol.USUARIO),
        "carlos", new Usuario("carlos", "carlos123", Usuario.Rol.USUARIO),
        "ana",    new Usuario("ana",    "ana123",    Usuario.Rol.USUARIO)
    );

    private final SesionRepository sesionRepo;

    public AuthService(SesionRepository sesionRepo) {
        this.sesionRepo = sesionRepo;
    }

    /**
     * Valida credenciales y, si son correctas, crea y devuelve un token de sesión.
     * Retorna null si las credenciales son incorrectas.
     */
    public String login(String username, String password) {
        Usuario usuario = USUARIOS.get(username);
        if (usuario == null || !usuario.getPassword().equals(password)) return null;

        String token = UUID.randomUUID().toString();
        sesionRepo.guardar(new Sesion(token, usuario));
        System.out.printf("[Auth] Login: %s (%s)%n", username, usuario.getRol());
        return token;
    }

    /**
     * Busca la sesión asociada al token.
     * Retorna Optional.empty() si el token es inválido o nulo.
     */
    public Optional<Sesion> validarToken(String token) {
        return sesionRepo.buscarPorToken(token);
    }
}
