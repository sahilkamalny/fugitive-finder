package org.example.fugitivefinder.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/org.example.fugitivefinder/dashboard.fxml")
        );

        Scene scene = new Scene(loader.load(), 1440, 900);
        stage.setTitle("Fugitive Finder");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}