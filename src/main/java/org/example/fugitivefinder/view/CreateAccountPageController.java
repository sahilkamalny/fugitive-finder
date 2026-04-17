package org.example.fugitivefinder.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.fugitivefinder.viewModel.CreateAccountViewModel;

public class CreateAccountPageController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private CreateAccountViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new CreateAccountViewModel();

        firstNameField.textProperty().bindBidirectional(viewModel.firstNameProperty());
        lastNameField.textProperty().bindBidirectional(viewModel.lastNameProperty());
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        confirmPasswordField.textProperty().bindBidirectional(viewModel.confirmPasswordProperty());
    }

    @FXML
    private void handleCreateAccount() {
        boolean success = viewModel.createAccount(firstNameField);
        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Account Creation Failed");
            alert.setContentText("Check the fields, make sure passwords match, and use a new email.");
            alert.showAndWait();
        }
    }

    @FXML
    private void returnToLogin() {
        viewModel.returnToLogin(firstNameField);
    }
}