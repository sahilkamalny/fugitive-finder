package org.example.fugitivefinder.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.fugitivefinder.viewModel.LoginViewModel;

public class LoginPageController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private LoginViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new LoginViewModel();

        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
    }

    @FXML
    private void handleLogin() {
        boolean success = viewModel.login(emailField);
        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Login Failed");
            alert.setContentText("Create an account first, then sign in.");
            alert.showAndWait();
        }
    }

    @FXML
    private void goToCreateAccount() {
        viewModel.goToCreateAccount(emailField);
    }
}