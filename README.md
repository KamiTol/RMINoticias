# Sistema de Noticias UPB — RMI en Java

## Arquitectura: Layered Architecture

```
rmiserver/src/main/java/upb/noticias/
├── presentation/
│   ├── Main.java          ← Punto de entrada; lee config y lanza el servidor
│   └── Servidor.java      ← Composition Root: crea el registry y cablea dependencias
├── service/
│   ├── INoticiasService.java   ← Interfaz RMI (COMPARTIDA con rmiclient)
│   ├── NoticiasService.java    ← Implementación: lógica de negocio + UnicastRemoteObject
│   └── AuthService.java        ← Autenticación y gestión de tokens
├── repository/
│   ├── NoticiaRepository.java  ← CRUD de noticias (Map en memoria, reemplazable por BD)
│   └── SesionRepository.java   ← Almacén de sesiones activas
├── model/
│   ├── Noticia.java   ← Modelo serializable (COMPARTIDO con rmiclient)
│   ├── Usuario.java   ← Datos del usuario + enum Rol
│   └── Sesion.java    ← Estado de sesión (solo servidor, no viaja por RMI)
└── resources/
    └── config.properties

rmiclient/src/main/java/upb/noticias/
├── presentation/
│   ├── Main.java    ← Conecta y lanza el menú
│   └── Menu.java    ← I/O de consola; delega todo a NoticiasClient
├── client/
│   ├── ConexionRMI.java      ← Ciclo de vida de la conexión (Naming.lookup)
│   └── NoticiasClient.java   ← Proxy: envuelve llamadas RMI y maneja excepciones
├── service/
│   └── INoticiasService.java ← Copia de la interfaz (necesaria para el cast RMI)
├── model/
│   └── Noticia.java          ← Copia del modelo (necesaria para deserializar)
└── resources/
    └── config.properties
```

## Por qué esta separación

| Capa | Responsabilidad única | Beneficio |
|---|---|---|
| `presentation` | I/O y punto de entrada | Cambiar a GUI sin tocar lógica |
| `service` | Reglas de negocio | Testeable de forma aislada |
| `repository` | Acceso a datos | Cambiar a BD modificando solo esta capa |
| `model` | Estructura de datos | Serializable, sin lógica de negocio |
| `client` | Conexión RMI | Reconectar o cambiar protocolo en un lugar |

## Usuarios de prueba

| Usuario | Contraseña  | Rol     |
|---------|-------------|---------|
| admin   | admin123    | ADMIN   |
| maria   | maria123    | USUARIO |
| carlos  | carlos123   | USUARIO |
| ana     | ana123      | USUARIO |

## Cómo ejecutar

### Servidor
```bash
cd rmiserver
mvn compile exec:java
```

### Cliente (otra terminal)
```bash
cd rmiclient
mvn compile exec:java
```

### JAR ejecutable
```bash
mvn package
java -jar target/rmiserver-1.0-SNAPSHOT-jar-with-dependencies.jar
java -jar target/rmiclient-1.0-SNAPSHOT-jar-with-dependencies.jar
```
