package upb.noticias.client;

import upb.noticias.service.INoticiasService;

import java.rmi.Naming;

/**
 * Gestiona la conexión RMI. Solo imprime en consola mensajes de error críticos.
 */
public class ConexionRMI {

    private final String uri;
    private INoticiasService servicio;

    public ConexionRMI(String ip, String port, String serviceName) {
        this.uri = "rmi://" + ip + ":" + port + "/" + serviceName;
    }

    public boolean conectar() {
        try {
            servicio = (INoticiasService) Naming.lookup(uri);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo conectar al servidor RMI: " + e.getMessage());
            return false;
        }
    }

    public INoticiasService getServicio() {
        if (servicio == null)
            throw new IllegalStateException("Sin conexión activa.");
        return servicio;
    }
}
