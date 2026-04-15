package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.service.UserService;
import org.example.fugitivefinder.session.Session;
import org.example.fugitivefinder.viewModel.SceneManager;

public class LoginViewModel {

    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public boolean login(Node sourceNode) {
        boolean success = UserService.login(email.get(), password.get());

        if (!success) {
            return false;
        }

// TEMP user (until we return real one from backend)
        Session.getInstance().setCurrentUser(
                new AppUser(
                        email.get(),
                        email.get(),
                        "",
                        "",
                        email.get(),
                        "",
                        new java.util.ArrayList<>()
                )
        );
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/dashboard.fxml", 1440, 900);
        return true;
    }

    public void goToCreateAccount(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/create_account.fxml", 1440, 900);
    }
}