package com.javafx.proyecto;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblError;
    @FXML
    private Button btnEntrar;
    @FXML
    private VBox loginContainer;
    @FXML
    private GridPane formGrid;

    @FXML
    private void initialize() {
        aplicarAnimacionEntrada();

        txtUsuario.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txtUsuario.getStyleClass().remove("error");
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
        String u = txtUsuario.getText();
        String p = txtPassword.getText();

        lblError.setText("");
        txtUsuario.getStyleClass().remove("error");
        txtPassword.getStyleClass().remove("error");

        if (u.isEmpty() || p.isEmpty()) {
            lblError.setText("Completa todos los campos");
            animarError();
            if (u.isEmpty()) txtUsuario.getStyleClass().add("error");
            if (p.isEmpty()) txtPassword.getStyleClass().add("error");
            return;
        }

        if ("admin".equals(u) && "admin".equals(p)) {
            animarExito();
        } else {
            lblError.setText("Usuario o contraseña incorrectos");
            txtUsuario.getStyleClass().add("error");
            txtPassword.getStyleClass().add("error");
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
