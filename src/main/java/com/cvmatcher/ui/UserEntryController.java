package com.cvmatcher.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/** "Already have an account? Sign in" vs "No account? Sign up" for regular users. */
public class UserEntryController {

    @FXML private Label titleLabel;

    @FXML
    private void onSignIn(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/user_login.fxml", "CV Job Matcher - Sign in");
    }

    @FXML
    private void onSignUp(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/user_signup.fxml", "CV Job Matcher - Sign up");
    }

    @FXML
    private void onBack(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/role_selection.fxml", "CV Job Matcher");
    }

    private void navigate(ActionEvent event, String fxml, String title) {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            SceneNavigator.switchTo(stage, fxml, title);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not open screen: " + e.getMessage()).showAndWait();
        }
    }
}
