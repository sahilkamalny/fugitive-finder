package org.example.fugitivefinder.view;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.MapsViewModel;
import javafx.scene.control.ComboBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.control.TextField;

import static javafx.util.Duration.millis;

public class MapsViewController {
    @FXML
    private StackPane mapContainer;
    private MapView mapView;
    private MapsViewModel viewModel;
    private boolean isHeatMapMode = false;
    private final List<MapLayer> activeLayers = new ArrayList<>();

    @FXML
    private ComboBox<String> officeFilterComboBox;

    @FXML
    private TextField nameSearchField;
    @FXML
    public void initialize() {
        viewModel = new MapsViewModel();
        setUpMap();
        setupOfficeFilter();
        new Thread(viewModel::loadMapData).start();

    }

    public void setUpMap() {
        mapView = new MapView();
        mapView.setCenter(new MapPoint(38, -98.5795));
        mapView.setZoom(4.8);
        mapContainer.getChildren().add(mapView);
        setupListeners();
        setupMarkerClicks();
    }

    private void setupListeners() {
        viewModel.getFugitiveLocations().addListener((ListChangeListener<MapPoint>) change -> {
            Platform.runLater(this::renderMap);
        });
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

    private void renderMap() {
        if (mapView.getWidth() <= 0) return;

        for (MapLayer layer : activeLayers) {
            mapView.removeLayer(layer);
        }
        activeLayers.clear();

        if (isHeatMapMode) {
            viewModel.getLocationGroups().forEach((point, people) -> {
                MapController layer=(new MapController(point, true, people.size()));
                activeLayers.add(layer);
                mapView.addLayer(layer);
            });
        } else {
            for (MapPoint point : viewModel.getFugitiveLocations()) {
                MapController layer = new MapController(point, false, 0);
                activeLayers.add(layer);
                mapView.addLayer(layer);
            }
        }
    }

    private void setupOfficeFilter() {
        officeFilterComboBox.getItems().add("All Offices");
        officeFilterComboBox.getItems().addAll(viewModel.getOfficeCoordinates().keySet());
        officeFilterComboBox.setValue("All Offices");
    }

    @FXML
    private void handleOfficeFilter() {
        String selectedOffice = officeFilterComboBox.getValue();

        if (selectedOffice == null || selectedOffice.equals("All Offices")) {
            viewModel.clearOfficeFilter();
        } else {
            viewModel.filterByOffice(selectedOffice);
        }

        renderMap();
    }

    @FXML
    private void handleNameSearch() {
        String name = nameSearchField.getText();

        if (name == null || name.isBlank()) {
            viewModel.clearNameSearch();
        } else {
            viewModel.filterByName(name);
        }

        renderMap();
    }

    @FXML
    private void clearNameSearch() {
        nameSearchField.clear();
        viewModel.clearNameSearch();
        renderMap();
    }
    @FXML
    private void clearOfficeFilter() {
        officeFilterComboBox.setValue("All Offices");
        viewModel.clearOfficeFilter();
        renderMap();
    }
    @FXML
    private void toggleHeatMap() {
        isHeatMapMode = !isHeatMapMode;
        renderMap();
    }

    private VBox createMapCard(WantedPerson person) {
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
        card.setOnMouseClicked(event -> viewModel.openCriminalProfile(mapContainer, person));

        return card;
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
        infoPane.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.6)));

        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label title = new Label(officeName.toUpperCase() + " OFFICE");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        title.setWrapText(true);
        title.setMaxWidth(240);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

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

        ScrollPane scroll = new ScrollPane();
        VBox cardsContainer = new VBox(12);
        cardsContainer.setPadding(new Insets(5));
        cardsContainer.setStyle("-fx-background-color: transparent;");

        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        for (WantedPerson person : localFugitives) {
            VBox card = createMapCard(person);
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

        StackPane.setAlignment(infoPane, Pos.CENTER_RIGHT);
        StackPane.setMargin(infoPane, new Insets(20));

        infoPane.setTranslateX(400);
        TranslateTransition transition = new TranslateTransition(
                millis(300), infoPane);
        transition.setToX(0);
        transition.play();
    }

    @FXML
    private void goToDashboard() {
        viewModel.goToDashboard(mapContainer);    }

    @FXML
    private void goToCharts() {
        viewModel.goToCharts(mapContainer);    }



    @FXML
    private void goToUserProfile() {
        viewModel.goToUserProfile(mapContainer);    }


}