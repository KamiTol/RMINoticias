package upb.noticias.client;

import upb.noticias.service.INoticiasService;

import java.rmi.Naming;

/**
 * Gestiona el ciclo de vida de la conexión RMI.
 * Separar la conexión del uso del servicio facilita reconectar o cambiar el protocolo.
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
            System.out.println("[Cliente] Conectado a " + uri);
            return true;
        } catch (Exception e) {
            System.err.println("[Cliente] No se pudo conectar: " + e.getMessage());
            return false;
        }
    }

    public INoticiasService getServicio() {
        if (servicio == null) throw new IllegalStateException("No hay conexión activa. Llama a conectar() primero.");
        return servicio;
    }
}
