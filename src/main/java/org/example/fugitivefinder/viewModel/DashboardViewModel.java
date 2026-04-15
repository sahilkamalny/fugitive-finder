package org.example.fugitivefinder.viewModel;

import com.gluonhq.maps.MapPoint;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.FbiApiService;
import org.example.fugitivefinder.session.Session;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DashboardViewModel {

    private final StringProperty username = new SimpleStringProperty("User");
    private final StringProperty totalWanted = new SimpleStringProperty("0");
    private final StringProperty rewardCases = new SimpleStringProperty("0");
    private final StringProperty updates = new SimpleStringProperty("0");

    private final ObservableList<WantedPerson> allPeople = FXCollections.observableArrayList();
    private final ObservableList<WantedPerson> filteredPeople = FXCollections.observableArrayList();
    private final ObservableList<WantedPerson> featuredTargets = FXCollections.observableArrayList();
    private final ObservableList<MapPoint> fugitiveLocations = FXCollections.observableArrayList();

    private final StringProperty selectedCrimeType = new SimpleStringProperty("All");
    private final StringProperty selectedSex = new SimpleStringProperty("All");
    private final StringProperty cityQuery = new SimpleStringProperty("");
    private final IntegerProperty minAge = new SimpleIntegerProperty(0);
    private final IntegerProperty maxAge = new SimpleIntegerProperty(100);
    private final BooleanProperty rewardOnly = new SimpleBooleanProperty(false);
    private final BooleanProperty warningOnly = new SimpleBooleanProperty(false);

    private final ObservableList<String> crimeTypeOptions = FXCollections.observableArrayList("All");
    private final ObservableList<String> sexOptions = FXCollections.observableArrayList("All");

    public void loadData() {
        AppUser currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            username.set(currentUser.getUsername());
        }

        List<WantedPerson> people = FbiApiService.getWantedPeople();

        allPeople.setAll(people);

        totalWanted.set(String.valueOf(people.size()));

        long rewardCount = people.stream()
                .filter(person -> person.getReward_text() != null && !person.getReward_text().isBlank())
                .count();

        rewardCases.set(String.valueOf(rewardCount));
        updates.set(String.valueOf(Math.min(10, people.size())));

        buildFilterOptions();
        applyFilters();
    }

    private void buildFilterOptions() {
        crimeTypeOptions.setAll("All");
        sexOptions.setAll("All");

        List<String> crimes = allPeople.stream()
                .map(this::extractCrimeType)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> sexes = allPeople.stream()
                .map(person -> safe(person.getSex()))
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        crimeTypeOptions.addAll(crimes);
        sexOptions.addAll(sexes);
    }

    public void applyFilters() {
        List<WantedPerson> results = allPeople.stream()
                .filter(this::matchesCrimeType)
                .filter(this::matchesSex)
                .filter(this::matchesCity)
                .filter(this::matchesAge)
                .filter(this::matchesReward)
                .filter(this::matchesWarning)
                .collect(Collectors.toList());

        filteredPeople.setAll(results);

        featuredTargets.clear();
        featuredTargets.addAll(results.subList(0, Math.min(6, results.size())));

        rebuildMapLocations(results);
    }

    private void rebuildMapLocations(List<WantedPerson> people) {
        fugitiveLocations.clear();

        for (WantedPerson person : people) {
            Double lat = person.getLatitude();
            Double lon = person.getLongitude();

            if (lat != null && lon != null) {
                fugitiveLocations.add(new MapPoint(lat, lon));
            }
        }

        if (fugitiveLocations.isEmpty()) {
            fugitiveLocations.add(new MapPoint(40.7506, -73.4290));
            fugitiveLocations.add(new MapPoint(40.8682, -73.4257));
            fugitiveLocations.add(new MapPoint(40.6959, -73.3257));
        }
    }

    private boolean matchesCrimeType(WantedPerson person) {
        String selected = selectedCrimeType.get();
        if (selected == null || selected.equals("All")) {
            return true;
        }
        String crime = extractCrimeType(person);
        return crime.equalsIgnoreCase(selected);
    }

    private boolean matchesSex(WantedPerson person) {
        String selected = selectedSex.get();
        if (selected == null || selected.equals("All")) {
            return true;
        }
        return safe(person.getSex()).equalsIgnoreCase(selected);
    }

    private boolean matchesCity(WantedPerson person) {
        String query = cityQuery.get();
        if (query == null || query.isBlank()) {
            return true;
        }

        String cityText = person.getPlace_of_birth() + " " +
                person.getFieldOfficesText() + " " +
                person.getLocations();

        return cityText.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }

    private boolean matchesAge(WantedPerson person) {
        Integer age = person.getAgeNow();
        if (age == null) {
            return true;
        }
        return age >= minAge.get() && age <= maxAge.get();
    }

    private boolean matchesReward(WantedPerson person) {
        if (!rewardOnly.get()) {
            return true;
        }
        return person.getReward_text() != null && !person.getReward_text().isBlank();
    }

    private boolean matchesWarning(WantedPerson person) {
        if (!warningOnly.get()) {
            return true;
        }
        return person.getWarning_message() != null && !person.getWarning_message().isBlank();
    }

    private String extractCrimeType(WantedPerson person) {
        return safe(person.getSubjects());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
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

    public ObservableList<String> getCrimeTypeOptions() {
        return crimeTypeOptions;
    }

    public ObservableList<String> getSexOptions() {
        return sexOptions;
    }

    public StringProperty selectedCrimeTypeProperty() {
        return selectedCrimeType;
    }

    public StringProperty selectedSexProperty() {
        return selectedSex;
    }

    public StringProperty cityQueryProperty() {
        return cityQuery;
    }

    public IntegerProperty minAgeProperty() {
        return minAge;
    }

    public IntegerProperty maxAgeProperty() {
        return maxAge;
    }

    public BooleanProperty rewardOnlyProperty() {
        return rewardOnly;
    }

    public BooleanProperty warningOnlyProperty() {
        return warningOnly;
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