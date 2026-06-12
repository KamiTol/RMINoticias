package upb.noticias.presentation;

import upb.noticias.repository.NoticiaRepository;
import upb.noticias.repository.SesionRepository;
import upb.noticias.service.AuthService;
import upb.noticias.service.INoticiasService;
import upb.noticias.service.NoticiasService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Servidor {

    private final String ip;
    private final String port;
    private final String serviceName;

    public Servidor(String ip, String port, String serviceName) {
        this.ip = ip;
        this.port = port;
        this.serviceName = serviceName;
    }

    public boolean deploy() {
        try {
            System.setProperty("java.rmi.server.hostname", ip);

            SesionRepository  sesionRepo  = new SesionRepository();
            NoticiaRepository noticiaRepo = new NoticiaRepository();
            AuthService       authService = new AuthService(sesionRepo);
            INoticiasService  service     = new NoticiasService(noticiaRepo, authService);

            String uri = "//" + ip + ":" + port + "/" + serviceName;
            LocateRegistry.createRegistry(Integer.parseInt(port));
            Naming.rebind(uri, service);

            // Solo el mensaje de inicio (información esencial)
            System.out.println("[SERVIDOR] Activo en " + uri);
            return true;

        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo iniciar el servidor: " + e.getMessage());
            return false;
        }
    }
}
