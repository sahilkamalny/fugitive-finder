package org.example.fugitivefinder.controller;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;


public class DashboardController {
    @FXML
    private StackPane mapContainer;

    private MapView mapView;

    public void initialize() {
        mapView = new MapView();
        MapPoint usaCenter = new MapPoint(39.8283, -98.5795);
        mapView.setCenter(usaCenter);
        mapView.setZoom(3);
        mapView.addLayer(new MapController(new MapPoint(40.7506, -73.4290)));

        // Marker in Huntington
        mapView.addLayer(new MapController(new MapPoint(40.8682, -73.4257)));

        // Marker in Babylon
        mapView.addLayer(new MapController(new MapPoint(40.6959, -73.3257)));


        mapContainer.getChildren().add(mapView);
    }
}

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.DashboardViewModel;

public class DashboardController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label totalWantedLabel;

    @FXML
    private Label rewardCasesLabel;

    @FXML
    private Label updatesLabel;

    @FXML
    private FlowPane featuredCardsPane;

    private DashboardViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new DashboardViewModel();

        usernameLabel.textProperty().bind(viewModel.usernameProperty());
        totalWantedLabel.textProperty().bind(viewModel.totalWantedProperty());
        rewardCasesLabel.textProperty().bind(viewModel.rewardCasesProperty());
        updatesLabel.textProperty().bind(viewModel.updatesProperty());

        viewModel.loadData();
        renderCards();
    }

    private void renderCards() {
        featuredCardsPane.getChildren().clear();

        for (WantedPerson person : viewModel.getFeaturedTargets()) {
            featuredCardsPane.getChildren().add(createCard(person));
        }
    }

    private VBox createCard(WantedPerson person) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(190);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);

        String imageUrl = person.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            imageView.setImage(new Image(imageUrl, true));
        }

        Label nameLabel = new Label(person.getTitle());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Label rewardLabel = new Label(person.getDisplayReward());
        rewardLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 14;");

        VBox details = new VBox(6, nameLabel, rewardLabel);
        details.setPadding(new Insets(10));

        VBox card = new VBox(imageView, details);
        card.setPrefWidth(190);
        card.setPrefHeight(250);
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
