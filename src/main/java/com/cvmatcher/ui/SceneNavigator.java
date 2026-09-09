package com.cvmatcher.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Small helper so controllers can switch views without duplicating FXMLLoader boilerplate. */
public final class SceneNavigator {

    private SceneNavigator() {}

    public static void switchTo(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
    }
}
