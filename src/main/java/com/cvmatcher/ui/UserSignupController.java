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

public class UserSignupController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserDao userDao = new SqliteUserDao();
    private final AuthService authService = new AuthService(userDao);

    @FXML
    private void onSignUp(ActionEvent event) {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();

        if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Name, phone number, and password are required.");
            return;
        }

        try {
            User user = authService.registerUser(name, phone, password, null, null);
            SessionContext.setCurrentUser(user);
            goToDashboard();
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/user_entry.fxml", "CV Job Matcher - User");
        } catch (IOException e) {
            statusLabel.setText("Could not go back: " + e.getMessage());
        }
    }

    private void goToDashboard() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Dashboard");
        } catch (IOException e) {
            statusLabel.setText("Could not open dashboard: " + e.getMessage());
        }
    }
}
