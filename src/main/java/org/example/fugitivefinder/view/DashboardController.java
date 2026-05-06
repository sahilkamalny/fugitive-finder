package org.example.fugitivefinder.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.DashboardViewModel;

public class DashboardController {

    @FXML private Label usernameLabel;
    @FXML private Label totalWantedLabel;
    @FXML private Label rewardCasesLabel;
    @FXML private FlowPane featuredCardsPane;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ComboBox<String> rewardFilterComboBox;
    @FXML private TextField searchField;

    private DashboardViewModel viewModel;
    private List<WantedPerson> masterPeopleList;
    //the currently visible filtered list
    private List<WantedPerson> allPeople;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 16;

    @FXML
    public void initialize() {
        viewModel = new DashboardViewModel();
        bindProperties();
        setupSort();
        setupFilters();
        setupListeners();
        loadInitialData();
    }

    public void bindProperties() {
        usernameLabel.textProperty().bind(viewModel.usernameProperty());
        totalWantedLabel.textProperty().bind(viewModel.totalWantedProperty());
        rewardCasesLabel.textProperty().bind(viewModel.rewardCasesProperty());
    }

    private void setupFilters() {
        statusFilterComboBox.getItems().addAll("All", "Wanted", "Captured", "Missing");
        statusFilterComboBox.setValue("All");

        rewardFilterComboBox.getItems().addAll(
                "All", "Reward Listed", "No Reward", "$10,000+", "$50,000+", "$100,000+"
        );
        rewardFilterComboBox.setValue("All");
    }

    private void setupSort() {
        sortComboBox.getItems().addAll(
                "Default",
                "Reward: High to Low",
                "Reward: Low to High",
                "Name: A to Z",
                "Name: Z to A"
        );
        sortComboBox.setValue("Default");
        sortComboBox.getStylesheets().add(getClass().getResource(
                "/org.example.fugitivefinder/styles/combobox.css").toExternalForm());
    }

    private void setupListeners() {
        viewModel.getAllPeople().addListener((ListChangeListener<WantedPerson>) change -> {
            masterPeopleList = new ArrayList<>(viewModel.getAllPeople());
            applyFiltersAndSort();
        });
    }

    private void loadInitialData() {
        new Thread(() -> {
            viewModel.loadData();
            Platform.runLater(() -> {
                masterPeopleList = new ArrayList<>(viewModel.getAllPeople());
                applyFiltersAndSort();
            });
        }).start();
    }

    @FXML
    private void handleSort() {
        applyFiltersAndSort();
    }

    @FXML
    private void handleFilters() {
        applyFiltersAndSort();
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        applyFiltersAndSort();
    }

    @FXML
    private void clearAllFilters() {
        searchField.clear();
        statusFilterComboBox.setValue("All");
        rewardFilterComboBox.setValue("All");
        sortComboBox.setValue("Default");
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        if (masterPeopleList == null) return;

        //filtering
        allPeople = masterPeopleList.stream()
                .filter(this::matchesName)
                .filter(this::matchesStatus)
                .filter(this::matchesReward)
                .collect(Collectors.toList());

        //sorting
        String selectedSort = sortComboBox.getValue();
        if (selectedSort != null && !selectedSort.equals("Default")) {
            switch (selectedSort) {
                case "Reward: High to Low" -> allPeople.sort((a, b) -> Double.compare(b.getRewardAmount(), a.getRewardAmount()));
                case "Reward: Low to High" -> allPeople.sort((a, b) -> Double.compare(a.getRewardAmount(), b.getRewardAmount()));
                case "Name: A to Z" -> allPeople.sort(Comparator.comparing(p -> p.getTitle() != null ? p.getTitle() : ""));
                case "Name: Z to A" -> allPeople.sort((a, b) -> (b.getTitle() != null ? b.getTitle() : "").compareTo(a.getTitle() != null ? a.getTitle() : ""));
            }
        }

        currentPage = 1;
        Platform.runLater(this::renderPage);
    }

    private boolean matchesName(WantedPerson person) {
        String query = searchField.getText();
        if (query == null || query.isBlank()) return true;
        return person.getTitle() != null && person.getTitle().toLowerCase().contains(query.toLowerCase().trim());
    }

    private boolean matchesStatus(WantedPerson person) {
        String status = statusFilterComboBox.getValue();
        if (status == null || status.equals("All")) return true;
        return person.getStatus() != null && person.getStatus().equalsIgnoreCase(status);
    }

    private boolean matchesReward(WantedPerson person) {
        String rewardOption = rewardFilterComboBox.getValue();
        if (rewardOption == null || rewardOption.equals("All")) return true;

        double amount = person.getRewardAmount();
        return switch (rewardOption) {
            case "Reward Listed" -> amount > 0;
            case "No Reward" -> amount == 0;
            case "$10,000+" -> amount >= 10000;
            case "$50,000+" -> amount >= 50000;
            case "$100,000+" -> amount >= 100000;
            default -> true;
        };
    }

    private void renderPage() {
        featuredCardsPane.getChildren().clear();

        if (allPeople == null || allPeople.isEmpty()) {
            pageLabel.setText("No results found");
            prevButton.setDisable(true);
            nextButton.setDisable(true);
            return;
        }

        int totalPages = (int) Math.ceil((double) allPeople.size() / PAGE_SIZE);
        int fromIndex = (currentPage - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allPeople.size());

        for (WantedPerson person : allPeople.subList(fromIndex, toIndex)) {
            featuredCardsPane.getChildren().add(createCard(person));
        }

        pageLabel.setText("Page " + currentPage + " of " + totalPages);
        prevButton.setDisable(currentPage == 1);
        nextButton.setDisable(currentPage == totalPages);
    }

    private VBox createCard(WantedPerson person) {
        Label nameLabel = new Label(person.getTitle());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);

        Label rewardLabel = new Label(person.getDisplayReward());
        rewardLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 14;");
        rewardLabel.setWrapText(true);

        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(false);

        String imageUrl = person.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            imageView.setImage(new javafx.scene.image.Image(imageUrl, true));
        } else {
            imageView.setImage(new javafx.scene.image.Image(getClass().getResource("/org.example.fugitivefinder/images/criminal1.png").toExternalForm()));
        }

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            String uid = org.example.fugitivefinder.session.Session.getInstance().getUserId();
            org.example.fugitivefinder.service.FirestoreService.saveTarget(uid, person.getUid());
        });

        VBox card = new VBox(6, imageView, nameLabel, rewardLabel, saveButton);
        card.setPadding(new Insets(12));
        card.setPrefWidth(270);
        card.setPrefHeight(280);
        card.setStyle("-fx-background-color: #111827; -fx-background-radius: 14; -fx-border-color: #334155; -fx-border-radius: 14;");
        card.setOnMouseClicked(event -> viewModel.openCriminalProfile(card, person));

        return card;
    }

    @FXML private void showMapScreen() { viewModel.goToMap(featuredCardsPane); }
    @FXML private void showChartsScreen() { viewModel.goToCharts(featuredCardsPane); }
    @FXML private void goToLeaderboard() { viewModel.goToLeaderboard(featuredCardsPane); }
    @FXML private void goToUserProfile() { viewModel.goToUserProfile(featuredCardsPane); }

    @FXML
    public void goToPrevPage(ActionEvent actionEvent) {
        if (currentPage > 1) {
            currentPage--;
            renderPage();
        }
    }

    @FXML
    public void goToNextPage(ActionEvent actionEvent) {
        if (allPeople == null) return;
        int totalPages = (int) Math.ceil((double) allPeople.size() / PAGE_SIZE);
        if (currentPage < totalPages) {
            currentPage++;
            renderPage();
        }
    }
}