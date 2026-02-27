package com.javafx.proyecto.util;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Supplier;

public class UIUtils {

    private static boolean cambiandoProgramaticamente = false;

    public static void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        añadirIconoADialogo(alerta);
        alerta.showAndWait();
    }

    public static void añadirIconoADialogo(Dialog<?> dialogo) {
        Stage stage = (Stage) dialogo.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(UIUtils.class.getResourceAsStream("/miapp/icons/paw.png")));
    }

    public static GridPane crearGridBasico() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setStyle("-fx-background-color: #f9f9f9; -fx-alignment: center;");

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        col1.setPrefWidth(120);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(250);
        col2.setPrefWidth(300);
        col2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }

    public static ImageView crearIcono(String ruta, int tamaño) {
        try {
            ImageView iv = new ImageView(new Image(UIUtils.class.getResourceAsStream(ruta)));
            iv.setFitWidth(tamaño);
            iv.setFitHeight(tamaño);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            return null;
        }
    }

    public static void configurarHoverBoton(Button btn) {
        if (btn == null) return;

        btn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    public static void aplicarAnimacionEntrada(StackPane stackContenido) {
        if (stackContenido != null) {
            stackContenido.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(600), stackContenido);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
    }

    public static <T> void configurarAutocompletado(ComboBox<T> combo, ObservableList<T> items) {
        FilteredList<T> filteredItems = new FilteredList<>(items, p -> true);
        combo.setItems(filteredItems);
        combo.setEditable(true);

        combo.setConverter(new javafx.util.StringConverter<T>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public T fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                return items.stream()
                        .filter(item -> item.toString().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        TextField editor = combo.getEditor();

        editor.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });

        editor.textProperty().addListener((obs, oldValue, newValue) -> {
            if (cambiandoProgramaticamente) return;

            final T selected = combo.getSelectionModel().getSelectedItem();

            if (newValue == null || newValue.trim().isEmpty()) {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p -> true);
                    combo.hide();
                } finally {
                    cambiandoProgramaticamente = false;
                }
                return;
            }

            if (selected == null || !selected.toString().equals(newValue)) {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p ->
                        p.toString().toLowerCase().startsWith(newValue.toLowerCase().trim()));
                    combo.setVisibleRowCount(5);
                    combo.show();
                } finally {
                    cambiandoProgramaticamente = false;
                }
            } else {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p -> true);
                } finally {
                    cambiandoProgramaticamente = false;
                }
            }
        });

        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (cambiandoProgramaticamente) return;
            if (newValue != null) {
                Platform.runLater(() -> {
                    try {
                        cambiandoProgramaticamente = true;
                        editor.positionCaret(editor.getText().length());
                    } finally {
                        cambiandoProgramaticamente = false;
                    }
                });
            }
        });
    }

    public static void configurarBuscadorConAutocompletado(ComboBox<String> combo,
            ObservableList<String> items,
            Supplier<List<String>> dataSupplier) {

        items.setAll(dataSupplier.get());

        FilteredList<String> filteredItems = new FilteredList<>(items, p -> true);
        combo.setItems(filteredItems);
        combo.setEditable(true);

        TextField editor = combo.getEditor();

        editor.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(" ")) {
                event.consume();
            }
        });

        editor.textProperty().addListener((obs, oldValue, newValue) -> {
            if (cambiandoProgramaticamente) return;

            final String selected = combo.getSelectionModel().getSelectedItem();

            if (newValue == null || newValue.trim().isEmpty()) {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p -> true);
                    combo.hide();
                } finally {
                    cambiandoProgramaticamente = false;
                }
                return;
            }

            if (selected == null || !selected.equals(newValue)) {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p ->
                        p.toLowerCase().startsWith(newValue.toLowerCase().trim()));
                    combo.setVisibleRowCount(5);
                    combo.show();
                } finally {
                    cambiandoProgramaticamente = false;
                }
            } else {
                try {
                    cambiandoProgramaticamente = true;
                    filteredItems.setPredicate(p -> true);
                } finally {
                    cambiandoProgramaticamente = false;
                }
            }
        });

        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (cambiandoProgramaticamente) return;
            if (newValue != null) {
                Platform.runLater(() -> {
                    try {
                        cambiandoProgramaticamente = true;
                        editor.positionCaret(editor.getText().length());
                    } finally {
                        cambiandoProgramaticamente = false;
                    }
                });
            }
        });
    }

    public static void mostrarErrorConexion(Label... labels) {
        for (Label lbl : labels) {
            if (lbl != null) {
                lbl.setVisible(true);
                lbl.setManaged(true);
            }
        }
    }

    public static void ocultarErrorConexion(Label... labels) {
        for (Label lbl : labels) {
            if (lbl != null) {
                lbl.setVisible(false);
                lbl.setManaged(false);
            }
        }
    }
}
