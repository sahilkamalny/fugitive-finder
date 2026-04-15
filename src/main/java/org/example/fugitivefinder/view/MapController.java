package org.example.fugitivefinder.view;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapController extends MapLayer {
    private final Map<MapPoint, Circle> circles = new HashMap<>();



    public MapController(List<MapPoint> points) {
        for (MapPoint point : points) {
            Circle circle = new Circle(8, Color.RED);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);

            circles.put(point, circle);
            this.getChildren().add(circle);
        }
    }

    @Override
    protected void layoutLayer() {
        circles.forEach((point, circle) -> {
            Point2D point2d = getMapPoint(point.getLatitude(), point.getLongitude());
            if (point2d != null) {
                circle.setTranslateX(point2d.getX());
                circle.setTranslateY(point2d.getY());
            }
        });
    }
}