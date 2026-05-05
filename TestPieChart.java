import javafx.application.Platform;
import javafx.scene.chart.PieChart;

public class TestPieChart {
    public static void main(String[] args) {
        Platform.startup(() -> {
            try {
                PieChart chart = new PieChart();
                PieChart.Data data = new PieChart.Data("Test", 0);
                chart.getData().add(data);
                System.out.println("Node: " + data.getNode());
                Platform.exit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
