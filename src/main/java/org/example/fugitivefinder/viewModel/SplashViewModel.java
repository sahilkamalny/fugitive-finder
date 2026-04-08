package org.example.fugitivefinder.viewModel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import org.example.fugitivefinder.viewModel.SceneManager;

public class SplashViewModel {

    private final DoubleProperty progress = new SimpleDoubleProperty(0.35);

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void goToLogin(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/login.fxml", 1440, 900);
    }
}