package com.cvmatcher.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Entry screen: the person picks a role before authenticating.
 * "User" leads to a sign-in-or-sign-up choice; "Admin" leads straight to a
 * phone+password login against the single fixed admin account.
 */
public class RoleSelectionController {

    @FXML private Label titleLabel;

    @FXML
    private void onChooseUser(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/user_entry.fxml", "CV Job Matcher - User");
    }

    @FXML
    private void onChooseAdmin(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/admin_login.fxml", "CV Job Matcher - Admin login");
    }

    @FXML
    private void onExit(ActionEvent event) {
        javafx.application.Platform.exit();
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
