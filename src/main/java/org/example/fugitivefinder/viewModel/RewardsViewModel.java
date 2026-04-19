package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.FbiApiService;
import org.example.fugitivefinder.session.Session;

import java.util.List;
import java.util.stream.Collectors;

public class RewardsViewModel {

    private final StringProperty searchQuery = new SimpleStringProperty("");
    private final ObservableList<WantedPerson> rewardCases = FXCollections.observableArrayList();
    private final ObservableList<WantedPerson> filteredRewardCases = FXCollections.observableArrayList();

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    public ObservableList<WantedPerson> getFilteredRewardCases() {
        return filteredRewardCases;
    }

    public void loadData() {
        List<WantedPerson> people = FbiApiService.getWantedPeople().stream()
                .filter(person -> person.getRewardText() != null && !person.getRewardText().isBlank())
                .collect(Collectors.toList());

        rewardCases.setAll(people);
        filteredRewardCases.setAll(people);
    }

    public void filter() {
        String query = searchQuery.get() == null ? "" : searchQuery.get().trim().toLowerCase();

        List<WantedPerson> filtered = rewardCases.stream()
                .filter(person -> person.getTitle().toLowerCase().contains(query)
                        || person.getDisplayReward().toLowerCase().contains(query))
                .collect(Collectors.toList());

        filteredRewardCases.setAll(filtered);
    }

    public void openCriminalProfile(Node sourceNode, WantedPerson person) {
        Session.getInstance().setSelectedWantedPerson(person);
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/criminal-profile.fxml", 1440, 900);
    }

    public void goToDashboard(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/dashboard.fxml", 1440, 900);
    }

    public void goToUserProfile(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/user-profile.fxml", 1440, 900);
    }
}