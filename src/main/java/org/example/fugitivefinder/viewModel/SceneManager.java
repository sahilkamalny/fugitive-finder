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
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath)
            );
            Scene scene = new Scene(loader.load(), width, height);
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
