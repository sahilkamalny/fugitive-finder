package org.example.fugitivefinder.viewModel;

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
import org.example.fugitivefinder.viewModel.SceneManager;

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
    private final ObservableList<WantedPerson> featuredTargets = FXCollections.observableArrayList();

    private final ObservableList<MapPoint> fugitiveLocations = FXCollections.observableArrayList();
    private final Map<MapPoint, List<WantedPerson>> locationGroups = new HashMap<>();

    private final Map<String, MapPoint> officeCoordinates = new HashMap<>();


    public DashboardViewModel() {
        loadOfficeCoordinates();
    }

    private void loadOfficeCoordinates() {
        try (InputStream is = getClass().getResourceAsStream("/org/example/fugitivefinder/offices.json");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("[") && line.contains("]")) {
                    String key = line.split(":")[0].replace("\"", "").trim().toLowerCase();
                    String coords = line.split("\\[")[1].split("\\]")[0];
                    String[] latLng = coords.split(",");

                    double lat = Double.parseDouble(latLng[0].trim());
                    double lng = Double.parseDouble(latLng[1].trim());

                    officeCoordinates.put(key, new MapPoint(lat, lng));
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load offices.json. Falling back to default center.");
            e.printStackTrace();
        }
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

        locationGroups.clear();
        fugitiveLocations.clear();

        fugitiveLocations.add(new MapPoint(40.9465, -73.0692));
        fugitiveLocations.add(new MapPoint(40.7512, -73.4287));
        for (WantedPerson person : people) {
            MapPoint point = findCoordinates(person.getField_offices());
            if (point != null) {
                locationGroups.computeIfAbsent(point, k -> new ArrayList<>()).add(person);
                if (!fugitiveLocations.contains(point)) {
                    fugitiveLocations.add(point);
                }
            }
        }
    }

    private MapPoint findCoordinates(List<String> offices) {
        if (offices == null || offices.isEmpty()) return null;
        String officeName = offices.get(0).toLowerCase().replace(" ", "");
        return officeCoordinates.getOrDefault(officeName, new MapPoint(39.8283, -98.5795));
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

    public ObservableList<WantedPerson> getFeaturedTargets() {
        return featuredTargets;
    }

    public ObservableList<MapPoint> getFugitiveLocations() {
        return fugitiveLocations;
    }

    public List<WantedPerson> getPeopleAtLocation(MapPoint point) {
        return locationGroups.getOrDefault(point, new ArrayList<>());
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