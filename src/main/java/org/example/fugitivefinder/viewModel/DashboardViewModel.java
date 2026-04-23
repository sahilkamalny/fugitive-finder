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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardViewModel {

    private final StringProperty username = new SimpleStringProperty("User");
    private final StringProperty totalWanted = new SimpleStringProperty("0");
    private final StringProperty rewardCases = new SimpleStringProperty("0");
    private final StringProperty updates = new SimpleStringProperty("0");
    private final ObservableList<WantedPerson> allPeople = FXCollections.observableArrayList();

    public void loadData() {
        AppUser currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            Platform.runLater(() -> username.set(currentUser.getUsername()));
        }

        List<WantedPerson> people = FbiApiService.getWantedPeople();

        Platform.runLater(() -> {
            totalWanted.set(String.valueOf(people.size()));

            long rewardCount = people.stream()
                    .filter(person -> person.getRewardText() != null && !person.getRewardText().isBlank())
                    .count();

            rewardCases.set(String.valueOf(rewardCount));
            updates.set(String.valueOf(Math.min(10, people.size())));

            allPeople.clear();
            allPeople.addAll(people);

        });
        System.out.println("People size: " + people.size());
    }

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

    public ObservableList<WantedPerson> getAllPeople() {
        return allPeople;
    }



    public void openCriminalProfile(Node sourceNode, WantedPerson person) {
        Session.getInstance().setSelectedWantedPerson(person);
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/criminal-profile.fxml", 1440, 900);
    }
    public void goToMap(Node source) {
        SceneManager.switchScene(source, "/org.example.fugitivefinder/maps-view.fxml", 1440, 900);
    }

    public void goToCharts(Node source) {
        SceneManager.switchScene(source, "/org.example.fugitivefinder/charts-view.fxml", 1440, 900);
    }

    public void goToRewards(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/rewards.fxml", 1440, 900);
    }

    public void goToUserProfile(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/user-profile.fxml", 1440, 900);
    }

}