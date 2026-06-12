package upb.noticias.presentation;

import upb.noticias.client.NoticiasClient;
import upb.noticias.model.Noticia;

import java.util.List;
import java.util.Scanner;

/**
 * Menú de consola — conservado como fallback.
 * Todas las llamadas RMI están envueltas en try-catch porque los métodos
 * de NoticiasClient lanzan checked exceptions que los lambdas no pueden propagar.
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

    private void listarNoticias() {
        try {
            List<Noticia> lista = cliente.listarNoticias();
            if (lista == null || lista.isEmpty()) { System.out.println("No hay noticias."); return; }
            System.out.println("\n══ Noticias (" + lista.size() + ") ══");
            lista.forEach(n -> System.out.println(n.getTitular() + " [" + n.getNombreUnico() + "]\n"));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorNombre() {
        System.out.print("Nombre único: ");
        try {
            Noticia n = cliente.buscarNoticia(sc.nextLine().trim());
            System.out.println(n != null ? n.getTitular() : "Noticia no encontrada.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorTitular() {
        System.out.print("Fragmento del titular: ");
        try {
            List<Noticia> res = cliente.buscarPorTitular(sc.nextLine().trim());
            if (res == null || res.isEmpty()) { System.out.println("Sin resultados."); return; }
            res.forEach(n -> System.out.println(n.getTitular() + " [" + n.getNombreUnico() + "]"));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void iniciarSesion() {
        if (token != null) { System.out.println("Ya tienes sesión activa. Ciérrala primero (opción 8)."); return; }
        System.out.print("Usuario: ");     String usuario   = sc.nextLine().trim();
        System.out.print("Contraseña: "); String contrasena = sc.nextLine().trim();
        try {
            String t = cliente.login(usuario, contrasena);
            if (t == null) {
                System.out.println("Credenciales incorrectas.");
            } else {
                token = t;
                usuarioActual = usuario;
                rolActual = "admin".equals(usuario) ? "ADMIN" : "USUARIO";
                System.out.printf("Bienvenido, %s! Rol: %s%n", usuario, rolActual);
            }
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    private void publicarNoticia() {
        if (!verificarSesion()) return;
        System.out.print("Nombre único: "); String nombre    = sc.nextLine().trim();
        System.out.print("Titular: ");      String titular   = sc.nextLine().trim();
        System.out.print("Contenido: ");    String contenido = sc.nextLine().trim();
        try {
            System.out.println(cliente.publicarNoticia(token, nombre, titular, contenido));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void modificarNoticia() {
        if (!verificarSesion()) return;
        System.out.print("Nombre único: ");     String nombre    = sc.nextLine().trim();
        System.out.print("Nuevo titular: ");    String titular   = sc.nextLine().trim();
        System.out.print("Nuevo contenido: ");  String contenido = sc.nextLine().trim();
        try {
            System.out.println(cliente.modificarNoticia(token, nombre, titular, contenido));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void eliminarNoticia() {
        if (!verificarSesion()) return;
        System.out.print("Nombre único: "); String nombre = sc.nextLine().trim();
        System.out.print("¿Confirmar? (s/n): ");
        if (!"s".equalsIgnoreCase(sc.nextLine().trim())) { System.out.println("Cancelado."); return; }
        try {
            System.out.println(cliente.eliminarNoticia(token, nombre));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
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
