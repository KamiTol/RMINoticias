package upb.noticias.repository;

import upb.noticias.model.Sesion;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SesionRepository {

    private final Map<String, Sesion> store = new ConcurrentHashMap<>();

    public void guardar(Sesion sesion) {
        store.put(sesion.getToken(), sesion);
    }

    public Optional<Sesion> buscarPorToken(String token) {
        if (token == null) return Optional.empty();
        return Optional.ofNullable(store.get(token));
    }

    public boolean eliminar(String token) {
        return store.remove(token) != null;
    }
}
