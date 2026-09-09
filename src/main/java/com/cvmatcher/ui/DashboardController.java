package com.cvmatcher.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Button manageUsersButton;
    @FXML private Button updateNoticeButton;

    @FXML
    public void initialize() {
        var user = SessionContext.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
            boolean isAdmin = user.isAdmin();
            manageUsersButton.setVisible(isAdmin);
            manageUsersButton.setManaged(isAdmin);
            updateNoticeButton.setVisible(isAdmin);
            updateNoticeButton.setManaged(isAdmin);
        }
    }

    @FXML
    private void onBuildCv(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/cv_builder.fxml", "My CV");
    }

    @FXML
    private void onUploadNotice(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/upload_notice.fxml", "Upload job notice");
    }

    @FXML
    private void onUpdateNotice(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/notice_list.fxml", "Update job notice");
    }

    @FXML
    private void onViewNotices(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/notice_list.fxml", "Job notices");
    }

    @FXML
    private void onMyMatches(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/matches.fxml", "My matches");
    }

    @FXML
    private void onJobAvailability(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/job_positions.fxml", "Job position availability");
    }

    @FXML
    private void onManageUsers(ActionEvent event) {
        navigate(event, "/com/cvmatcher/ui/user_management.fxml", "Registered users");
    }

    @FXML
    private void onExit(ActionEvent event) {
        try {
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/role_selection.fxml", "CV Job Matcher");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not exit: " + e.getMessage()).showAndWait();
        }
    }

    private void navigate(ActionEvent event, String fxml, String title) {
        try {
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            SceneNavigator.switchTo(stage, fxml, title);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not open screen: " + e.getMessage()).showAndWait();
        }
    }
}
