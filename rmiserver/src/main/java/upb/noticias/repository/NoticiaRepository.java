package upb.noticias.repository;

import upb.noticias.model.Noticia;

import java.util.*;
import java.util.stream.Collectors;

public class NoticiaRepository {

    private final Map<String, Noticia> store = new LinkedHashMap<>();

    public NoticiaRepository() {
        seed("upb-bienvenida-2025",
             "Bienvenidos al semestre 2025-1",
             "admin",
             "La Universidad Pontificia Bolivariana da la bienvenida a todos sus estudiantes al nuevo semestre académico.");
        seed("tecnologia-ia-avances",
             "Avances en Inteligencia Artificial en la UPB",
             "maria",
             "El grupo de investigación presentó sus últimos resultados en el congreso internacional de sistemas distribuidos.");
    }

    private void seed(String nombre, String titular, String autor, String contenido) {
        store.put(nombre, new Noticia(nombre, titular, autor, contenido));
    }

    public boolean guardar(Noticia noticia) {
        if (store.containsKey(noticia.getNombreUnico())) return false;
        store.put(noticia.getNombreUnico(), noticia);
        return true;
    }

    public Optional<Noticia> buscarPorNombre(String nombreUnico) {
        return Optional.ofNullable(store.get(nombreUnico));
    }

    public List<Noticia> listarTodas() {
        return new ArrayList<>(store.values());
    }

    public List<Noticia> buscarPorTitular(String fragmento) {
        String frag = fragmento.toLowerCase();
        return store.values().stream()
                .filter(n -> n.getTitular().toLowerCase().contains(frag))
                .collect(Collectors.toList());
    }

    public boolean eliminar(String nombreUnico) {
        return store.remove(nombreUnico) != null;
    }
}
