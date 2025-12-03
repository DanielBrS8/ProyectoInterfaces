package com.javafx.proyecto;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class PruebaControlsFX {

    public static void main(String[] args) {
        ValidationSupport vs = new ValidationSupport();
        Validator<String> validador =
                Validator.createEmptyValidator("Campo obligatorio");

        System.out.println("OK - ControlsFX cargado correctamente: " + vs + " / " + validador);
    }
}
