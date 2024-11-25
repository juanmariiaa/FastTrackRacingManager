package org.juanmariiaa.utils;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utils {

    public static Alert showPopUp(String title, String header, String text, Alert.AlertType type) {
        Alert alertDialog = new Alert(type);
        alertDialog.setTitle(title);
        alertDialog.setHeaderText(header);
        alertDialog.setContentText(text);

        alertDialog.show();

        Stage s = (Stage) alertDialog.getDialogPane().getScene().getWindow();
        s.toFront();

        return alertDialog;
    }


    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

