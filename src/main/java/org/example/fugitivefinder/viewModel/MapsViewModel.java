package org.example.fugitivefinder.viewModel;

import com.gluonhq.maps.MapPoint;
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


    private final ObservableList<MapPoint> fugitiveLocations = FXCollections.observableArrayList();
    private final Map<MapPoint, List<WantedPerson>> locationGroups = new HashMap<>();
    private final Map<String, MapPoint> officeCoordinates = new HashMap<>();
    private final ObservableList<WantedPerson> allPeople = FXCollections.observableArrayList();

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

    public void applyMapFilters(String city, String crime, String status, String keyword, boolean rewardOnly) {
        locationGroups.clear();
        fugitiveLocations.clear();

        String cityFilter = city == null ? "All Cities" : city;
        String crimeFilter = crime == null ? "All Crimes" : crime;
        String statusFilter = status == null ? "All Statuses" : status;
        String keywordFilter = keyword == null ? "" : keyword.toLowerCase().trim();

        for (WantedPerson person : allPeople) {
            if (!matchesCity(person, cityFilter)) {
                continue;
            }

            if (!matchesCrime(person, crimeFilter)) {
                continue;
            }

            if (!matchesStatus(person, statusFilter)) {
                continue;
            }

            if (!matchesKeyword(person, keywordFilter)) {
                continue;
            }

            if (rewardOnly && person.getRewardAmount() <= 0) {
                continue;
            }

            MapPoint point = findCoordinates(person.getFieldOffices());

            if (point != null) {
                locationGroups.computeIfAbsent(point, k -> new ArrayList<>()).add(person);

                if (!fugitiveLocations.contains(point)) {
                    fugitiveLocations.add(point);
                }
            }
        }
    }

    private boolean matchesCity(WantedPerson person, String cityFilter) {
        if (cityFilter.equals("All Cities")) {
            return true;
        }

        if (person.getFieldOffices() == null || person.getFieldOffices().isEmpty()) {
            return false;
        }

        return person.getFieldOffices().stream()
                .anyMatch(office -> office.equalsIgnoreCase(cityFilter));
    }

    private boolean matchesCrime(WantedPerson person, String crimeFilter) {
        if (crimeFilter.equals("All Crimes")) {
            return true;
        }

        String text = buildSearchText(person);
        return text.contains(crimeFilter.toLowerCase());
    }

    private boolean matchesStatus(WantedPerson person, String statusFilter) {
        if (statusFilter.equals("All Statuses")) {
            return true;
        }

        String status = person.getDisplayStatus();
        return status != null && status.toLowerCase().contains(statusFilter.toLowerCase());
    }

    private boolean matchesKeyword(WantedPerson person, String keywordFilter) {
        if (keywordFilter.isBlank()) {
            return true;
        }

        String text = buildSearchText(person);
        return text.contains(keywordFilter);
    }

    private String buildSearchText(WantedPerson person) {
        StringBuilder sb = new StringBuilder();

        if (person.getTitle() != null) {
            sb.append(person.getTitle()).append(" ");
        }

        if (person.getDescription() != null) {
            sb.append(person.getDescription()).append(" ");
        }

        if (person.getWarning_message() != null) {
            sb.append(person.getWarning_message()).append(" ");
        }

        if (person.getRewardText() != null) {
            sb.append(person.getRewardText()).append(" ");
        }

        if (person.getAliases() != null) {
            sb.append(String.join(" ", person.getAliases())).append(" ");
        }

        if (person.getFieldOffices() != null) {
            sb.append(String.join(" ", person.getFieldOffices())).append(" ");
        }

        return sb.toString().toLowerCase();
    }

    public void loadMapData() {
        List<WantedPerson> people = FbiApiService.getWantedPeople();
        Platform.runLater(() -> {
            allPeople.setAll(people);
            locationGroups.clear();
            fugitiveLocations.clear();

            for (WantedPerson person : people) {
                MapPoint point = findCoordinates(person.getFieldOffices());
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

    public ObservableList<MapPoint> getFugitiveLocations() { return fugitiveLocations; }
    public Map<MapPoint, List<WantedPerson>> getLocationGroups() { return locationGroups; }
    public Map<String, MapPoint> getOfficeCoordinates() { return officeCoordinates; }
    public ObservableList<WantedPerson> getWantedPeopleList() {
        return allPeople;
    }
    public void goToDashboard(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/dashboard.fxml", 1440, 900);
    }

    public void openCriminalProfile(Node sourceNode, WantedPerson person) {
        Session.getInstance().setSelectedWantedPerson(person);
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/criminal-profile.fxml", 1440, 900);
    }

    public void goToRewards(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/rewards.fxml", 1440, 900);
    }
    public void goToCharts(Node source) {
        SceneManager.switchScene(source, "/org.example.fugitivefinder/charts-view.fxml", 1440, 900);
    }

    public void goToUserProfile(Node sourceNode) {
        SceneManager.switchScene(sourceNode, "/org.example.fugitivefinder/user-profile.fxml", 1440, 900);
    }
}