#!/bin/bash
cd "$(dirname "$0")"
java -cp "lib/*" --module-path lib --add-modules javafx.base,javafx.controls,javafx.graphics,javafx.fxml,javafx.web,javafx.swing,javafx.media com.javafx.proyecto.Main
