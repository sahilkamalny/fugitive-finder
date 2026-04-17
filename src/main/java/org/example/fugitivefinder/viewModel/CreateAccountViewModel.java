package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.service.UserService;
import org.example.fugitivefinder.session.Session;
import org.example.fugitivefinder.viewModel.SceneManager;

public class CreateAccountViewModel {

    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty confirmPassword = new SimpleStringProperty("");

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty confirmPasswordProperty() {
        return confirmPassword;
    }

    public boolean createAccount(Node sourceNode) {
        if (firstName.get().isBlank() || lastName.get().isBlank() || username.get().isBlank()
                || email.get().isBlank() || password.get().isBlank() || confirmPassword.get().isBlank()) {
            return false;
        }

        if (!password.get().equals(confirmPassword.get())) {
            return false;
        }

        boolean success = UserService.createAccount(
                firstName.get(),
                lastName.get(),
                username.get(),
                email.get(),
                password.get()
        );

        if (!success) {
            return false;
        }

        // For now (temporary until we fetch real user object)
        Session.getInstance().setCurrentUser(
                new AppUser(
                        username.get(),
                        username.get(),
                        firstName.get(),
                        lastName.get(),
                        email.get(),
                        "",
                        new java.util.ArrayList<>()
                )
        );
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/dashboard.fxml", 1440, 900);
        return true;
    }

    public void returnToLogin(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/login.fxml", 1440, 900);
    }
}