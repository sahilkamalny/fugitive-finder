package org.example.fugitivefinder.view;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MapController extends MapLayer {

    private final MapPoint mapPoint;
    private final Circle circle;

    public MapController(MapPoint mapPoint) {
        this.mapPoint = mapPoint;
        this.circle = new Circle(8, Color.RED);
        this.circle.setStroke(Color.BLACK);
        this.circle.setStrokeWidth(2);
        this.getChildren().add(circle);
    }

    @Override
    protected void layoutLayer() {
        // This converts Latitude/Longitude to X/Y pixels on the screen
        Point2D point2d = getMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude());
        circle.setTranslateX(point2d.getX());
        circle.setTranslateY(point2d.getY());
    }
}