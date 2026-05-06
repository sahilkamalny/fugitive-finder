package org.example.fugitivefinder.viewModel;

import com.gluonhq.maps.MapPoint;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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

public class MapsViewModel {

    private final Map<MapPoint, List<WantedPerson>> locationGroups = new HashMap<>();
    private final Map<String, MapPoint> officeCoordinates = new HashMap<>();
    private final ObservableList<WantedPerson> allPeople = FXCollections.observableArrayList();
    private final ObservableList<MapPoint> filteredLocations = FXCollections.observableArrayList();

    private String nameQuery = "";
    private String officeFilter = "All Offices";

    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty statusMessage = new SimpleStringProperty("Initializing map...");

    public MapsViewModel() {
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

    public void loadMapData() {
        Platform.runLater(() -> {
            loading.set(true);
            statusMessage.set("Fetching fugitive data from FBI API...");
        });

        List<WantedPerson> people = FbiApiService.getWantedPeople();
        Platform.runLater(() -> {
            statusMessage.set("Processing coordinates...");
            allPeople.setAll(people);
            applyFilters();
            loading.set(false);
            statusMessage.set("Ready");
        });
    }

    private void applyFilters() {
        filteredLocations.clear();
        locationGroups.clear();

        String query = nameQuery.toLowerCase().trim();
        boolean hasNameQuery = !query.isEmpty();
        boolean hasOfficeQuery = !officeFilter.equals("All Offices");

        for (WantedPerson person : allPeople) {
            boolean matchesName = !hasNameQuery || (person.getTitle() != null && person.getTitle().toLowerCase().contains(query));
            
            boolean matchesOffice = !hasOfficeQuery;
            if (hasOfficeQuery && person.getFieldOffices() != null) {
                String target = officeFilter.toLowerCase().replace(" ", "");
                matchesOffice = person.getFieldOffices().stream()
                        .anyMatch(o -> o.toLowerCase().replace(" ", "").equals(target));
            }

            if (matchesName && matchesOffice) {
                MapPoint point = findCoordinates(person.getFieldOffices());
                if (point != null) {
                    locationGroups.computeIfAbsent(point, k -> new ArrayList<>()).add(person);
                    if (!filteredLocations.contains(point)) {
                        filteredLocations.add(point);
                    }
                }
            }
        }
    }

    public void filterByName(String name) {
        this.nameQuery = name != null ? name : "";
        applyFilters();
    }

    public void filterByOffice(String officeName) {
        this.officeFilter = officeName != null ? officeName : "All Offices";
        applyFilters();
    }

    public void clearNameSearch() {
        this.nameQuery = "";
        applyFilters();
    }

    public void clearOfficeFilter() {
        this.officeFilter = "All Offices";
        applyFilters();
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public boolean isLoading() {
        return loading.get();
    }

    private MapPoint findCoordinates(List<String> offices) {
        if (offices == null || offices.isEmpty()){
            return null;
        }
        String officeName = offices.get(0).toLowerCase().trim().replace(" ", "");
        MapPoint point = officeCoordinates.get(officeName);
        return  point;
    }

    public ObservableList<MapPoint> getFugitiveLocations() {
        return filteredLocations;
    }
    public Map<MapPoint, List<WantedPerson>> getLocationGroups() { return locationGroups; }
    public Map<String, MapPoint> getOfficeCoordinates() { return officeCoordinates; }
    public List<WantedPerson> getWantedPeopleList() { return allPeople; }

    public void goToDashboard(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/dashboard.fxml", 1440, 900);
    }

    public void openCriminalProfile(Node sourceNode, WantedPerson person) {
        Session.getInstance().setSelectedWantedPerson(person);
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/criminal-profile.fxml", 1440, 900);
    }

    public void goToCharts(Node source) {
        SceneManager.switchScene(source, "/org.example.fugitivefinder/analytics.fxml", 1440, 900);
    }

    public void goToUserProfile(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/user-profile.fxml", 1440, 900);
    }
}