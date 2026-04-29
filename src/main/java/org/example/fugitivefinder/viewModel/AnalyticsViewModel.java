package org.example.fugitivefinder.viewModel;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import org.example.fugitivefinder.service.ChartDataService;
import org.json.JSONObject;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.FbiApiService;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for the Analytics screen.
 * Manages chart data as observable properties and fetches data
 * from the FBI API on a background thread.
 *
 * Follows the MVVM pattern established by the team.
 * Navigation is intentionally excluded — it will be integrated
 * when the team's SceneManager is finalized in a later sprint.
 *
 * @author Sahil Kamal
 */
public class AnalyticsViewModel {

    // --- Observable Properties ---
    private final BooleanProperty loading = new SimpleBooleanProperty(true);
    private final StringProperty statusMessage = new SimpleStringProperty("Loading data from FBI API...");
    private final StringProperty totalRecordsText = new SimpleStringProperty("--");
    private final StringProperty recordsAnalyzedText = new SimpleStringProperty("--");

    // --- Chart Data ---
    private final ObservableList<XYChart.Data<String, Number>> subjectsData =
            FXCollections.observableArrayList();

    private final ObservableList<PieChart.Data> fieldOfficeData =
            FXCollections.observableArrayList();

    private final ObservableList<XYChart.Data<String, Number>> raceData =
            FXCollections.observableArrayList();

    private final ObservableList<PieChart.Data> sexData =
            FXCollections.observableArrayList();

    // --- Service ---
    private final ChartDataService chartDataService;

    public AnalyticsViewModel() {
        this.chartDataService = new ChartDataService();
    }

    // ---------- Property Getters ----------

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public StringProperty totalRecordsTextProperty() {
        return totalRecordsText;
    }

    public StringProperty recordsAnalyzedTextProperty() {
        return recordsAnalyzedText;
    }

    public ObservableList<XYChart.Data<String, Number>> getSubjectsData() {
        return subjectsData;
    }

    public ObservableList<PieChart.Data> getFieldOfficeData() {
        return fieldOfficeData;
    }

    public ObservableList<XYChart.Data<String, Number>> getRaceData() {
        return raceData;
    }

    public ObservableList<PieChart.Data> getSexData() {
        return sexData;
    }

    // ---------- Data Loading ----------

    /**
     * Loads all chart data from the FBI API on a background thread.
     * Updates the observable properties on the JavaFX Application Thread
     * when data is ready.
     */
  /**  public void loadData() {
        loading.set(true);
        statusMessage.set("Fetching data from FBI API...");

        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    // Fetch data from the API (200 records across 4 pages)
                    List<JSONObject> items = chartDataService.fetchAllItems();
                    int totalCount = chartDataService.getTotalRecordCount();

                    // Compute all distributions
                    Map<String, Integer> subjects = chartDataService.getSubjectsDistribution(items);
                    Map<String, Integer> offices = chartDataService.getFieldOfficeDistribution(items, 8);
                    Map<String, Integer> races = chartDataService.getRaceDistribution(items);
                    Map<String, Integer> sexes = chartDataService.getSexDistribution(items);

                    // Update UI on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        // Update stats
                        totalRecordsText.set(String.valueOf(totalCount));
                        recordsAnalyzedText.set(String.valueOf(items.size()));

                        // Populate subjects bar chart (top 10)
                        subjectsData.clear();
                        int subjectCount = 0;
                        for (Map.Entry<String, Integer> entry : subjects.entrySet()) {
                            if (subjectCount >= 10) break;
                            subjectsData.add(new XYChart.Data<>(
                                    shortenLabel(entry.getKey()), entry.getValue()));
                            subjectCount++;
                        }

                        // Populate field office pie chart
                        fieldOfficeData.clear();
                        for (Map.Entry<String, Integer> entry : offices.entrySet()) {
                            fieldOfficeData.add(new PieChart.Data(
                                    entry.getKey() + " (" + entry.getValue() + ")",
                                    entry.getValue()));
                        }

                        // Populate race bar chart
                        raceData.clear();
                        for (Map.Entry<String, Integer> entry : races.entrySet()) {
                            raceData.add(new XYChart.Data<>(
                                    entry.getKey(), entry.getValue()));
                        }

                        // Populate sex pie chart
                        sexData.clear();
                        for (Map.Entry<String, Integer> entry : sexes.entrySet()) {
                            sexData.add(new PieChart.Data(
                                    entry.getKey() + " (" + entry.getValue() + ")",
                                    entry.getValue()));
                        }

                        loading.set(false);
                        statusMessage.set("Data loaded successfully");
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        loading.set(false);
                        statusMessage.set("Error loading data: " + e.getMessage());
                    });
                }
                return null;
            }
        };

        Thread thread = new Thread(fetchTask);
        thread.setDaemon(true);
        thread.start();
    }
*/
  public void loadData() {
      loading.set(true);
      statusMessage.set("Fetching data from FBI API...");

      Task<Void> fetchTask = new Task<>() {
          @Override
          protected Void call() {
              try {
                  System.out.println("Analytics loading started...");

                  List<WantedPerson> people = FbiApiService.getWantedPeople();
                  System.out.println("Analytics people fetched: " + people.size());

                  Map<String, Integer> offices = new java.util.HashMap<>();
                  Map<String, Integer> rewards = new java.util.HashMap<>();
                  Map<String, Integer> statuses = new java.util.HashMap<>();

                  for (WantedPerson person : people) {
                      if (person.getFieldOffices() != null) {
                          for (String office : person.getFieldOffices()) {
                              offices.merge(office, 1, Integer::sum);
                          }
                      }

                      String rewardType = person.getRewardAmount() > 0 ? "Reward Listed" : "No Reward";
                      rewards.merge(rewardType, 1, Integer::sum);

                      statuses.merge(person.getDisplayStatus(), 1, Integer::sum);
                  }

                  Platform.runLater(() -> {
                      totalRecordsText.set(String.valueOf(people.size()));
                      recordsAnalyzedText.set(String.valueOf(people.size()));

                      subjectsData.clear();
                      rewards.forEach((key, value) ->
                              subjectsData.add(new XYChart.Data<>(key, value))
                      );

                      fieldOfficeData.clear();
                      offices.entrySet().stream()
                              .limit(8)
                              .forEach(entry ->
                                      fieldOfficeData.add(new PieChart.Data(entry.getKey(), entry.getValue()))
                              );

                      raceData.clear();
                      statuses.forEach((key, value) ->
                              raceData.add(new XYChart.Data<>(key, value))
                      );

                      sexData.clear();
                      rewards.forEach((key, value) ->
                              sexData.add(new PieChart.Data(key, value))
                      );

                      loading.set(false);
                      statusMessage.set("Data loaded successfully");
                  });
              } catch (Exception e) {
                  e.printStackTrace();

                  Platform.runLater(() -> {
                      loading.set(false);
                      statusMessage.set("Error loading analytics data.");
                  });
              }

              return null;
          }
      };

      Thread thread = new Thread(fetchTask);
      thread.setDaemon(true);
      thread.start();
  }
    // ---------- Helpers ----------

    /**
     * Shortens long labels for better chart readability.
     */
    private String shortenLabel(String label) {
        if (label.length() <= 22) return label;

        Map<String, String> shortcuts = Map.of(
                "ViCAP Missing Persons", "ViCAP Missing",
                "ViCAP Homicides and Sexual Assaults", "ViCAP Homicides",
                "ViCAP Unidentified Persons", "ViCAP Unidentified",
                "Criminal Enterprise Investigations", "Criminal Enterprise",
                "Kidnappings and Missing Persons", "Kidnappings",
                "Seeking Information - Terrorism", "Info - Terrorism",
                "Additional Violent Crimes", "Violent Crimes",
                "Endangered Child Alert Program", "ECAP"
        );

        return shortcuts.getOrDefault(label, label.substring(0, 20) + "...");
    }
}
