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
    private final ObservableList<WantedPerson> featuredTargets = FXCollections.observableArrayList();
    private final ObservableList<WantedPerson> allPeople = FXCollections.observableArrayList();
    private final ObservableList<MapPoint> fugitiveLocations = FXCollections.observableArrayList();
    private final Map<MapPoint, List<WantedPerson>> locationGroups = new HashMap<>();
    private final Map<String, MapPoint> officeCoordinates = new HashMap<>();


    public DashboardViewModel() {
        loadOfficeCoordinates();
    }

    private void loadOfficeCoordinates() {
        String path = "/offices.json";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("CRITICALLLL: Could not find offices.json at " + path);
                return;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains(":") && line.contains("[")) {
                        String key = line.split(":")[0].replace("\"", "").trim().toLowerCase();
                        String coords = line.split("\\[")[1].split("\\]")[0];
                        String[] latLng = coords.split(",");

                        double lat = Double.parseDouble(latLng[0].trim());
                        double lng = Double.parseDouble(latLng[1].trim());

                        officeCoordinates.put(key, new MapPoint(lat, lng));
                    }
                }
                System.out.println("SUCCESS: Loaded " + officeCoordinates.size() + " office coordinates.");
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to parse offices.json");
            e.printStackTrace();
        }
    }

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

            allPeople.clear();
            allPeople.addAll(people);

            featuredTargets.clear();
            featuredTargets.addAll(people.subList(0, Math.min(20, people.size())));

            locationGroups.clear();
            fugitiveLocations.clear();

            for (WantedPerson person : people) {
                MapPoint point = findCoordinates(person.getField_offices());
                if (point != null) {
                    locationGroups.computeIfAbsent(point, k -> new ArrayList<>()).add(person);
                    if (!fugitiveLocations.contains(point)) {
                        fugitiveLocations.add(point);
                    }
                }
            }
        });
    }
    private MapPoint findCoordinates(List<String> offices) {
        if (offices == null || offices.isEmpty()){
           // System.out.println("This person has no field offices listed.");
        return null;
    }
        String officeName = offices.get(0).toLowerCase().trim().replace(" ", "");
        MapPoint point = officeCoordinates.get(officeName);
       /* if (point == null) {
            System.out.println("Match Failed");
            System.out.println("   -> API sent: " + offices.get(0));
            System.out.println("   -> Search key used: '" + officeName + "'");
            System.out.println("   -> Total keys in json map: " + officeCoordinates.size());

            if (!officeCoordinates.isEmpty()) {
                System.out.println("   -> This keys is in map: " + officeCoordinates.keySet().stream().limit(5).toList());
            }
        } else {
            System.out.println("Match Success for " + officeName + " at " + point.getLatitude() + ", " + point.getLongitude());
        }*/
        return  point;
    }

    public StringProperty usernameProperty() { return username; }
    public StringProperty totalWantedProperty() { return totalWanted; }
    public StringProperty rewardCasesProperty() { return rewardCases; }
    public StringProperty updatesProperty() { return updates; }
    public ObservableList<WantedPerson> getFeaturedTargets() { return featuredTargets; }
    public ObservableList<WantedPerson> getAllPeople() { return allPeople; }
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

    public Map<String, MapPoint> getOfficeCoordinates() {
        return this.officeCoordinates;
    }

    public List<WantedPerson> getWantedPeopleList() {
        return this.allPeople;
    }
}