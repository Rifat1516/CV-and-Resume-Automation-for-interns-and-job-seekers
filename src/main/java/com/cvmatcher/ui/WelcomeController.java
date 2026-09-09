package com.cvmatcher.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/** First screen shown on launch: a landing/splash screen with Continue or Exit. */
public class WelcomeController {

    @FXML private Label titleLabel;

    @FXML
    private void onContinue(ActionEvent event) {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/role_selection.fxml", "CV Job Matcher");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not continue: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onExit(ActionEvent event) {
        Platform.exit();
    }
}
