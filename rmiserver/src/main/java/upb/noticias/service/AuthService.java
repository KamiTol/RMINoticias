package upb.noticias.service;

import upb.noticias.model.Sesion;
import upb.noticias.model.Usuario;
import upb.noticias.repository.SesionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AuthService {

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

    /** Retorna token si credenciales son válidas, null si no. */
    public String login(String username, String password) {
        Usuario usuario = USUARIOS.get(username);
        if (usuario == null || !usuario.getPassword().equals(password)) return null;
        String token = UUID.randomUUID().toString();
        sesionRepo.guardar(new Sesion(token, usuario));
        // Solo log de login exitoso (información esencial)
        System.out.printf("[LOGIN] %s (%s)%n", username, usuario.getRol());
        return token;
    }

    public Optional<Sesion> validarToken(String token) {
        return sesionRepo.buscarPorToken(token);
    }
}
