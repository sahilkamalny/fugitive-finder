package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.FbiApiService;
import org.example.fugitivefinder.service.FirestoreService;
import org.example.fugitivefinder.session.Session;

import java.util.List;

public class UserProfileViewModel {

    private final StringProperty username = new SimpleStringProperty("User");
    private final StringProperty fullName = new SimpleStringProperty("Logged In User");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty avatarPath = new SimpleStringProperty("");

    private final ObservableList<WantedPerson> savedTargets = FXCollections.observableArrayList();

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty avatarPathProperty() {
        return avatarPath;
    }

    public ObservableList<WantedPerson> getSavedTargets() {
        return savedTargets;
    }

    public void loadData() {
        email.set(Session.getInstance().getEmail());
        username.set(Session.getInstance().getUsername());
        fullName.set(Session.getInstance().getFullName());

        loadSavedTargets(); // already done
    }


    private void loadSavedTargets() {

        String uid = Session.getInstance().getUserId();

        List<String> savedIds = FirestoreService.getSavedTargets(uid);
        List<WantedPerson> allPeople = FbiApiService.getWantedPeople();

        savedTargets.clear();

        for (WantedPerson person : allPeople) {
            if (savedIds.contains(person.getUid())) {
                savedTargets.add(person);
            }
        }
    }

    public void goToDashboard(Node sourceNode) {
        SceneManager.switchScene(
                sourceNode,
                "/org.example.fugitivefinder/dashboard.fxml",
                1440,
                900
        );
    }

    public void goToRewards(Node sourceNode) {
        SceneManager.switchScene(
                sourceNode,
                "/org.example.fugitivefinder/rewards.fxml",
                1440,
                900
        );
    }
}