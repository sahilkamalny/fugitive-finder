package org.example.fugitivefinder.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.DashboardViewModel;

public class DashboardController {

    @FXML private Label usernameLabel;
    @FXML private Label totalWantedLabel;
    @FXML private Label rewardCasesLabel;
    @FXML private Label updatesLabel;
    @FXML private FlowPane featuredCardsPane;
    @FXML private StackPane mapContainer;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private ComboBox<String> sortComboBox;

    private DashboardViewModel viewModel;
    private MapView mapView;

    private List<WantedPerson> allPeople;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 16;

    @FXML
    public void initialize() {
        viewModel = new DashboardViewModel();

        usernameLabel.textProperty().bind(viewModel.usernameProperty());
        totalWantedLabel.textProperty().bind(viewModel.totalWantedProperty());
        rewardCasesLabel.textProperty().bind(viewModel.rewardCasesProperty());
        updatesLabel.textProperty().bind(viewModel.updatesProperty());

        mapView = new MapView();
        mapView.setCenter(new MapPoint(38, -98.5795));
        mapView.setZoom(4.8);
        mapContainer.getChildren().add(mapView);

        setupMarkerClicks();

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

        viewModel.getFugitiveLocations().addListener((javafx.collections.ListChangeListener<MapPoint>) change -> {
            Platform.runLater(this::renderMap);
        });

        viewModel.getAllPeople().addListener((javafx.collections.ListChangeListener<WantedPerson>) change -> {
            allPeople = new ArrayList<>(viewModel.getAllPeople());
            Platform.runLater(() -> renderPage());
        });

        new Thread(() -> {
            viewModel.loadData();
            Platform.runLater(() -> {
                allPeople = new ArrayList<>(viewModel.getAllPeople());
                renderPage();
            });
        }).start();
    }

    @FXML
    private void handleSort() {
        if (allPeople == null || allPeople.isEmpty()) return;

        String selected = sortComboBox.getValue();

        switch (selected) {
            case "Reward: High to Low" ->
                    allPeople.sort((a, b) -> {
                        double rewardA = a.getRewardAmount();
                        double rewardB = b.getRewardAmount();
                        if (rewardA == 0 && rewardB == 0) return 0;
                        if (rewardA == 0) return 1;
                        if (rewardB == 0) return -1;
                        return Double.compare(rewardB, rewardA);
                    });
            case "Reward: Low to High" ->
                    allPeople.sort((a, b) -> {
                        double rewardA = a.getRewardAmount();
                        double rewardB = b.getRewardAmount();
                        if (rewardA == 0 && rewardB == 0) return 0;
                        if (rewardA == 0) return 1;
                        if (rewardB == 0) return -1;
                        return Double.compare(rewardA, rewardB);
                    });
            case "Name: A to Z" ->
                    allPeople.sort(Comparator.comparing(p ->
                            p.getTitle() != null ? p.getTitle() : ""));
            case "Name: Z to A" ->
                    allPeople.sort((a, b) -> {
                        String titleA = a.getTitle() != null ? a.getTitle() : "";
                        String titleB = b.getTitle() != null ? b.getTitle() : "";
                        return titleB.compareTo(titleA);
                    });
            default -> allPeople = new ArrayList<>(viewModel.getAllPeople());
        }

        currentPage = 1;
        renderPage();
    }

    private void setupMarkerClicks() {
        mapView.setOnMouseClicked(event -> {
            MapPoint clickedPoint = mapView.getMapPosition(event.getX(), event.getY());
            for (Map.Entry<String, MapPoint> entry : viewModel.getOfficeCoordinates().entrySet()) {
                MapPoint officePoint = entry.getValue();
                if (isNear(clickedPoint, officePoint)) {
                    String officeName = entry.getKey();
                    showFugitivesForOffice(officeName);
                    break;
                }
            }
        });
    }

    private boolean isNear(MapPoint p1, MapPoint p2) {
        double threshold = 0.2;
        return Math.abs(p1.getLatitude() - p2.getLatitude()) < threshold &&
                Math.abs(p1.getLongitude() - p2.getLongitude()) < threshold;
    }

    private void showFugitivesForOffice(String officeName) {
        List<WantedPerson> localFugitives = viewModel.getWantedPeopleList().stream()
                .filter(f -> f.getFieldOffices() != null && !f.getFieldOffices().isEmpty())
                .filter(f -> f.getFieldOffices().get(0).toLowerCase().replace(" ", "").equals(officeName))
                .toList();
        if (localFugitives.isEmpty()) return;

        VBox infoPane = new VBox(15);
        infoPane.setId("officeInfoPane");
        infoPane.setPadding(new Insets(20));
        infoPane.setStyle("-fx-background-color: rgba(17, 24, 39, 0.8); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(75, 85, 99, 0.5); " +
                "-fx-border-width: 1.5;");
        infoPane.setPrefWidth(320);
        infoPane.setMaxWidth(320);
        infoPane.setMaxHeight(560);
        infoPane.setEffect(new javafx.scene.effect.DropShadow(15, javafx.scene.paint.Color.rgb(0,0,0,0.6)));

        javafx.scene.layout.HBox header = new javafx.scene.layout.HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label title = new Label(officeName.toUpperCase() + " OFFICE");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        title.setWrapText(true);
        title.setMaxWidth(240);

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: rgba(55, 65, 81, 0.7); -fx-text-fill: white; " +
                "-fx-background-radius: 50; -fx-cursor: hand;");
        closeBtn.setPrefSize(30, 30);
        closeBtn.setFocusTraversable(false);
        closeBtn.setOnAction(e -> {
            mapContainer.getChildren().remove(infoPane);
            mapContainer.requestFocus();
        });

        header.getChildren().addAll(title, spacer, closeBtn);
        infoPane.getChildren().add(header);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane();
        VBox cardsContainer = new VBox(12);
        cardsContainer.setPadding(new Insets(5));
        cardsContainer.setStyle("-fx-background-color: transparent;");

        scroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);

        for (WantedPerson person : localFugitives) {
            VBox card = createCard(person);
            card.setMinWidth(290);
            card.setMaxWidth(290);
            cardsContainer.getChildren().add(card);
        }

        scroll.setContent(cardsContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; " +
                "-fx-viewport-background: transparent;");

        infoPane.getChildren().add(scroll);

        mapContainer.getChildren().removeIf(node -> "officeInfoPane".equals(node.getId()));
        mapContainer.getChildren().add(infoPane);

        StackPane.setAlignment(infoPane, javafx.geometry.Pos.CENTER_RIGHT);
        StackPane.setMargin(infoPane, new Insets(20));

        infoPane.setTranslateX(400);
        javafx.animation.TranslateTransition transition = new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(300), infoPane);
        transition.setToX(0);
        transition.play();
    }

    private void renderMap() {
        if (mapView.getWidth() <= 0) return;
        for (MapPoint point : viewModel.getFugitiveLocations()) {
            mapView.addLayer(new MapController(point));
        }
    }

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

        VBox card = new VBox(6, nameLabel, rewardLabel);
        card.setPadding(new Insets(12));
        card.setPrefWidth(270);
        card.setPrefHeight(100);
        card.setStyle("-fx-background-color: #111827; -fx-background-radius: 14; -fx-border-color: #334155; -fx-border-radius: 14;");
        card.setOnMouseClicked(event -> viewModel.openCriminalProfile(featuredCardsPane, person));

        return card;
    }

    @FXML
    private void goToRewards() {
        viewModel.goToRewards(featuredCardsPane);
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