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

                  // Crime category distribution from subjects
                  Map<String, Integer> subjectDist = new java.util.HashMap<>();
                  // Field office distribution
                  Map<String, Integer> officeDist = new java.util.HashMap<>();
                  // Race/ethnicity distribution
                  Map<String, Integer> raceDist = new java.util.HashMap<>();
                  // Sex/gender distribution
                  Map<String, Integer> sexDist = new java.util.HashMap<>();

                  for (WantedPerson person : people) {
                      // Subjects (crime categories)
                      if (person.getSubjects() != null) {
                          for (String subject : person.getSubjects()) {
                              if (subject != null && !subject.isBlank()) {
                                  subjectDist.merge(subject, 1, Integer::sum);
                              }
                          }
                      }

                      // Field offices
                      if (person.getFieldOffices() != null) {
                          for (String office : person.getFieldOffices()) {
                              if (office != null && !office.isBlank()) {
                                  String display = office.substring(0, 1).toUpperCase()
                                          + office.substring(1).toLowerCase();
                                  officeDist.merge(display, 1, Integer::sum);
                              }
                          }
                      }

                      // Race
                      String race = person.getDisplayRace();
                      raceDist.merge(race, 1, Integer::sum);

                      // Sex
                      String sex = person.getDisplaySex();
                      sexDist.merge(sex, 1, Integer::sum);
                  }

                  // Sort all distributions by value descending
                  var sortedSubjects = sortDesc(subjectDist);
                  var sortedOffices = sortDesc(officeDist);
                  var sortedRaces = sortDesc(raceDist);
                  var sortedSexes = sortDesc(sexDist);

                  Platform.runLater(() -> {
                      totalRecordsText.set(String.valueOf(people.size()));
                      recordsAnalyzedText.set(String.valueOf(people.size()));

                      // Crime categories bar chart (top 10)
                      subjectsData.clear();
                      int count = 0;
                      for (var entry : sortedSubjects.entrySet()) {
                          if (count >= 10) break;
                          subjectsData.add(new XYChart.Data<>(
                                  shortenLabel(entry.getKey()), entry.getValue()));
                          count++;
                      }

                      // Field office pie chart (top 8 + Other)
                      fieldOfficeData.clear();
                      int officeCount = 0;
                      int otherCount = 0;
                      for (var entry : sortedOffices.entrySet()) {
                          if (officeCount < 8) {
                              fieldOfficeData.add(new PieChart.Data(
                                      entry.getKey() + " (" + entry.getValue() + ")",
                                      entry.getValue()));
                          } else {
                              otherCount += entry.getValue();
                          }
                          officeCount++;
                      }
                      if (otherCount > 0) {
                          fieldOfficeData.add(new PieChart.Data(
                                  "Other (" + otherCount + ")", otherCount));
                      }

                      // Race bar chart
                      raceData.clear();
                      for (var entry : sortedRaces.entrySet()) {
                          raceData.add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                      }

                      // Sex pie chart
                      sexData.clear();
                      for (var entry : sortedSexes.entrySet()) {
                          sexData.add(new PieChart.Data(
                                  entry.getKey() + " (" + entry.getValue() + ")",
                                  entry.getValue()));
                      }

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

    /** Sorts a map by value descending, preserving insertion order. */
    private Map<String, Integer> sortDesc(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, java.util.LinkedHashMap::new));
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
