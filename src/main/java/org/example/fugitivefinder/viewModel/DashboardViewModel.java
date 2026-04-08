package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.FbiApiService;
import org.example.fugitivefinder.session.Session;
import org.example.fugitivefinder.viewModel.SceneManager;

import java.util.List;

public class DashboardViewModel {

    private final StringProperty username = new SimpleStringProperty("User");
    private final StringProperty totalWanted = new SimpleStringProperty("0");
    private final StringProperty rewardCases = new SimpleStringProperty("0");
    private final StringProperty updates = new SimpleStringProperty("0");
    private final ObservableList<WantedPerson> featuredTargets = FXCollections.observableArrayList();

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty totalWantedProperty() {
        return totalWanted;
    }

    public StringProperty rewardCasesProperty() {
        return rewardCases;
    }

    public StringProperty updatesProperty() {
        return updates;
    }

    public ObservableList<WantedPerson> getFeaturedTargets() {
        return featuredTargets;
    }

    public void loadData() {
        AppUser currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            username.set(currentUser.getUsername());
        }

        List<WantedPerson> people = FbiApiService.getWantedPeople();

        totalWanted.set(String.valueOf(people.size()));

        long rewardCount = people.stream()
                .filter(person -> person.getReward_text() != null && !person.getReward_text().isBlank())
                .count();

        rewardCases.set(String.valueOf(rewardCount));
        updates.set(String.valueOf(Math.min(10, people.size())));

        featuredTargets.clear();
        int limit = Math.min(6, people.size());
        featuredTargets.addAll(people.subList(0, limit));
    }

    public void openCriminalProfile(Node sourceNode, WantedPerson person) {
        Session.getInstance().setSelectedWantedPerson(person);
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/criminal-profile.fxml", 1440, 900);
    }

    public void goToRewards(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/rewards.fxml", 1440, 900);
    }

    public void goToUserProfile(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/user-profile.fxml", 1440, 900);
    }
}