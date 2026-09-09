package com.cvmatcher;

import com.cvmatcher.dao.DatabaseManager;
import com.cvmatcher.dao.SqliteUserDao;
import com.cvmatcher.dao.UserDao;
import com.cvmatcher.service.AuthService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String ADMIN_PHONE = "01719602096";
    private static final String ADMIN_NAME = "Irfan";
    private static final String ADMIN_PASSWORD = "12345678";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Ensure schema exists and the fixed admin account is available on first run.
        DatabaseManager.getInstance();
        seedFixedAdmin();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cvmatcher/ui/welcome.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("CV Job Matcher");
        primaryStage.setScene(new Scene(root, 480, 400));
        primaryStage.show();
    }

    /**
     * The admin account is fixed, not self-registered: there is exactly one
     * admin row, seeded once on first run with the credentials above.
     * Regular users can only ever sign up with role USER (see
     * AuthService.registerUser, used by UserSignupController) -- there is
     * no UI path that lets someone register themselves as admin.
     */
    private void seedFixedAdmin() {
        UserDao userDao = new SqliteUserDao();
        new AuthService(userDao).registerFixedAdmin(ADMIN_NAME, ADMIN_PHONE, ADMIN_PASSWORD);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
