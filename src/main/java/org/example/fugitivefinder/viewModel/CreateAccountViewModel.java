package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

import org.example.fugitivefinder.model.FirebaseUser;
import org.example.fugitivefinder.service.FirebaseAuthService;
import org.example.fugitivefinder.service.FirestoreService;
import org.example.fugitivefinder.session.Session;

public class CreateAccountViewModel {

    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty confirmPassword = new SimpleStringProperty("");

    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty confirmPasswordProperty() { return confirmPassword; }

    public boolean createAccount(Node sourceNode) {

        if (firstName.get().isBlank() || lastName.get().isBlank() || username.get().isBlank()
                || email.get().isBlank() || password.get().isBlank() || confirmPassword.get().isBlank()) {
            return false;
        }

        if (!password.get().equals(confirmPassword.get())) {
            return false;
        }

        // 🔐 FIREBASE AUTH REGISTER
        FirebaseUser user = FirebaseAuthService.register(email.get(), password.get());

        if (user == null) {
            return false;
        }

        System.out.println("REGISTER SUCCESS UID: " + user.getLocalId());

        // FIRESTORE SAVE
        System.out.println("Calling Firestore createUser...");
        FirestoreService.createUser(user.getLocalId(), user.getEmail());

        // SESSION STORE
        Session.getInstance().setUserId(user.getLocalId());
        Session.getInstance().setEmail(user.getEmail());

        // NAVIGATE
        SceneManager.switchScene(
                sourceNode,
                "/org.example.fugitivefinder/dashboard.fxml",
                1440,
                900
        );

        return true;
    }

    public void returnToLogin(Node sourceNode) {
        SceneManager.switchScene(
                sourceNode,
                "/org.example.fugitivefinder/login.fxml",
                1440,
                900
        );
    }
}