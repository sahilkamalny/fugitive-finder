package org.example.fugitivefinder.view;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.fugitivefinder.viewModel.SceneManager;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.switchScene("LoginView.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
