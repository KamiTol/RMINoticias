package upb.noticias.presentation;

import upb.noticias.client.ConexionRMI;
import upb.noticias.client.NoticiasClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Punto de entrada del cliente.
 * Lee configuración, conecta por RMI y lanza el menú.
 */
public class Main {

    public static void main(String[] args) {
        Properties config = cargarConfig();

        ConexionRMI conexion = new ConexionRMI(
                config.getProperty("IP"),
                config.getProperty("PORT"),
                config.getProperty("SERVICENAME")
        );

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Sistema de Noticias UPB — RMI      ║");
        System.out.println("╚══════════════════════════════════════╝");

        if (!conexion.conectar()) {
            System.out.println("No se pudo conectar al servidor. Verifique que esté activo.");
            return;
        }

        NoticiasClient cliente = new NoticiasClient(conexion);
        new Menu(cliente).ejecutar();
    }

    private static Properties cargarConfig() {
        Properties p = new Properties();
        try (InputStream in = Main.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) throw new RuntimeException("config.properties no encontrado en resources/");
            p.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo config.properties", e);
        }
        return p;
    }
}
