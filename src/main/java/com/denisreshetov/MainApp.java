package com.denisreshetov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;  // Import statement for Image

import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.Taskbar.Feature;

public class MainApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Set the icon for the stage
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));

        //Set icon on the taskbar/dock
        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();

            if (taskbar.isSupported(Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                var dockIcon = defaultToolkit.getImage(getClass().getResource("/images/icon.png"));
                taskbar.setIconImage(dockIcon);
            }

        }

        stage.setScene(scene);
        stage.show();

    }

    public static void showProjectName(String title) {
        if (primaryStage != null) {
            primaryStage.setTitle("Hindlimb Clasping Analysis" + ": " + title);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}