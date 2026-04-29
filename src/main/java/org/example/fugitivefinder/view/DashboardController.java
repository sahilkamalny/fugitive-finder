package org.example.fugitivefinder.view;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.session.Session;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javafx.scene.control.TextField;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.DashboardViewModel;

import static javafx.util.Duration.millis;

public class DashboardController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label totalWantedLabel;
    @FXML
    private Label rewardCasesLabel;
    @FXML
    private FlowPane featuredCardsPane;
    @FXML
    private StackPane mapContainer;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label pageLabel;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private ComboBox<String> rewardFilterComboBox;

    @FXML
    private TextField searchField;

    private DashboardViewModel viewModel;
    private MapView mapView;

    private List<WantedPerson> allPeople;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 16;
    private boolean isHeatMapMode = false;

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
        statusFilterComboBox.getItems().addAll(
                "All",
                "Wanted",
                "Captured",
                "Missing"
        );
        statusFilterComboBox.setValue("All");

        rewardFilterComboBox.getItems().addAll(
                "All",
                "Reward Listed",
                "No Reward",
                "$10,000+",
                "$50,000+",
                "$100,000+"
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
            allPeople = new ArrayList<>(viewModel.getAllPeople());
            Platform.runLater(this::renderPage);
        });
    }

    private void loadInitialData() {
        new Thread(() -> {
            viewModel.loadData();
            Platform.runLater(() -> {
                allPeople = new ArrayList<>(viewModel.getAllPeople());
                renderPage();
            });
        }).start();
    }

    @FXML
    private void handleFilters() {
        applyFiltersAndSort();
    }
    @FXML
    private void handleSort() {
        applyFiltersAndSort();
    }
    private void applyFiltersAndSort() {
        if (viewModel.getAllPeople() == null || viewModel.getAllPeople().isEmpty()) {
            return;
        }

        String search = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String statusFilter = statusFilterComboBox.getValue();
        String rewardFilter = rewardFilterComboBox.getValue();

        allPeople = new ArrayList<>(viewModel.getAllPeople());

        allPeople.removeIf(person -> {
            boolean remove = false;

            if (!search.isBlank()) {
                String title = person.getTitle() == null ? "" : person.getTitle().toLowerCase();
                remove = !title.contains(search);
            }

            if (!remove && statusFilter != null && !statusFilter.equals("All")) {
                String status = person.getDisplayStatus().toLowerCase();
                remove = !status.contains(statusFilter.toLowerCase());
            }

            if (!remove && rewardFilter != null && !rewardFilter.equals("All")) {
                double reward = person.getRewardAmount();

                switch (rewardFilter) {
                    case "Reward Listed" -> remove = reward <= 0;
                    case "No Reward" -> remove = reward > 0;
                    case "$10,000+" -> remove = reward < 10000;
                    case "$50,000+" -> remove = reward < 50000;
                    case "$100,000+" -> remove = reward < 100000;
                }
            }

            return remove;
        });

        applySortOnly();

        currentPage = 1;
        renderPage();
    }

    private void applySortOnly() {
        if (allPeople == null || allPeople.isEmpty()) {
            return;
        }

        String selected = sortComboBox.getValue();

        switch (selected) {
            case "Reward: High to Low" -> allPeople.sort((a, b) -> {
                double rewardA = a.getRewardAmount();
                double rewardB = b.getRewardAmount();

                if (rewardA == 0 && rewardB == 0) return 0;
                if (rewardA == 0) return 1;
                if (rewardB == 0) return -1;

                return Double.compare(rewardB, rewardA);
            });

            case "Reward: Low to High" -> allPeople.sort((a, b) -> {
                double rewardA = a.getRewardAmount();
                double rewardB = b.getRewardAmount();

                if (rewardA == 0 && rewardB == 0) return 0;
                if (rewardA == 0) return 1;
                if (rewardB == 0) return -1;

                return Double.compare(rewardA, rewardB);
            });

            case "Name: A to Z" -> allPeople.sort(Comparator.comparing(p ->
                    p.getTitle() != null ? p.getTitle() : ""));

            case "Name: Z to A" -> allPeople.sort((a, b) -> {
                String titleA = a.getTitle() != null ? a.getTitle() : "";
                String titleB = b.getTitle() != null ? b.getTitle() : "";
                return titleB.compareTo(titleA);
            });

            default -> {
            }
        }
    }
  /**  @FXML
    private void handleSort() {
        if (allPeople == null || allPeople.isEmpty()) return;

        String selected = sortComboBox.getValue();

        switch (selected) {
            case "Reward: High to Low" -> allPeople.sort((a, b) -> {
                double rewardA = a.getRewardAmount();
                double rewardB = b.getRewardAmount();
                if (rewardA == 0 && rewardB == 0) return 0;
                if (rewardA == 0) return 1;
                if (rewardB == 0) return -1;
                return Double.compare(rewardB, rewardA);
            });
            case "Reward: Low to High" -> allPeople.sort((a, b) -> {
                double rewardA = a.getRewardAmount();
                double rewardB = b.getRewardAmount();
                if (rewardA == 0 && rewardB == 0) return 0;
                if (rewardA == 0) return 1;
                if (rewardB == 0) return -1;
                return Double.compare(rewardA, rewardB);
            });
            case "Name: A to Z" -> allPeople.sort(Comparator.comparing(p ->
                    p.getTitle() != null ? p.getTitle() : ""));
            case "Name: Z to A" -> allPeople.sort((a, b) -> {
                String titleA = a.getTitle() != null ? a.getTitle() : "";
                String titleB = b.getTitle() != null ? b.getTitle() : "";
                return titleB.compareTo(titleA);
            });
            default -> allPeople = new ArrayList<>(viewModel.getAllPeople());
        }

        currentPage = 1;
        renderPage();
    }
*/

    private void renderPage() {
        featuredCardsPane.getChildren().clear();

        if (allPeople == null || allPeople.isEmpty()) return;

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

    private void renderCards() {
        renderPage();
    }

    private VBox createCard(WantedPerson person) {
        Label nameLabel = new Label(person.getTitle());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);


        Label rewardLabel = new Label(person.getDisplayReward());
        rewardLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 14;");
        rewardLabel.setWrapText(true);

        AppUser currentUser = Session.getInstance().getCurrentUser();

        Label savedLabel = new Label(
                currentUser != null && currentUser.hasSavedTarget(person.getUid())
                        ? "★ Watchlist"
                        : ""
        );

        savedLabel.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 13; -fx-font-weight: bold;");
        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(false);

        String imageUrl = person.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            String proxyUrl = "https://fbi-backend-wilt.onrender.com/api/image/?url=" + imageUrl;
            imageView.setImage(new javafx.scene.image.Image(proxyUrl, true));
        }

        VBox card = new VBox(6, imageView, nameLabel, rewardLabel, savedLabel);
        card.setPadding(new Insets(12));
        card.setPrefWidth(270);
        card.setPrefHeight(280);
        card.setPickOnBounds(true);
        card.setStyle("-fx-background-color: #111827; -fx-background-radius: 14; -fx-border-color: #334155; -fx-border-radius: 14;");

        card.setOnMouseClicked(event -> {
            System.out.println("DASHBOARD CARD CLICKED: " + person.getTitle());
            viewModel.openCriminalProfile(card, person);
        });

        nameLabel.setOnMouseClicked(event -> {
            System.out.println("NAME CLICKED: " + person.getTitle());
            viewModel.openCriminalProfile(nameLabel, person);
        });

        return card;
    }

    @FXML
    private void showMapScreen() {
        viewModel.goToMap(featuredCardsPane);
    }

    @FXML
    private void showChartsScreen() {
        viewModel.goToCharts(featuredCardsPane);
    }


    @FXML
    private void goToUserProfile() {
        viewModel.goToUserProfile(featuredCardsPane);
    }

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