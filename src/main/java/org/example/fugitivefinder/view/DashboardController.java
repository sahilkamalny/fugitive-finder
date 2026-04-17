package org.example.fugitivefinder.view;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

    private DashboardViewModel viewModel;
    private MapView mapView;

    @FXML
    public void initialize() {
        viewModel = new DashboardViewModel();

        usernameLabel.textProperty().bind(viewModel.usernameProperty());
        totalWantedLabel.textProperty().bind(viewModel.totalWantedProperty());
        rewardCasesLabel.textProperty().bind(viewModel.rewardCasesProperty());
        updatesLabel.textProperty().bind(viewModel.updatesProperty());

        mapView = new MapView();
        mapView.setCenter(new MapPoint(39.8283, -98.5795));
        mapView.setZoom(3);
        mapContainer.getChildren().add(mapView);

        viewModel.getFeaturedTargets().addListener((javafx.collections.ListChangeListener<WantedPerson>) change -> {
            renderCards();
        });

        viewModel.getFugitiveLocations().addListener((javafx.collections.ListChangeListener<MapPoint>) change -> {
            renderMap();
        });

        new Thread(() -> viewModel.loadData()).start();
    }

    private void renderMap() {
        for (MapPoint point : viewModel.getFugitiveLocations()) {
            mapView.addLayer(new MapController(point));
        }
    }

    private void renderCards() {
        featuredCardsPane.getChildren().clear();
        for (WantedPerson person : viewModel.getFeaturedTargets()) {
            featuredCardsPane.getChildren().add(createCard(person));
        }
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
}