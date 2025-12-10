package com.javafx.proyecto;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class ValidadorForms {

    // USUARIOS

    public static ValidationSupport validarNombreUsuario(TextField txtNombre) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtNombre, (Control c, String texto) -> {
            if (texto == null || texto.trim().isEmpty()) {
                return ValidationResult.fromWarning(c, "El nombre no debe estar vacío");
            } else if (texto.trim().length() < 3 || texto.trim().length() > 50) {
                return ValidationResult.fromError(c, "El nombre debe tener entre 3 y 50 caracteres");
            } else {
                return ValidationResult.fromInfo(c, "Nombre válido");
            }
        });
        return vs;
    }

    public static ValidationSupport validarEmailUsuario(TextField txtEmail) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtEmail, (Control c, String texto) -> {
            if (texto == null || texto.trim().isEmpty()) {
                return ValidationResult.fromError(c, "El email es obligatorio");
            } else if (!texto.matches("^(.+)@(.+)\\.(.+)$")) {
                return ValidationResult.fromError(c, "El formato del email debe ser: texto@texto.texto");
            } else {
                return ValidationResult.fromInfo(c, "Email válido");
            }
        });
        return vs;
    }

    public static ValidationSupport validarTelefonoUsuario(TextField txtTelefono) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtTelefono, (Control c, String t) -> {
            if (t == null || t.trim().isEmpty()) {
                return ValidationResult.fromError(c, "El teléfono es obligatorio");
            } else if (!t.matches("\\d{9}")) {
                return ValidationResult.fromError(c, "El teléfono debe tener 9 dígitos numéricos");
            } else {
                return ValidationResult.fromInfo(c, "Teléfono válido");
            }
        });
        return vs;
    }

    public static ValidationSupport validarCampoObligatorio(TextField txtField, String nombreCampo) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtField, (Control c, String texto) -> {
            if (texto == null || texto.trim().isEmpty()) {
                return ValidationResult.fromError(c, nombreCampo + " es obligatorio");
            } else {
                return ValidationResult.fromInfo(c, nombreCampo + " válido");
            }
        });
        return vs;
    }

    // MASCOTAS

    public static ValidationSupport validarPesoMascota(TextField txtPeso) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtPeso, (Control c, String t) -> {
            if (t == null || t.isBlank()) {
                return ValidationResult.fromInfo(c, "Peso opcional");
            }
            try {
                double peso = Double.parseDouble(t.replace(",", "."));
                if (peso <= 0) {
                    return ValidationResult.fromError(c, "El peso debe ser un número positivo");
                }
                return ValidationResult.fromInfo(c, "Peso válido");
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "El peso debe ser un número (usa . como separador)");
            }
        });
        return vs;
    }

    public static ValidationSupport validarEspecieMascota(TextField txtEspecie) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtEspecie, (Control c, String texto) -> {
            if (texto == null || texto.trim().isEmpty()) {
                return ValidationResult.fromError(c, "La especie es obligatoria");
            } else {
                return ValidationResult.fromInfo(c, "Especie válida");
            }
        });
        return vs;
    }

    public static ValidationSupport validarRazaMascota(TextField txtRaza) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtRaza, (Control c, String texto) -> {
            if (texto == null || texto.trim().isEmpty()) {
                return ValidationResult.fromError(c, "La raza es obligatoria");
            } else {
                return ValidationResult.fromInfo(c, "Raza válida");
            }
        });
        return vs;
    }

    public static ValidationSupport validarEstadoSaludMascota(TextField txtEstado) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtEstado, (Control c, String texto) -> {
            if (texto == null || texto.trim().isEmpty()) {
                return ValidationResult.fromError(c, "El estado de salud es obligatorio");
            } else {
                return ValidationResult.fromInfo(c, "Estado válido");
            }
        });
        return vs;
    }

    // ---------- ADOPCIONES ----------

    public static ValidationSupport validarEstadoAdopcion(TextField txtEstado) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtEstado, (Control c, String texto) -> {
            if (texto == null || texto.trim().isEmpty()) {
                return ValidationResult.fromError(c, "El estado es obligatorio");
            } else {
                return ValidationResult.fromInfo(c, "Estado válido");
            }
        });
        return vs;
    }

    public static ValidationSupport validarCalificacionAdopcion(TextField txtCalif) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(txtCalif, (Control c, String t) -> {
            if (t == null || t.isBlank()) {
                return ValidationResult.fromInfo(c, "Calificación opcional");
            }
            try {
                int calif = Integer.parseInt(t);
                if (calif < 1 || calif > 5) {
                    return ValidationResult.fromError(c, "La calificación debe estar entre 1 y 5");
                }
                return ValidationResult.fromInfo(c, "Calificación válida");
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "La calificación debe ser un número");
            }
        });
        return vs;
    }

    /**
     * Valida que la fecha fin sea mayor o igual que la fecha de inicio.
     */
    public static ValidationSupport validarRangoFechasAdopcion(DatePicker dpInicio, DatePicker dpFin) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(dpFin, (Control c, LocalDate ff) -> {
            LocalDate fi = dpInicio.getValue();
            if (fi == null || ff == null) {
                return ValidationResult.fromInfo(c, "Fechas opcionales");
            }
            if (ff.isBefore(fi)) {
                return ValidationResult.fromError(c, "La fecha fin debe ser >= a la fecha inicio");
            }
            return ValidationResult.fromInfo(c, "Rango de fechas válido");
        });
        return vs;
    }

    /**
     * Valida que el ComboBox tenga un valor seleccionado.
     */
    public static ValidationSupport validarComboBoxObligatorio(ComboBox<?> combo, String nombreCampo) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(combo, (Control c, Object valor) -> {
            if (valor == null) {
                return ValidationResult.fromError(c, "Debes seleccionar " + nombreCampo);
            }
            return ValidationResult.fromInfo(c, nombreCampo + " seleccionado");
        });
        return vs;
    }

    /**
     * Valida que el CheckBox esté marcado (aceptado).
     */
    public static ValidationSupport validarCheckBoxAceptado(CheckBox check, String nombreCampo) {
        ValidationSupport vs = new ValidationSupport();
        vs.setErrorDecorationEnabled(true);

        vs.registerValidator(check, true, (Control c, Boolean v) -> {
            if (Boolean.FALSE.equals(v)) {
                return ValidationResult.fromWarning(c, "Debes aceptar " + nombreCampo);
            } else {
                return ValidationResult.fromInfo(c, nombreCampo + " aceptado");
            }
        });
        return vs;
    }
}
