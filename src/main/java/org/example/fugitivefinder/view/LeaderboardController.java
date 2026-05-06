package org.example.fugitivefinder.view;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.fugitivefinder.service.LeaderboardService.RankedFugitive;
import org.example.fugitivefinder.viewModel.LeaderboardViewModel;
import org.example.fugitivefinder.viewModel.SceneManager;

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
    @FXML private VBox loadingOverlay;
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

        // Ensure table columns expand to fill all available horizontal space
        leaderboardTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);

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
                        case "CRITICAL" -> "#dc2626"; // Darker red
                        case "HIGH" -> "#ef4444"; // Red
                        case "MODERATE" -> "#f59e0b"; // Orange
                        case "LOW" -> "#3b82f6"; // Blue
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
        regionCasesChart.setAnimated(false);

        regionPieChart.setTitle("Regional Distribution");
        regionPieChart.setLegendVisible(true);
        regionPieChart.setAnimated(false);

        regionRewardChart.setTitle("Avg Reward by Region ($)");
        regionRewardChart.setLegendVisible(false);
        regionRewardChart.setAnimated(false);

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


    // --- Navigation ---
    @FXML private void goToDashboard() {
        SceneManager.switchScene(leaderboardTable, "/org.example.fugitivefinder/dashboard.fxml", 1440, 900);
    }
    @FXML private void goToAnalytics() {
        SceneManager.switchScene(leaderboardTable, "/org.example.fugitivefinder/analytics.fxml", 1440, 900);
    }
    @FXML private void goToUserProfile() {
        SceneManager.switchScene(leaderboardTable, "/org.example.fugitivefinder/user-profile.fxml", 1440, 900);
    }
    @FXML private void goToMap() {
        SceneManager.switchScene(leaderboardTable, "/org.example.fugitivefinder/maps-view.fxml", 1440, 900);
    }

    @FXML
    private void signOut(MouseEvent event) {
        org.example.fugitivefinder.session.Session.getInstance().clear();
        SceneManager.switchScene((Node) event.getSource(), "/org.example.fugitivefinder/login.fxml", 1440, 900);
    }
}
