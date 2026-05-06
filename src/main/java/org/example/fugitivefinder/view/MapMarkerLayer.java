package org.example.fugitivefinder.view;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

public class MapMarkerLayer extends MapLayer {

    private final MapPoint mapPoint;

    public MapMarkerLayer(MapPoint mapPoint, boolean isHeatMap, int count) {
        this.mapPoint = mapPoint;
        
        if (isHeatMap) {
            double radius = 12 + (count * 4.5);
            Circle glow = new Circle(radius);
            RadialGradient heatGradient = new RadialGradient(0, 0, 0.5, 0.5, 1, true,
                    javafx.scene.paint.CycleMethod.NO_CYCLE,
                    new Stop(0, Color.RED),
                    new Stop(1, Color.TRANSPARENT));
            glow.setFill(heatGradient);
            glow.setOpacity(0.4);

            //inner circle
            Circle core = new Circle(7, Color.WHITE);
            core.setStroke(Color.RED);
            core.setStrokeWidth(2.5);
            
            this.getChildren().addAll(glow, core);
        } else {
            //Markers for regular Map
            Circle marker = new Circle(8, Color.RED);
            marker.setStroke(Color.BLACK);
            marker.setStrokeWidth(2);
            this.getChildren().add(marker);
        }

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
            // Move every visual component to the correct map coordinate
            for (Node node : getChildren()) {
                node.setTranslateX(point2d.getX());
                node.setTranslateY(point2d.getY());
            }
        }
    }
}