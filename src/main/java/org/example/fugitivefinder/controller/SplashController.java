package org.example.fugitivefinder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import org.example.fugitivefinder.viewModel.SceneManager;

public class SplashController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ProgressBar loadingBar;

    @FXML
    public void initialize() {
        if (loadingBar != null) {
            loadingBar.setProgress(0);
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> loadingBar.setProgress(1.0));
            pause.play();
        }
    }

    @FXML
    private void goToLogin() {
        SceneManager.switchScene(
                rootPane,
                "/org.example.fugitivefinder/login.fxml",
                1440,
                900
        );
    }
}