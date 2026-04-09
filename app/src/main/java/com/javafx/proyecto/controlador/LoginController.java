package com.javafx.proyecto.controlador;

import com.javafx.proyecto.bbdd.PawLinkClient;
import com.javafx.proyecto.util.SesionUsuario;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;

public class LoginController {

    private static final String GOOGLE_CLIENT_ID = "164559908013-6mh8k2a5kc9729ou6ahvc0gpk60g8otr.apps.googleusercontent.com";

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblError;
    @FXML
    private Button btnEntrar;
    @FXML
    private Button btnGoogle;
    @FXML
    private VBox loginContainer;
    @FXML
    private GridPane formGrid;

    @FXML
    private void initialize() {
        aplicarAnimacionEntrada();

        txtEmail.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txtEmail.getStyleClass().remove("error");
            }
        });

        txtPassword.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txtPassword.getStyleClass().remove("error");
            }
        });
    }

    private void aplicarAnimacionEntrada() {
        if (loginContainer != null) {
            loginContainer.setOpacity(0);
            loginContainer.setTranslateY(20);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), loginContainer);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition slideUp = new TranslateTransition(Duration.millis(800), loginContainer);
            slideUp.setFromY(20);
            slideUp.setToY(0);

            ParallelTransition entrada = new ParallelTransition(fadeIn, slideUp);
            entrada.setDelay(Duration.millis(100));
            entrada.play();
        }
    }

    @FXML
    private void onEntrar() {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        lblError.setText("");
        txtEmail.getStyleClass().remove("error");
        txtPassword.getStyleClass().remove("error");

        if (email.isEmpty() || password.isEmpty()) {
            lblError.setText("Completa todos los campos");
            animarError();
            if (email.isEmpty()) txtEmail.getStyleClass().add("error");
            if (password.isEmpty()) txtPassword.getStyleClass().add("error");
            return;
        }

        try {
            Map<String, Object> respuesta = PawLinkClient.login(email, password);
            String rol = respuesta.get("rol") != null ? respuesta.get("rol").toString() : "";
            if ("user".equalsIgnoreCase(rol)) {
                lblError.setText("Acceso denegado. Esta aplicación es solo para personal del centro.");
                animarError();
                return;
            }
            SesionUsuario.getInstancia().iniciarSesion(respuesta);
            animarExito();
        } catch (RuntimeException e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.contains("401")) {
                lblError.setText("Credenciales inválidas");
            } else if (mensaje != null && mensaje.contains("403")) {
                lblError.setText("Acceso solo para administradores y veterinarios");
            } else {
                lblError.setText("Error de conexión con el servidor");
            }
            txtEmail.getStyleClass().add("error");
            txtPassword.getStyleClass().add("error");
            animarError();
        } catch (Exception e) {
            lblError.setText("Error de conexión con el servidor");
            animarError();
        }
    }

    @FXML
    private void onGoogle() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(btnGoogle.getScene().getWindow());
        dialogStage.setTitle("Iniciar sesión con Google");

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + GOOGLE_CLIENT_ID
                + "&redirect_uri=urn:ietf:wg:oauth:2.0:oob"
                + "&response_type=code"
                + "&scope=openid%20email%20profile";

        webEngine.titleProperty().addListener((obs, oldTitle, newTitle) -> {
            if (newTitle != null && newTitle.startsWith("Success code=")) {
                String code = newTitle.substring("Success code=".length()).trim();
                dialogStage.close();
                procesarLoginGoogle(code);
            }
        });

        webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                String location = webEngine.getLocation();
                if (location == null || !location.contains("approvalCode")) return;

                String code = (String) webEngine.executeScript(
                        "(function() {"
                        + "  var el = document.getElementById('code');"
                        + "  if (el) return (el.value || el.textContent || '').trim();"
                        + "  var title = document.title || '';"
                        + "  if (title.indexOf('code=') !== -1) return title.split('code=')[1].trim();"
                        + "  return null;"
                        + "})()"
                );
                if (code != null && !code.isEmpty() && code.startsWith("4/")) {
                    dialogStage.close();
                    procesarLoginGoogle(code);
                }
            }
        });

        webEngine.load(authUrl);

        Scene scene = new Scene(webView, 500, 600);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void procesarLoginGoogle(String code) {
        try {
            if (code.contains("&")) {
                code = code.substring(0, code.indexOf('&'));
            }
            Map<String, Object> respuesta = PawLinkClient.loginGoogle(code);
            String rol = respuesta.get("rol") != null ? respuesta.get("rol").toString() : "";
            if ("user".equalsIgnoreCase(rol)) {
                lblError.setText("Acceso denegado. Esta aplicación es solo para personal del centro.");
                animarError();
                return;
            }
            SesionUsuario.getInstancia().iniciarSesion(respuesta);
            animarExito();
        } catch (RuntimeException e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.contains("401")) {
                lblError.setText("Usuario no autorizado");
            } else if (mensaje != null && mensaje.contains("403")) {
                lblError.setText("Acceso solo para administradores y veterinarios");
            } else {
                lblError.setText("Error de conexión con el servidor");
            }
            animarError();
        } catch (Exception e) {
            lblError.setText("Error de conexión con el servidor");
            animarError();
        }
    }

    private void animarError() {
        if (formGrid == null) return;

        TranslateTransition shake = new TranslateTransition(Duration.millis(50), formGrid);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);

        FadeTransition errorFade = new FadeTransition(Duration.millis(300), lblError);
        errorFade.setFromValue(0);
        errorFade.setToValue(1);

        ParallelTransition errorAnimation = new ParallelTransition(shake, errorFade);
        errorAnimation.play();
    }

    private void animarExito() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnEntrar);
        scale.setToX(0.95);
        scale.setToY(0.95);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        scale.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), loginContainer);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> cargarVentanaPrincipal());
            fadeOut.play();
        });

        scale.play();
    }

    private void cargarVentanaPrincipal() {
        try {
            Stage stage = (Stage) btnEntrar.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Principal.fxml"));
            Scene scene = new Scene(loader.load());

            stage.setTitle("Gestor Veterinario");
            stage.getIcons().clear();
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/miapp/icons/paw.png")));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            lblError.setText("Error cargando la ventana principal");
        }
    }
}
