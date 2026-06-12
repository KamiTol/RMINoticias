package upb.noticias.model;

import java.io.Serializable;

/**
 * Representa un usuario del sistema.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Rol { ADMIN, USUARIO }

    private final String username;
    private final String password;
    private final Rol rol;

    public Usuario(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public Rol getRol()          { return rol; }
    public boolean esAdmin()     { return Rol.ADMIN.equals(rol); }
}
