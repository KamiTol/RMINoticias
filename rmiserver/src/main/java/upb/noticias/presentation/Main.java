package upb.noticias.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Punto de entrada del servidor.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Properties config = cargarConfig();
        Servidor servidor = new Servidor(
                config.getProperty("IP"),
                config.getProperty("PORT"),
                config.getProperty("SERVICENAME")
        );
        if (servidor.deploy()) {
            Thread.currentThread().join(); // Mantiene el servidor activo
        }
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
