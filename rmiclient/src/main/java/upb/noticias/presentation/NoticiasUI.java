package upb.noticias.presentation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import upb.noticias.client.NoticiasClient;
import upb.noticias.db.NoticiaJsonDB;
import upb.noticias.model.Noticia;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz gráfica principal del cliente RMI Noticias.
 * Reemplaza completamente el menú de consola (Menu.java).
 */
public class NoticiasUI {

    // ── Estado de sesión ─────────────────────────────────────
    private NoticiasClient cliente;
    private final NoticiaJsonDB db;
    private boolean conectado;
    private String token = null;
    private String usuarioActual = null;

    // ── Datos ────────────────────────────────────────────────
    private final ObservableList<Noticia> noticiasList = FXCollections.observableArrayList();

    // ── Controles UI ─────────────────────────────────────────
    private Label lblEstado;
    private Label lblSesion;
    private TableView<Noticia> tabla;
    private TextField txtBuscar;
    private Button btnLogin, btnLogout;
    private Button btnPublicar, btnModificar, btnEliminar;
    private Button btnGuardarDB, btnCargarDB;
    private TextArea txtDetalle;
    private Label lblDBRuta;

    public NoticiasUI(NoticiasClient cliente, NoticiaJsonDB db, boolean conectado) {
        this.cliente   = cliente;
        this.db        = db;
        this.conectado = conectado;
    }

    public void mostrar(Stage stage) {
        stage.setTitle("Sistema de Noticias UPB — RMI");
        stage.setMinWidth(900);
        stage.setMinHeight(620);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");

        root.setTop(buildHeader());
        root.setLeft(buildSidebar());
        root.setCenter(buildCenter());
        root.setBottom(buildFooter());

        actualizarEstadoConexion();
        actualizarBotonesSegunSesion();

        // Si hay DB local, preguntar si cargar
        if (db.existe()) cargarDesdeDB();

        Scene scene = new Scene(root, 960, 640);
        stage.setScene(scene);
        stage.show();
    }

    // ══════════════════════════════════════════════════════════
    // CONSTRUCCIÓN DE LA UI
    // ══════════════════════════════════════════════════════════

    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setPadding(new Insets(14, 20, 14, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #1a237e;");

        Label titulo = new Label("📰  Noticias UPB — RMI");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        titulo.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblSesion = new Label("Sin sesión");
        lblSesion.setTextFill(Color.LIGHTGRAY);
        lblSesion.setFont(Font.font(13));

        lblEstado = new Label();
        lblEstado.setFont(Font.font(12));

        header.getChildren().addAll(titulo, spacer, lblSesion, lblEstado);
        return header;
    }

    private VBox buildSidebar() {
        VBox side = new VBox(10);
        side.setPadding(new Insets(18, 12, 18, 12));
        side.setStyle("-fx-background-color: #283593; -fx-min-width: 180px;");

        Label lblAcciones = sideLabel("ACCIONES");

        // Búsqueda
        Label lblBuscar = sideLabel("Buscar");
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Titular o nombre…");
        txtBuscar.setStyle("-fx-font-size: 12;");
        Button btnBuscar = sideButton("🔍  Buscar");
        btnBuscar.setOnAction(e -> buscarNoticias());

        Button btnListar = sideButton("📋  Listar todas");
        btnListar.setOnAction(e -> listarNoticias());

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #3949ab;");

        Label lblAuth = sideLabel("SESIÓN");
        btnLogin   = sideButton("🔑  Iniciar sesión");
        btnLogout  = sideButton("🚪  Cerrar sesión");
        btnLogin.setOnAction(e -> mostrarDialogoLogin());
        btnLogout.setOnAction(e -> cerrarSesion());

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #3949ab;");

        Label lblNoticias = sideLabel("NOTICIAS");
        btnPublicar  = sideButton("➕  Publicar");
        btnModificar = sideButton("✏️  Modificar");
        btnEliminar  = sideButton("🗑️  Eliminar");
        btnPublicar .setOnAction(e -> mostrarDialogoPublicar());
        btnModificar.setOnAction(e -> mostrarDialogoModificar());
        btnEliminar .setOnAction(e -> confirmarEliminar());

        Separator sep3 = new Separator();
        sep3.setStyle("-fx-background-color: #3949ab;");

        Label lblDB = sideLabel("BASE DE DATOS LOCAL");
        btnGuardarDB = sideButton("💾  Guardar JSON");
        btnCargarDB  = sideButton("📂  Cargar JSON");
        btnGuardarDB.setOnAction(e -> guardarEnDB());
        btnCargarDB .setOnAction(e -> cargarDesdeDB());

        side.getChildren().addAll(
            lblAcciones,
            lblBuscar, txtBuscar, btnBuscar, btnListar,
            sep1,
            lblAuth, btnLogin, btnLogout,
            sep2,
            lblNoticias, btnPublicar, btnModificar, btnEliminar,
            sep3,
            lblDB, btnGuardarDB, btnCargarDB
        );
        return side;
    }

    private SplitPane buildCenter() {
        // ── Tabla de noticias ──
        tabla = new TableView<>(noticiasList);
        tabla.setPlaceholder(new Label("No hay noticias cargadas."));
        tabla.setStyle("-fx-font-size: 13;");

        TableColumn<Noticia, String> colNombre = new TableColumn<>("Nombre único");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreUnico"));
        colNombre.setPrefWidth(190);

        TableColumn<Noticia, String> colTitular = new TableColumn<>("Titular");
        colTitular.setCellValueFactory(new PropertyValueFactory<>("titular"));
        colTitular.setPrefWidth(280);

        TableColumn<Noticia, String> colAutor = new TableColumn<>("Autor");
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAutor.setPrefWidth(100);

        TableColumn<Noticia, String> colFecha = new TableColumn<>("Creado");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        colFecha.setPrefWidth(140);

        tabla.getColumns().add(colNombre);
        tabla.getColumns().add(colTitular);
        tabla.getColumns().add(colAutor);
        tabla.getColumns().add(colFecha);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Al seleccionar una fila, mostrar detalle
        tabla.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, sel) -> mostrarDetalle(sel)
        );

        // ── Panel de detalle ──
        txtDetalle = new TextArea();
        txtDetalle.setEditable(false);
        txtDetalle.setWrapText(true);
        txtDetalle.setStyle("-fx-font-size: 13; -fx-font-family: 'Monospaced';");
        txtDetalle.setPromptText("Selecciona una noticia para ver su contenido…");

        VBox detallePanel = new VBox(6,
            new Label("Detalle de la noticia:"), txtDetalle);
        detallePanel.setPadding(new Insets(10));

        SplitPane split = new SplitPane(tabla, detallePanel);
        split.setDividerPositions(0.6);
        VBox.setVgrow(split, Priority.ALWAYS);
        return split;
    }

    private HBox buildFooter() {
        HBox foot = new HBox(10);
        foot.setPadding(new Insets(6, 14, 6, 14));
        foot.setAlignment(Pos.CENTER_LEFT);
        foot.setStyle("-fx-background-color: #e8eaf6;");

        lblDBRuta = new Label("DB local: " + db.getRuta());
        lblDBRuta.setStyle("-fx-text-fill: #555; -fx-font-size: 11;");

        Label lblCount = new Label();
        noticiasList.addListener((javafx.collections.ListChangeListener<Noticia>) c ->
            lblCount.setText("Noticias: " + noticiasList.size()));
        lblCount.setText("Noticias: 0");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        foot.getChildren().addAll(lblDBRuta, spacer, lblCount);
        return foot;
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS DE ESTILO
    // ══════════════════════════════════════════════════════════

    private Label sideLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 11));
        l.setTextFill(Color.LIGHTSTEELBLUE);
        l.setPadding(new Insets(8, 0, 2, 0));
        return l;
    }

    private Button sideButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("""
            -fx-background-color: #3949ab;
            -fx-text-fill: white;
            -fx-font-size: 12;
            -fx-border-radius: 4;
            -fx-background-radius: 4;
            -fx-padding: 7 10 7 10;
            """);
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle().replace("#3949ab", "#5c6bc0")));
        b.setOnMouseExited (e -> b.setStyle(b.getStyle().replace("#5c6bc0", "#3949ab")));
        return b;
    }

    // ══════════════════════════════════════════════════════════
    // LÓGICA DE NEGOCIO
    // ══════════════════════════════════════════════════════════

    private void listarNoticias() {
        if (!verificarConexion()) return;
        try {
            List<Noticia> lista = cliente.listarNoticias();
            noticiasList.setAll(lista != null ? lista : List.of());
        } catch (Exception e) {
            alertaError("Error al listar noticias", e.getMessage());
        }
    }

    private void buscarNoticias() {
        if (!verificarConexion()) return;
        String q = txtBuscar.getText().trim();
        if (q.isEmpty()) { listarNoticias(); return; }
        try {
            List<Noticia> res = cliente.buscarPorTitular(q);
            noticiasList.setAll(res != null ? res : List.of());
        } catch (Exception e) {
            alertaError("Error en búsqueda", e.getMessage());
        }
    }

    private void mostrarDialogoLogin() {
        if (token != null) {
            alertaInfo("Sesión activa", "Ya tienes sesión como '" + usuarioActual + "'. Ciérrala primero.");
            return;
        }
        Stage dlg = dialogoBase("Iniciar sesión");

        GridPane grid = formGrid();
        TextField fUser = new TextField();
        PasswordField fPass = new PasswordField();
        grid.addRow(0, new Label("Usuario:"),    fUser);
        grid.addRow(1, new Label("Contraseña:"), fPass);

        Button btnOk = new Button("Entrar");
        btnOk.setDefaultButton(true);
        btnOk.setStyle("-fx-background-color:#1a237e;-fx-text-fill:white;-fx-padding:6 18;");

        btnOk.setOnAction(e -> {
            if (!verificarConexion()) { dlg.close(); return; }
            try {
                String t = cliente.login(fUser.getText().trim(), fPass.getText());
                if (t == null) {
                    alertaError("Login fallido", "Usuario o contraseña incorrectos.");
                } else {
                    token = t;
                    usuarioActual = fUser.getText().trim();
                    actualizarBotonesSegunSesion();
                    dlg.close();
                }
            } catch (Exception ex) {
                alertaError("Error de conexión", ex.getMessage());
            }
        });

        VBox layout = new VBox(14, grid, btnOk);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        dlg.setScene(new Scene(layout, 300, 160));
        dlg.showAndWait();
    }

    private void cerrarSesion() {
        token = null;
        usuarioActual = null;
        actualizarBotonesSegunSesion();
        alertaInfo("Sesión cerrada", "Has cerrado sesión correctamente.");
    }

    private void mostrarDialogoPublicar() {
        if (!verificarSesion()) return;
        Stage dlg = dialogoBase("Publicar noticia");

        GridPane grid = formGrid();
        TextField fNombre   = new TextField();
        TextField fTitular  = new TextField();
        TextArea  fContenido = new TextArea();
        fContenido.setPrefRowCount(4);
        fContenido.setWrapText(true);

        grid.addRow(0, new Label("Nombre único:"), fNombre);
        grid.addRow(1, new Label("Titular:"),      fTitular);
        grid.addRow(2, new Label("Contenido:"),    fContenido);

        Button btnOk = accionBtn("Publicar");
        btnOk.setOnAction(e -> {
            try {
                String r = cliente.publicarNoticia(token,
                        fNombre.getText().trim(),
                        fTitular.getText().trim(),
                        fContenido.getText().trim());
                if (r.startsWith("OK")) { listarNoticias(); dlg.close(); }
                else alertaError("Error al publicar", r);
            } catch (Exception ex) { alertaError("Error RMI", ex.getMessage()); }
        });

        VBox layout = new VBox(14, grid, btnOk);
        layout.setPadding(new Insets(20));
        dlg.setScene(new Scene(layout, 400, 260));
        dlg.showAndWait();
    }

    private void mostrarDialogoModificar() {
        if (!verificarSesion()) return;
        Noticia sel = tabla.getSelectionModel().getSelectedItem();
        Stage dlg = dialogoBase("Modificar noticia");

        GridPane grid = formGrid();
        TextField fNombre    = new TextField(sel != null ? sel.getNombreUnico() : "");
        TextField fTitular   = new TextField(sel != null ? sel.getTitular() : "");
        TextArea  fContenido = new TextArea(sel != null ? sel.getContenido() : "");
        fContenido.setPrefRowCount(4);
        fContenido.setWrapText(true);

        grid.addRow(0, new Label("Nombre único:"),    fNombre);
        grid.addRow(1, new Label("Nuevo titular:"),   fTitular);
        grid.addRow(2, new Label("Nuevo contenido:"), fContenido);

        Button btnOk = accionBtn("Guardar cambios");
        btnOk.setOnAction(e -> {
            try {
                String r = cliente.modificarNoticia(token,
                        fNombre.getText().trim(),
                        fTitular.getText().trim(),
                        fContenido.getText().trim());
                if (r.startsWith("OK")) { listarNoticias(); dlg.close(); }
                else alertaError("Error al modificar", r);
            } catch (Exception ex) { alertaError("Error RMI", ex.getMessage()); }
        });

        VBox layout = new VBox(14, grid, btnOk);
        layout.setPadding(new Insets(20));
        dlg.setScene(new Scene(layout, 400, 280));
        dlg.showAndWait();
    }

    private void confirmarEliminar() {
        if (!verificarSesion()) return;
        Noticia sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alertaInfo("Sin selección", "Selecciona una noticia de la tabla."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar la noticia?");
        confirm.setContentText("\"" + sel.getTitular() + "\"");
        Optional<ButtonType> resp = confirm.showAndWait();
        if (resp.isPresent() && resp.get() == ButtonType.OK) {
            try {
                String r = cliente.eliminarNoticia(token, sel.getNombreUnico());
                if (r.startsWith("OK")) listarNoticias();
                else alertaError("Error al eliminar", r);
            } catch (Exception e) { alertaError("Error RMI", e.getMessage()); }
        }
    }

    // ══════════════════════════════════════════════════════════
    // BASE DE DATOS JSON
    // ══════════════════════════════════════════════════════════

    private void guardarEnDB() {
        if (noticiasList.isEmpty()) {
            alertaInfo("Sin datos", "No hay noticias en la tabla para guardar.");
            return;
        }
        db.guardarTodas(noticiasList);
        alertaInfo("Guardado", "Noticias guardadas en:\n" + db.getRuta());
    }

    private void cargarDesdeDB() {
        List<Noticia> cargadas = db.cargarTodas();
        if (cargadas.isEmpty()) {
            alertaInfo("Base de datos vacía", "No se encontraron noticias en el archivo JSON.");
            return;
        }
        noticiasList.setAll(cargadas);
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════

    private void mostrarDetalle(Noticia n) {
        if (n == null) { txtDetalle.clear(); return; }
        txtDetalle.setText(
            "Nombre único : " + n.getNombreUnico()              + "\n" +
            "Titular      : " + n.getTitular()                  + "\n" +
            "Autor        : " + n.getAutor()                    + "\n" +
            "Creado       : " + n.getFechaCreacion()            + "\n" +
            "Actualizado  : " + n.getUltimaFechaActualizacion() + "\n\n" +
            "── Contenido ────────────────────────────────\n" +
            n.getContenido()
        );
    }

    private void actualizarEstadoConexion() {
        if (conectado) {
            lblEstado.setText("● Conectado");
            lblEstado.setTextFill(Color.LIGHTGREEN);
        } else {
            lblEstado.setText("● Sin servidor");
            lblEstado.setTextFill(Color.TOMATO);
        }
    }

    private void actualizarBotonesSegunSesion() {
        boolean sesionActiva = token != null;
        btnLogin   .setDisable(sesionActiva);
        btnLogout  .setDisable(!sesionActiva);
        btnPublicar.setDisable(!sesionActiva);
        btnModificar.setDisable(!sesionActiva);
        btnEliminar.setDisable(!sesionActiva);

        if (sesionActiva) {
            lblSesion.setText("👤 " + usuarioActual);
            lblSesion.setTextFill(Color.LIGHTGREEN);
        } else {
            lblSesion.setText("Sin sesión");
            lblSesion.setTextFill(Color.LIGHTGRAY);
        }
    }

    private boolean verificarConexion() {
        if (!conectado || cliente == null) {
            alertaError("Sin conexión", "No hay conexión con el servidor RMI.\n" +
                "Puedes cargar noticias desde la base de datos local (JSON).");
            return false;
        }
        return true;
    }

    private boolean verificarSesion() {
        if (token == null) {
            alertaError("Sin sesión", "Debes iniciar sesión primero.");
            return false;
        }
        return true;
    }

    private Stage dialogoBase(String titulo) {
        Stage dlg = new Stage();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle(titulo);
        dlg.setResizable(false);
        return dlg;
    }

    private GridPane formGrid() {
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(0));
        ColumnConstraints c1 = new ColumnConstraints(110);
        ColumnConstraints c2 = new ColumnConstraints(200, 250, Double.MAX_VALUE);
        c2.setHgrow(Priority.ALWAYS);
        g.getColumnConstraints().addAll(c1, c2);
        return g;
    }

    private Button accionBtn(String texto) {
        Button b = new Button(texto);
        b.setDefaultButton(true);
        b.setStyle("-fx-background-color:#1a237e;-fx-text-fill:white;-fx-padding:6 18;");
        return b;
    }

    private void alertaError(String titulo, String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(titulo);
            a.setHeaderText(null);
            a.setContentText(msg);
            a.showAndWait();
        });
    }

    private void alertaInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
