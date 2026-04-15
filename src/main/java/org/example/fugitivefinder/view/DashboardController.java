package org.example.fugitivefinder.view;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
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

    @FXML
    private StackPane mapContainer;

    @FXML
    private ComboBox<String> crimeTypeComboBox;

    @FXML
    private ComboBox<String> sexComboBox;

    @FXML
    private TextField cityField;

    @FXML
    private Slider minAgeSlider;

    @FXML
    private Slider maxAgeSlider;

    @FXML
    private Label minAgeLabel;

    @FXML
    private Label maxAgeLabel;

    @FXML
    private CheckBox rewardOnlyCheckBox;

    @FXML
    private CheckBox warningOnlyCheckBox;

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

        viewModel.loadData();

        setupFilters();
        renderMap();
        renderCards();
    }

    private void setupFilters() {
        crimeTypeComboBox.setItems(viewModel.getCrimeTypeOptions());
        sexComboBox.setItems(viewModel.getSexOptions());

        crimeTypeComboBox.valueProperty().bindBidirectional(viewModel.selectedCrimeTypeProperty());
        sexComboBox.valueProperty().bindBidirectional(viewModel.selectedSexProperty());
        cityField.textProperty().bindBidirectional(viewModel.cityQueryProperty());

        minAgeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.minAgeProperty().set(newVal.intValue());
            minAgeLabel.setText(String.valueOf(newVal.intValue()));
            refreshDashboard();
        });

        maxAgeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.maxAgeProperty().set(newVal.intValue());
            maxAgeLabel.setText(String.valueOf(newVal.intValue()));
            refreshDashboard();
        });

        rewardOnlyCheckBox.selectedProperty().bindBidirectional(viewModel.rewardOnlyProperty());
        warningOnlyCheckBox.selectedProperty().bindBidirectional(viewModel.warningOnlyProperty());

        crimeTypeComboBox.setValue("All");
        sexComboBox.setValue("All");
        minAgeSlider.setValue(0);
        maxAgeSlider.setValue(100);
        minAgeLabel.setText("0");
        maxAgeLabel.setText("100");

        crimeTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> refreshDashboard());
        sexComboBox.valueProperty().addListener((obs, oldVal, newVal) -> refreshDashboard());
        cityField.textProperty().addListener((obs, oldVal, newVal) -> refreshDashboard());
        rewardOnlyCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refreshDashboard());
        warningOnlyCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> refreshDashboard());
    }

    private void refreshDashboard() {
        if (viewModel.minAgeProperty().get() > viewModel.maxAgeProperty().get()) {
            return;
        }

        viewModel.applyFilters();
        renderMap();
        renderCards();
    }

    private void renderMap() {
        mapContainer.getChildren().clear();
        mapView = new MapView();
        mapView.setCenter(new MapPoint(39.8283, -98.5795));
        mapView.setZoom(3);

        for (MapPoint point : viewModel.getFugitiveLocations()) {
            mapView.addLayer(new MapController(point));
        }

        mapContainer.getChildren().add(mapView);
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