package com.javafx.ejercicio4;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
    private void initialize() {

    }

    @FXML
    private void onEntrar() {
        String u = txtUsuario.getText();
        String p = txtPassword.getText();

        if ("admin".equals(u) && "admin".equals(p)) {
            cargarVentanaPrincipal();
        } else {
            lblError.setText("Usuario o contraseña incorrectos");
        }
    }

    private void cargarVentanaPrincipal() {
        try {
            Stage stage = (Stage) btnEntrar.getScene().getWindow();

            // Ventana principal: Principal.fxml en src/main/resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Principal.fxml"));
            Scene scene = new Scene(loader.load());

            stage.setTitle("Gestor Veterinario");
            stage.getIcons().clear();
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/miapp/icons/paw.png")));
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            lblError.setText("Error cargando la ventana principal");
        }
    }

}
