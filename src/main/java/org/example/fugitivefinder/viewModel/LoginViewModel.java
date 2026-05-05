package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

import org.example.fugitivefinder.model.FirebaseUser;
import org.example.fugitivefinder.service.FirebaseAuthService;
import org.example.fugitivefinder.service.FirestoreService;
import org.example.fugitivefinder.session.Session;

import java.util.Map;

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

        Session.getInstance().setUserId(user.getLocalId());
        Session.getInstance().setEmail(user.getEmail());

        Map<String, String> userData = FirestoreService.getUserData(user.getLocalId());

        if (userData != null) {
            Session.getInstance().setUsername(userData.get("username"));
            Session.getInstance().setFullName(
                    userData.get("firstName") + " " + userData.get("lastName")
            );
            Session.getInstance().setEmail(userData.get("email"));
        }

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