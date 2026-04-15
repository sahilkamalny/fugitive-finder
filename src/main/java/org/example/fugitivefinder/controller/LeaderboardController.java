package org.example.fugitivefinder.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.fugitivefinder.service.LeaderboardService.RankedFugitive;
import org.example.fugitivefinder.viewModel.LeaderboardViewModel;

/**
 * Controller for the Leaderboard & Region Stats view.
 * Binds the leaderboard table and region charts to the ViewModel.
 *
 * @author Sahil Kamal
 */
public class LeaderboardController {

    @FXML private TableView<RankedFugitive> leaderboardTable;
    @FXML private TableColumn<RankedFugitive, Integer> rankColumn;
    @FXML private TableColumn<RankedFugitive, String> nameColumn;
    @FXML private TableColumn<RankedFugitive, String> rewardColumn;
    @FXML private TableColumn<RankedFugitive, String> dangerColumn;
    @FXML private TableColumn<RankedFugitive, String> officeColumn;

    @FXML private BarChart<String, Number> regionCasesChart;
    @FXML private PieChart regionPieChart;
    @FXML private BarChart<String, Number> regionRewardChart;

    @FXML private ProgressIndicator loadingSpinner;
    @FXML private VBox contentContainer;
    @FXML private StackPane loadingOverlay;
    @FXML private Label statusLabel;

    @FXML private Label highestRewardLabel;
    @FXML private Label mostDangerousRegionLabel;
    @FXML private Label totalAnalyzedLabel;
    @FXML private Label criticalCountLabel;

    private LeaderboardViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new LeaderboardViewModel();

        // Bind loading
        loadingOverlay.visibleProperty().bind(viewModel.loadingProperty());
        loadingOverlay.managedProperty().bind(viewModel.loadingProperty());
        contentContainer.visibleProperty().bind(viewModel.loadingProperty().not());
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        // Bind stats cards
        highestRewardLabel.textProperty().bind(viewModel.highestRewardTextProperty());
        mostDangerousRegionLabel.textProperty().bind(viewModel.mostDangerousRegionProperty());
        totalAnalyzedLabel.textProperty().bind(viewModel.totalFugitivesAnalyzedProperty());
        criticalCountLabel.textProperty().bind(viewModel.criticalThreatCountProperty());

        // Configure table columns
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        rewardColumn.setCellValueFactory(new PropertyValueFactory<>("rewardText"));
        dangerColumn.setCellValueFactory(new PropertyValueFactory<>("dangerLevel"));
        officeColumn.setCellValueFactory(new PropertyValueFactory<>("fieldOffice"));

        // Style danger column with colors
        dangerColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String level, boolean empty) {
                super.updateItem(level, empty);
                if (empty || level == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(level);
                    String color = switch (level) {
                        case "CRITICAL" -> "#ef4444";
                        case "HIGH" -> "#f59e0b";
                        case "MODERATE" -> "#3b82f6";
                        case "LOW" -> "#10b981";
                        default -> "#94a3b8";
                    };
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });

        leaderboardTable.setItems(viewModel.getLeaderboardData());

        // Configure charts
        regionCasesChart.setTitle("Cases by Region");
        regionCasesChart.setLegendVisible(false);
        regionCasesChart.setAnimated(true);

        regionPieChart.setTitle("Regional Distribution");
        regionPieChart.setLegendVisible(true);
        regionPieChart.setAnimated(true);

        regionRewardChart.setTitle("Avg Reward by Region ($)");
        regionRewardChart.setLegendVisible(false);
        regionRewardChart.setAnimated(true);

        // Listen for data load
        viewModel.loadingProperty().addListener((obs, was, is) -> {
            if (!is) populateCharts();
        });

        viewModel.loadData();
    }

    private void populateCharts() {
        // Region cases bar chart
        XYChart.Series<String, Number> casesSeries = new XYChart.Series<>();
        casesSeries.setName("Cases");
        casesSeries.getData().addAll(viewModel.getRegionCasesData());
        regionCasesChart.getData().clear();
        regionCasesChart.getData().add(casesSeries);
        applyBarColors(casesSeries);

        // Region pie chart
        regionPieChart.setData(viewModel.getRegionDistributionData());
        applyPieColors(regionPieChart);

        // Region reward bar chart
        XYChart.Series<String, Number> rewardSeries = new XYChart.Series<>();
        rewardSeries.setName("Avg Reward");
        rewardSeries.getData().addAll(viewModel.getRegionRewardData());
        regionRewardChart.getData().clear();
        regionRewardChart.getData().add(rewardSeries);
        applyBarColors(rewardSeries);
    }

    private void applyBarColors(XYChart.Series<String, Number> series) {
        String[] colors = {"#4fd1c5", "#f59e0b", "#ef4444", "#8b5cf6"};
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String, Number> data = series.getData().get(i);
            final int ci = i % colors.length;
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: " + colors[ci] + ";");
            } else {
                data.nodeProperty().addListener((o, old, n) -> {
                    if (n != null) n.setStyle("-fx-bar-fill: " + colors[ci] + ";");
                });
            }
        }
    }

    private void applyPieColors(PieChart chart) {
        String[] colors = {"#4fd1c5", "#f59e0b", "#ef4444", "#8b5cf6"};
        for (int i = 0; i < chart.getData().size(); i++) {
            chart.getData().get(i).getNode()
                    .setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
        }
    }

    // --- Navigation Stubs ---
    @FXML private void goToDashboard() {
        System.out.println("[Leaderboard] Navigate to Dashboard — pending integration");
    }
    @FXML private void goToAnalytics() {
        System.out.println("[Leaderboard] Navigate to Analytics — pending integration");
    }
    @FXML private void goToRewards() {
        System.out.println("[Leaderboard] Navigate to Rewards — pending integration");
    }
    @FXML private void goToUserProfile() {
        System.out.println("[Leaderboard] Navigate to Profile — pending integration");
    }
}
