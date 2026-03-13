package com.template;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {

            URL fxmlLocation = Main.class.getResource("/com/template/LoginUI.fxml");

            if (fxmlLocation == null) {
                System.err.println(" FXML file not found!");

            }

            // 2. Load the FXML
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            Parent root = fxmlLoader.load();

            // 3. Show the Scene
            Scene scene = new Scene(root, 1000, 720);
            stage.setTitle("TAK LIMITED - Flat Rental & Logistics");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            System.out.println("✅ Application Started Successfully!");

        } catch (Exception e) {
            System.err.println("❌ CRASH: An error occurred while starting the app.");
            e.printStackTrace(); // This prints the detailed error in the console
        }
    }

    public static void main(String[] args) {
        launch();
    }
}