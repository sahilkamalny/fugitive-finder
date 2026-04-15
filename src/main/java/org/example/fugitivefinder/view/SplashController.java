package org.example.fugitivefinder.view;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import org.example.fugitivefinder.viewModel.SplashViewModel;

public class SplashController {

    @FXML
    private ProgressBar loadingBar;

    private SplashViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new SplashViewModel();
        loadingBar.progressProperty().bind(viewModel.progressProperty());
    }

    @FXML
    private void goToLogin() {
        viewModel.goToLogin(loadingBar);
    }
}