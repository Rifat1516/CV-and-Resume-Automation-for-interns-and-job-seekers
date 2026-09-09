package com.cvmatcher.ui;

import com.cvmatcher.dao.SqliteUserDao;
import com.cvmatcher.dao.UserDao;
import com.cvmatcher.model.User;
import com.cvmatcher.service.UserDirectoryService;
import com.cvmatcher.service.UserDirectoryServiceImpl;
import com.cvmatcher.service.UserDirectoryServiceProxy;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Only ever talks to UserDirectoryService through its Proxy -- if a
 * non-admin somehow reaches this screen, the proxy throws rather than
 * silently returning data. Password hashes are never fetched into a
 * column here, so there is no accidental exposure even to the admin.
 */
public class UserManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, Integer> ageColumn;
    @FXML private TableColumn<User, String> cityColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private Label statusLabel;

    private final UserDao userDao = new SqliteUserDao();

    @FXML
    public void initialize() {
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        User currentUser = SessionContext.getCurrentUser();
        UserDirectoryService realService = new UserDirectoryServiceImpl(userDao);
        UserDirectoryService directory = new UserDirectoryServiceProxy(realService, currentUser);

        try {
            List<User> users = directory.listAllUsers();
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (SecurityException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) userTable.getScene().getWindow();
            SceneNavigator.switchTo(stage, "/com/cvmatcher/ui/dashboard.fxml", "CV Job Matcher - Dashboard");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not go back: " + e.getMessage()).showAndWait();
        }
    }
}
