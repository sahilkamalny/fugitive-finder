package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

import org.example.fugitivefinder.model.FirebaseUser;
import org.example.fugitivefinder.service.FirebaseAuthService;
import org.example.fugitivefinder.session.Session;

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

        FirebaseUser user = FirebaseAuthService.login(email.get(), password.get());

        if (user == null) {
            return false;
        }

        // 🔥 STORE USER SESSION
        Session.getInstance().setUserId(user.getLocalId());
        Session.getInstance().setEmail(user.getEmail());

        // 🔥 GO TO DASHBOARD
        SceneManager.switchScene(
                sourceNode,
                "/org.example.fugitivefinder/dashboard.fxml",
                1440,
                900
        );

        return true;
    }

    public void goToCreateAccount(Node sourceNode) {
        SceneManager.switchScene(
                sourceNode,
                "/org.example.fugitivefinder/create_account.fxml",
                1440,
                900
        );
    }
}