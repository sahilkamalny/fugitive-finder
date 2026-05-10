package org.example.fugitivefinder.viewModel;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;

public final class SceneManager {

    private SceneManager() {
    }

    public static void switchScene(Node sourceNode, String fxmlPath, double width, double height) {
        System.out.println("====== SCENEMANAGER TRIGGERED ======");
        System.out.println("Attempting to load: " + fxmlPath);
        System.out.println("Source Node: " + sourceNode);
        try {

            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath)
            );

            if (loader.getLocation() == null) {
                System.out.println("FXML NOT FOUND: " + fxmlPath);
                return;
            }

            javafx.scene.Parent root = loader.load();
            Scene currentScene = sourceNode.getScene();
            
            // Preserve window dimensions and fullscreen state by swapping the root node
            currentScene.setRoot(root);

            // Add smooth fade-in effect
            root.setOpacity(0);
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(350), root);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

        } catch (Exception e) {
            System.out.println("SCENE SWITCH FAILED");
            e.printStackTrace();
        }
    }
}
