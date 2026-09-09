package com.cvmatcher.ui;

import com.cvmatcher.dao.SqliteUserDao;
import com.cvmatcher.dao.UserDao;
import com.cvmatcher.model.User;
import com.cvmatcher.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Admin has no signup path here -- only login, against the single fixed
 * admin account that Main seeds at startup.
 */
public class AdminLoginController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserDao userDao = new SqliteUserDao();
    private final AuthService authService = new AuthService(userDao);

    @FXML
    private void onLogin(ActionEvent event) {
        Optional<User> admin = authService.loginAsAdmin(nameField.getText().trim(), phoneField.getText().trim(), passwordField.getText());
        if (admin.isEmpty()) {
            statusLabel.setText("Invalid admin name, phone number, or password.");
            return;
        }
        SessionContext.setCurrentUser(admin.get());
        try {
            Stage stage = (Stage) phoneField.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Admin dashboard");
        } catch (IOException e) {
            statusLabel.setText("Could not open dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) phoneField.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/role_selection.fxml", "CV Job Matcher");
        } catch (IOException e) {
            statusLabel.setText("Could not go back: " + e.getMessage());
        }
    }
}
