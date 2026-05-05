package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.FbiApiService;
import org.example.fugitivefinder.service.FirestoreService;
import org.example.fugitivefinder.session.Session;
import java.util.ArrayList;
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

        String uid = Session.getInstance().getUserId();

        if (uid == null || uid.isBlank()) {
            username.set("Guest");
            fullName.set("No user logged in");
            email.set("Please log in first");
            savedTargets.clear();
            return;
        }

        username.set(Session.getInstance().getUsername());
        fullName.set(Session.getInstance().getFullName());
        email.set(Session.getInstance().getEmail());

        loadSavedTargets();
    }


    private void loadSavedTargets() {

        String uid = Session.getInstance().getUserId();

        List<String> savedIds = FirestoreService.getSavedTargets(uid);
        List<WantedPerson> allPeople = FbiApiService.getWantedPeople();

        savedTargets.clear();

        for (WantedPerson person : allPeople) {
            if (person.getUid() != null && savedIds.contains(person.getUid())) {
                savedTargets.add(person);
            }
        }

        System.out.println("Loaded saved targets: " + savedTargets.size());
    }



    public void goToDashboard(Node sourceNode) {
        SceneManager.switchScene(
                sourceNode,
                "/org.example.fugitivefinder/dashboard.fxml",
                1440,
                900
        );
    }
    public void goToMaps(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/maps-view.fxml", 1440, 900);
    }

    public void goToRewards(Node sourceNode) {
        SceneManager.switchScene(
                sourceNode,
                "/org.example.fugitivefinder/rewards.fxml",
                1440,
                900
        );
    }
    public void goToAnalytics(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/analytics.fxml", 1440, 900);
    }


}