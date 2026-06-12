package upb.noticias.db;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import upb.noticias.model.Noticia;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Base de datos local en formato JSON (noticias.json).
 * Se guarda en el directorio de trabajo junto al JAR.
 * Permite guardar/cargar el estado de las noticias sin depender del servidor.
 */
public class NoticiaJsonDB {

    private static final Path DB_PATH = Paths.get("noticias.json");
    private final Gson gson;

    public NoticiaJsonDB() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /** Persiste la lista completa de noticias en el archivo JSON. */
    public void guardarTodas(List<Noticia> noticias) {
        try (Writer w = new OutputStreamWriter(
                Files.newOutputStream(DB_PATH, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING), StandardCharsets.UTF_8)) {
            gson.toJson(noticias, w);
        } catch (IOException e) {
            System.err.println("[DB] Error al guardar JSON: " + e.getMessage());
        }
    }

    /** Carga las noticias guardadas localmente. Retorna lista vacía si no existe el archivo. */
    public List<Noticia> cargarTodas() {
        if (!Files.exists(DB_PATH)) return new ArrayList<>();
        try (Reader r = new InputStreamReader(
                Files.newInputStream(DB_PATH), StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<Noticia>>() {}.getType();
            List<Noticia> lista = gson.fromJson(r, listType);
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[DB] Error al leer JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Indica si ya existe la BD local. */
    public boolean existe() {
        return Files.exists(DB_PATH);
    }

    /** Ruta del archivo JSON para mostrar en la UI. */
    public String getRuta() {
        return DB_PATH.toAbsolutePath().toString();
    }
}
