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

