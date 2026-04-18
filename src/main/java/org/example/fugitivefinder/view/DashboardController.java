package org.example.fugitivefinder.view;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.DashboardViewModel;

import java.util.List;

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

        viewModel.getFugitiveLocations().addListener((javafx.collections.ListChangeListener<MapPoint>) change -> {
            Platform.runLater(this::renderMap);
        });

        viewModel.getAllPeople().addListener((javafx.collections.ListChangeListener<WantedPerson>) change -> {
            allPeople = viewModel.getAllPeople();
            Platform.runLater(() -> renderPage());
        });

        new Thread(() -> viewModel.loadData()).start();
    }

    private void renderMap() {
        if (mapView.getWidth() <= 0) {
            return;
        }
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