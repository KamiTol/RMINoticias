package upb.noticias.model;

/**
 * Sesión activa en el servidor (no viaja por RMI, es interna al servidor).
 */
public class Sesion {

    private final String token;
    private final Usuario usuario;

    public Sesion(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String  getToken()    { return token; }
    public Usuario getUsuario()  { return usuario; }
    public String  getUsername() { return usuario.getUsername(); }
    public boolean esAdmin()     { return usuario.esAdmin(); }
}
