package upb.noticias.presentation;

import upb.noticias.client.NoticiasClient;
import upb.noticias.model.Noticia;

import java.util.List;
import java.util.Scanner;

/**
 * Menú interactivo en consola.
 * Esta clase solo se encarga de I/O; toda la lógica vive en NoticiasClient.
 */
public class Menu {

    private final NoticiasClient cliente;
    private final Scanner sc = new Scanner(System.in);

    private String token = null;
    private String usuarioActual = null;
    private String rolActual = null;

    public Menu(NoticiasClient cliente) {
        this.cliente = cliente;
    }

    public void ejecutar() {
        boolean activo = true;
        while (activo) {
            imprimirMenu();
            String opcion = sc.nextLine().trim();
            switch (opcion) {
                case "1" -> listarNoticias();
                case "2" -> buscarPorNombre();
                case "3" -> buscarPorTitular();
                case "4" -> iniciarSesion();
                case "5" -> publicarNoticia();
                case "6" -> modificarNoticia();
                case "7" -> eliminarNoticia();
                case "8" -> cerrarSesion();
                case "0" -> activo = false;
                default  -> System.out.println("Opción no válida.");
            }
        }
        System.out.println("¡Hasta luego!");
    }

    // ── Menú ───────────────────────────────────────────────────

    private void imprimirMenu() {
        System.out.println();
        if (token != null)
            System.out.printf("─── Sesión: %s [%s] ───%n", usuarioActual, rolActual);
        else
            System.out.println("─── Sin sesión activa ───");

        System.out.println("1. Listar todas las noticias");
        System.out.println("2. Buscar noticia por nombre único");
        System.out.println("3. Buscar noticias por titular");
        System.out.println("4. Iniciar sesión");
        if (token != null) {
            System.out.println("5. Publicar noticia");
            System.out.println("6. Modificar noticia");
            System.out.println("7. Eliminar noticia");
            System.out.println("8. Cerrar sesión");
        }
        System.out.println("0. Salir");
        System.out.print("Opción: ");
    }

    // ── Opciones ───────────────────────────────────────────────

    private void listarNoticias() {
        List<Noticia> lista = cliente.listarNoticias();
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay noticias.");
            return;
        }
        System.out.println("\n══ Noticias (" + lista.size() + ") ══");
        lista.forEach(n -> System.out.println(n + "\n"));
    }

    private void buscarPorNombre() {
        System.out.print("Nombre único: ");
        Noticia n = cliente.buscarNoticia(sc.nextLine().trim());
        System.out.println(n != null ? "\n" + n : "Noticia no encontrada.");
    }

    private void buscarPorTitular() {
        System.out.print("Fragmento del titular: ");
        List<Noticia> res = cliente.buscarPorTitular(sc.nextLine().trim());
        if (res == null || res.isEmpty()) { System.out.println("Sin resultados."); return; }
        System.out.println("\n══ Resultados (" + res.size() + ") ══");
        res.forEach(n -> System.out.println(n + "\n"));
    }

    private void iniciarSesion() {
        if (token != null) {
            System.out.println("Ya tienes sesión activa como '" + usuarioActual + "'. Ciérrala primero (opción 8).");
            return;
        }
        System.out.print("Usuario: ");
        String usuario = sc.nextLine().trim();
        System.out.print("Contraseña: ");
        String contrasena = sc.nextLine().trim();

        String t = cliente.login(usuario, contrasena);
        if (t == null) {
            System.out.println("Credenciales incorrectas.");
        } else {
            token = t;
            usuarioActual = usuario;
            rolActual = "admin".equals(usuario) ? "ADMIN" : "USUARIO";
            System.out.printf("Bienvenido, %s! Rol: %s%n", usuario, rolActual);
        }
    }

    private void publicarNoticia() {
        if (!verificarSesion()) return;
        System.out.print("Nombre único (ej: upb-evento-2025): ");
        String nombre = sc.nextLine().trim();
        System.out.print("Titular: ");
        String titular = sc.nextLine().trim();
        System.out.print("Contenido: ");
        String contenido = sc.nextLine().trim();
        System.out.println(cliente.publicarNoticia(token, nombre, titular, contenido));
    }

    private void modificarNoticia() {
        if (!verificarSesion()) return;
        System.out.print("Nombre único de la noticia: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Nuevo titular (Enter para no cambiar): ");
        String titular = sc.nextLine().trim();
        System.out.print("Nuevo contenido (Enter para no cambiar): ");
        String contenido = sc.nextLine().trim();
        System.out.println(cliente.modificarNoticia(token, nombre, titular, contenido));
    }

    private void eliminarNoticia() {
        if (!verificarSesion()) return;
        System.out.print("Nombre único de la noticia: ");
        String nombre = sc.nextLine().trim();
        System.out.print("¿Confirmar eliminación? (s/n): ");
        if (!"s".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.println("Cancelado.");
            return;
        }
        System.out.println(cliente.eliminarNoticia(token, nombre));
    }

    private void cerrarSesion() {
        if (token == null) { System.out.println("No hay sesión activa."); return; }
        System.out.println("Sesión de '" + usuarioActual + "' cerrada.");
        token = null; usuarioActual = null; rolActual = null;
    }

    private boolean verificarSesion() {
        if (token == null) { System.out.println("Debes iniciar sesión primero (opción 4)."); return false; }
        return true;
    }
}
