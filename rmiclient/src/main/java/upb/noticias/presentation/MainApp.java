package upb.noticias.presentation;

import javafx.application.Application;
import javafx.stage.Stage;
import upb.noticias.client.ConexionRMI;
import upb.noticias.client.NoticiasClient;
import upb.noticias.db.NoticiaJsonDB;

import java.io.InputStream;
import java.util.Properties;

/**
 * Punto de entrada JavaFX. Reemplaza el Main de consola.
 * Lanza directamente la ventana de login; sin menú de texto.
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties config = cargarConfig();
        ConexionRMI conexion = new ConexionRMI(
                config.getProperty("IP"),
                config.getProperty("PORT"),
                config.getProperty("SERVICENAME")
        );

        NoticiaJsonDB db = new NoticiaJsonDB();

        // Intentar conectar; la ventana principal mostrará el estado
        boolean conectado = conexion.conectar();
        NoticiasClient cliente = conectado ? new NoticiasClient(conexion) : null;

        NoticiasUI ui = new NoticiasUI(cliente, db, conectado);
        ui.mostrar(primaryStage);
    }

    private Properties cargarConfig() {
        Properties p = new Properties();
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) throw new RuntimeException("config.properties no encontrado");
            p.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo config.properties", e);
        }
        return p;
    }
}
