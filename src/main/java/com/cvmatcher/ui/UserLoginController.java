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

public class UserLoginController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserDao userDao = new SqliteUserDao();
    private final AuthService authService = new AuthService(userDao);

    @FXML
    private void onLogin(ActionEvent event) {
        Optional<User> user = authService.login(nameField.getText().trim(), phoneField.getText().trim(), passwordField.getText());
        if (user.isEmpty()) {
            statusLabel.setText("Invalid name, phone number, or password.");
            return;
        }
        if (user.get().isAdmin()) {
            // Safety net: admin accounts must go through the dedicated admin login screen.
            statusLabel.setText("This account is an admin account. Use the Admin login screen.");
            return;
        }
        SessionContext.setCurrentUser(user.get());
        goToDashboard();
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) phoneField.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/user_entry.fxml", "CV Job Matcher - User");
        } catch (IOException e) {
            statusLabel.setText("Could not go back: " + e.getMessage());
        }
    }

    private void goToDashboard() {
        try {
            Stage stage = (Stage) phoneField.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Dashboard");
        } catch (IOException e) {
            statusLabel.setText("Could not open dashboard: " + e.getMessage());
        }
    }
}
