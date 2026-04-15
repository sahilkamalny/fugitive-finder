package org.example.fugitivefinder.viewModel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import org.example.fugitivefinder.service.LeaderboardService;
import org.example.fugitivefinder.service.LeaderboardService.RankedFugitive;
import org.example.fugitivefinder.service.RegionStatsService;
import org.example.fugitivefinder.service.RegionStatsService.RegionStat;

import java.util.List;
import java.util.Map;

/**
 * ViewModel for the Leaderboard & Region Stats screen.
 * Loads data on a background thread and exposes observable
 * properties for the leaderboard table and region charts.
 *
 * @author Sahil Kamal
 */
public class LeaderboardViewModel {

    // --- Loading State ---
    private final BooleanProperty loading = new SimpleBooleanProperty(true);
    private final StringProperty statusMessage = new SimpleStringProperty("Loading leaderboard...");

    // --- Stats Cards ---
    private final StringProperty highestRewardText = new SimpleStringProperty("--");
    private final StringProperty mostDangerousRegion = new SimpleStringProperty("--");
    private final StringProperty totalFugitivesAnalyzed = new SimpleStringProperty("--");
    private final StringProperty criticalThreatCount = new SimpleStringProperty("--");

    // --- Leaderboard Table Data ---
    private final ObservableList<RankedFugitive> leaderboardData =
            FXCollections.observableArrayList();

    // --- Region Chart Data ---
    private final ObservableList<XYChart.Data<String, Number>> regionCasesData =
            FXCollections.observableArrayList();

    private final ObservableList<PieChart.Data> regionDistributionData =
            FXCollections.observableArrayList();

    private final ObservableList<XYChart.Data<String, Number>> regionRewardData =
            FXCollections.observableArrayList();

    // --- Services ---
    private final LeaderboardService leaderboardService;
    private final RegionStatsService regionStatsService;

    public LeaderboardViewModel() {
        this.leaderboardService = new LeaderboardService();
        this.regionStatsService = new RegionStatsService();
    }

    // --- Property Getters ---

    public BooleanProperty loadingProperty() { return loading; }
    public StringProperty statusMessageProperty() { return statusMessage; }
    public StringProperty highestRewardTextProperty() { return highestRewardText; }
    public StringProperty mostDangerousRegionProperty() { return mostDangerousRegion; }
    public StringProperty totalFugitivesAnalyzedProperty() { return totalFugitivesAnalyzed; }
    public StringProperty criticalThreatCountProperty() { return criticalThreatCount; }
    public ObservableList<RankedFugitive> getLeaderboardData() { return leaderboardData; }
    public ObservableList<XYChart.Data<String, Number>> getRegionCasesData() { return regionCasesData; }
    public ObservableList<PieChart.Data> getRegionDistributionData() { return regionDistributionData; }
    public ObservableList<XYChart.Data<String, Number>> getRegionRewardData() { return regionRewardData; }

    // --- Data Loading ---

    /**
     * Loads leaderboard and region data on a background thread.
     */
    public void loadData() {
        loading.set(true);
        statusMessage.set("Fetching data from FBI API...");

        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    // Fetch leaderboard (top 20)
                    List<RankedFugitive> topFugitives = leaderboardService.getMostDangerous(20);

                    // Fetch region stats
                    Map<String, RegionStat> regionStats = regionStatsService.getRegionStats();

                    Platform.runLater(() -> {
                        // Populate leaderboard
                        leaderboardData.setAll(topFugitives);

                        // Stats cards
                        if (!topFugitives.isEmpty()) {
                            RankedFugitive top = topFugitives.get(0);
                            highestRewardText.set(top.getRewardText());
                        }

                        totalFugitivesAnalyzed.set(String.valueOf(
                                topFugitives.size() > 0 ? "200+" : "0"));

                        long criticalCount = topFugitives.stream()
                                .filter(f -> f.getDangerLevel().equals("CRITICAL"))
                                .count();
                        criticalThreatCount.set(String.valueOf(criticalCount));

                        // Region charts
                        regionCasesData.clear();
                        regionDistributionData.clear();
                        regionRewardData.clear();

                        String topRegion = "";
                        int topRegionCases = 0;

                        for (Map.Entry<String, RegionStat> entry : regionStats.entrySet()) {
                            RegionStat stat = entry.getValue();
                            String name = stat.getRegionName();

                            regionCasesData.add(new XYChart.Data<>(name, stat.getTotalCases()));

                            regionDistributionData.add(new PieChart.Data(
                                    name + " (" + stat.getTotalCases() + ")",
                                    stat.getTotalCases()));

                            regionRewardData.add(new XYChart.Data<>(
                                    name, (int) stat.getAvgReward()));

                            if (stat.getTotalCases() > topRegionCases) {
                                topRegionCases = stat.getTotalCases();
                                topRegion = name;
                            }
                        }

                        mostDangerousRegion.set(topRegion);
                        loading.set(false);
                        statusMessage.set("Data loaded successfully");
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        loading.set(false);
                        statusMessage.set("Error: " + e.getMessage());
                    });
                }
                return null;
            }
        };

        Thread thread = new Thread(fetchTask);
        thread.setDaemon(true);
        thread.start();
    }
}
