package org.example.fugitivefinder.view;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

public class MapMarkerLayer extends MapLayer {

    private final MapPoint mapPoint;
    private final Circle circle;

    public MapMarkerLayer(MapPoint mapPoint, boolean isHeatMap, int count) {
        this.mapPoint = mapPoint;
        if (isHeatMap) {
            // Heat Map Style: Glowing Radial Gradient
            double radius = 12 + (count * 4.5);
            this.circle = new Circle(radius);
            RadialGradient heatGradient = new RadialGradient(0, 0, 0.5, 0.5, 1, true,
                    javafx.scene.paint.CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(239, 68, 68, 0.8)), // Bright Red/Orange
                    new Stop(1, Color.rgb(239, 68, 68, 0.0))); // Faded Edge
            this.circle.setFill(heatGradient);
        } else {
            // Standard Style: Solid Red Circle
            this.circle = new Circle(8, Color.RED);
            this.circle.setStroke(Color.BLACK);
            this.circle.setStrokeWidth(2);
        }

        this.getChildren().add(circle);

        this.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent != null) {
                this.markDirty();
            }
        });
    }


    @Override
    protected void layoutLayer() {
        super.layoutLayer();
        Point2D point2d = getMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude());
        if (point2d != null) {
            circle.setTranslateX(point2d.getX());
            circle.setTranslateY(point2d.getY());
        }
    }
}