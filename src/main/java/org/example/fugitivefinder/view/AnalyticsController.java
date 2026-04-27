package org.example.fugitivefinder.view;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.fugitivefinder.viewModel.AnalyticsViewModel;
import org.example.fugitivefinder.viewModel.SceneManager;

/**
 * Controller for the Analytics view (analytics.fxml).
 * Binds FXML elements to the AnalyticsViewModel and manages
 * the loading state and chart population.
 *
 * Follows the same Controller-ViewModel pattern used by
 * DashboardController, RewardsController, etc.
 *
 * Note: Navigation handlers are placeholder stubs that will be
 * connected once the team's SceneManager is finalized
 * and merged to main.
 *
 * @author Sahil Kamal
 */
public class AnalyticsController {

    // --- FXML Injected Elements ---

    @FXML
    private BarChart<String, Number> subjectsChart;

    @FXML
    private PieChart fieldOfficeChart;

    @FXML
    private BarChart<String, Number> raceChart;

    @FXML
    private PieChart sexChart;

    @FXML
    private ProgressIndicator loadingSpinner;

    @FXML
    private VBox chartsContainer;

    @FXML
    private StackPane loadingOverlay;

    @FXML
    private Label statusLabel;

    @FXML
    private Label totalRecordsLabel;

    @FXML
    private Label recordsAnalyzedLabel;

    // --- ViewModel ---
    private AnalyticsViewModel viewModel;

    /**
     * Called automatically by the FXMLLoader after all @FXML fields are injected.
     * Sets up bindings between the view and the ViewModel, then triggers data loading.
     */
    @FXML
    public void initialize() {
        viewModel = new AnalyticsViewModel();

        // Bind loading state
        loadingOverlay.visibleProperty().bind(viewModel.loadingProperty());
        loadingOverlay.managedProperty().bind(viewModel.loadingProperty());
        chartsContainer.visibleProperty().bind(viewModel.loadingProperty().not());

        // Bind status text
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());
        totalRecordsLabel.textProperty().bind(viewModel.totalRecordsTextProperty());
        recordsAnalyzedLabel.textProperty().bind(viewModel.recordsAnalyzedTextProperty());

        // Configure charts appearance
        configureSubjectsChart();
        configureFieldOfficeChart();
        configureRaceChart();
        configureSexChart();

        // Load data from the API
        viewModel.loadData();

        // Listen for when data finishes loading to populate the charts
        viewModel.loadingProperty().addListener((obs, wasLoading, isLoading) -> {
            if (!isLoading) {
                populateCharts();
            }
        });
    }

    // ---------- Chart Configuration ----------

    private void configureSubjectsChart() {
        subjectsChart.setTitle("Crimes by Category");
        subjectsChart.setLegendVisible(false);
        subjectsChart.setAnimated(true);
        subjectsChart.getXAxis().setLabel("Crime Category");
        subjectsChart.getYAxis().setLabel("Number of Cases");
        subjectsChart.setCategoryGap(5);
        subjectsChart.setBarGap(2);
    }

    private void configureFieldOfficeChart() {
        fieldOfficeChart.setTitle("Cases by Field Office");
        fieldOfficeChart.setLegendVisible(true);
        fieldOfficeChart.setAnimated(true);
        fieldOfficeChart.setLabelsVisible(true);
        fieldOfficeChart.setStartAngle(90);
    }

    private void configureRaceChart() {
        raceChart.setTitle("Fugitives by Race/Ethnicity");
        raceChart.setLegendVisible(false);
        raceChart.setAnimated(true);
        raceChart.getXAxis().setLabel("Race/Ethnicity");
        raceChart.getYAxis().setLabel("Number of Persons");
        raceChart.setCategoryGap(10);
        raceChart.setBarGap(2);
    }

    private void configureSexChart() {
        sexChart.setTitle("Sex Distribution");
        sexChart.setLegendVisible(true);
        sexChart.setAnimated(true);
        sexChart.setLabelsVisible(true);
        sexChart.setStartAngle(90);
    }

    // ---------- Chart Population ----------

    /**
     * Populates all four charts with data from the ViewModel.
     * Called after the background data loading completes.
     */
    private void populateCharts() {
        // Populate subjects bar chart
        XYChart.Series<String, Number> subjectsSeries = new XYChart.Series<>();
        subjectsSeries.setName("Cases");
        subjectsSeries.getData().addAll(viewModel.getSubjectsData());
        subjectsChart.getData().clear();
        subjectsChart.getData().add(subjectsSeries);
        applyBarColors(subjectsSeries);

        // Populate field office pie chart
        fieldOfficeChart.setData(viewModel.getFieldOfficeData());
        applyPieColors(fieldOfficeChart);

        // Populate race bar chart
        XYChart.Series<String, Number> raceSeries = new XYChart.Series<>();
        raceSeries.setName("Persons");
        raceSeries.getData().addAll(viewModel.getRaceData());
        raceChart.getData().clear();
        raceChart.getData().add(raceSeries);
        applyBarColors(raceSeries);

        // Populate sex pie chart
        sexChart.setData(viewModel.getSexData());
        applyPieColors(sexChart);
    }

    /**
     * Applies a consistent color palette to bar chart data points.
     */
    private void applyBarColors(XYChart.Series<String, Number> series) {
        String[] colors = {
                "#4fd1c5", "#f59e0b", "#ef4444", "#8b5cf6",
                "#3b82f6", "#10b981", "#f97316", "#ec4899",
                "#06b6d4", "#a855f7"
        };

        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String, Number> data = series.getData().get(i);
            final int colorIndex = i % colors.length;

            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: " + colors[colorIndex] + ";");
            } else {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-bar-fill: " + colors[colorIndex] + ";");
                    }
                });
            }
        }
    }

    /**
     * Applies a consistent color palette to pie chart slices.
     */
    private void applyPieColors(PieChart chart) {
        String[] colors = {
                "#4fd1c5", "#f59e0b", "#ef4444", "#8b5cf6",
                "#3b82f6", "#10b981", "#f97316", "#ec4899",
                "#06b6d4", "#a855f7", "#14b8a6"
        };

        for (int i = 0; i < chart.getData().size(); i++) {
            PieChart.Data data = chart.getData().get(i);
            String color = colors[i % colors.length];
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
        }
    }

    // ---------- Navigation ----------

    @FXML
    private void goToDashboard() {
        SceneManager.switchScene(subjectsChart, "/org.example.fugitivefinder/dashboard.fxml", 1440, 900);
    }

    @FXML
    private void goToLeaderboard() {
        SceneManager.switchScene(subjectsChart, "/org.example.fugitivefinder/leaderboard.fxml", 1440, 900);
    }

    @FXML
    private void goToRewards() {
        SceneManager.switchScene(subjectsChart, "/org.example.fugitivefinder/rewards.fxml", 1440, 900);
    }

    @FXML
    private void goToUserProfile() {
        SceneManager.switchScene(subjectsChart, "/org.example.fugitivefinder/user-profile.fxml", 1440, 900);
    }

    @FXML
    private void goToMap() {
        SceneManager.switchScene(subjectsChart, "/org.example.fugitivefinder/maps-view.fxml", 1440, 900);
    }
}
