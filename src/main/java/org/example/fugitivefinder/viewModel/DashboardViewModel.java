package org.example.fugitivefinder.viewModel;

import javafx.application.Platform;
import com.gluonhq.maps.MapPoint;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.FbiApiService;
import org.example.fugitivefinder.session.Session;

import java.util.List;

public class DashboardViewModel {

    private final StringProperty username = new SimpleStringProperty("User");
    private final StringProperty totalWanted = new SimpleStringProperty("0");
    private final StringProperty rewardCases = new SimpleStringProperty("0");
    private final StringProperty updates = new SimpleStringProperty("0");
    private final ObservableList<WantedPerson> featuredTargets = FXCollections.observableArrayList();

    private final ObservableList<MapPoint> fugitiveLocations = FXCollections.observableArrayList();
    public void loadData() {
        AppUser currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            Platform.runLater(() -> username.set(currentUser.getUsername()));
        }

        List<WantedPerson> people = FbiApiService.getWantedPeople();

        Platform.runLater(() -> {
            totalWanted.set(String.valueOf(people.size()));

            long rewardCount = people.stream()
                    .filter(person -> person.getReward_text() != null && !person.getReward_text().isBlank())
                    .count();

            rewardCases.set(String.valueOf(rewardCount));
            updates.set(String.valueOf(Math.min(10, people.size())));

            featuredTargets.clear();
            int limit = Math.min(6, people.size());
            featuredTargets.addAll(people.subList(0, limit));
        });
        featuredTargets.clear();
        int limit = Math.min(6, people.size());
        featuredTargets.addAll(people.subList(0, Math.min(6, people.size())));
        fugitiveLocations.clear();
        fugitiveLocations.add(new MapPoint(40.7506, -73.4290));
        fugitiveLocations.add(new MapPoint(40.8682, -73.4257)); // Huntington
        fugitiveLocations.add(new MapPoint(40.6959, -73.3257));//Babylon
    }

    public StringProperty usernameProperty() { return username; }
    public StringProperty totalWantedProperty() { return totalWanted; }
    public StringProperty rewardCasesProperty() { return rewardCases; }
    public StringProperty updatesProperty() { return updates; }
    public ObservableList<WantedPerson> getFeaturedTargets() { return featuredTargets; }
    public ObservableList<MapPoint> getFugitiveLocations() { return fugitiveLocations; }
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