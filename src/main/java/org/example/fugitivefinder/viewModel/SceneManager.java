package org.example.fugitivefinder.viewModel;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneManager {

    private SceneManager() {
    }

    public static void switchScene(Node sourceNode, String fxmlPath, double width, double height) {
        try {
            System.out.println("Switching to: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath)
            );

            if (loader.getLocation() == null) {
                System.out.println("FXML NOT FOUND: " + fxmlPath);
                return;
            }

            Scene scene = new Scene(loader.load(), width, height);
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("SCENE SWITCH FAILED");
            e.printStackTrace();
        }
    }
}
