package org.example.fugitivefinder.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.time.Duration;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for fetching FBI Most Wanted API data
 * and aggregating it into chart-ready distributions.
 *
 * This service fetches multiple pages of data and computes frequency
 * distributions for subjects (crime categories), field offices,
 * race/ethnicity, sex, and poster classification.
 *
 * @author Sahil Kamal
 */
public class ChartDataService {

    private static final String BASE_URL = "https://fbi-backend-wilt.onrender.com/api/wanted/";
    private static final int PAGE_SIZE = 50;
    private static final int DEFAULT_PAGES = 4; // 200 records by default

    private final HttpClient client;

    public ChartDataService() {
        this.client = HttpClient.newHttpClient();
    }

    // ---------- Public API Methods ----------

    /**
     * Fetches all raw JSON items from the FBI API across multiple pages.
     *
     * @param numPages how many pages to fetch (each page = 50 records)
     * @return list of JSONObjects, one per wanted person
     */
    public List<JSONObject> fetchAllItems(int numPages) {
        List<JSONObject> allItems = new ArrayList<>();

        for (int page = 1; page <= 1; page++) {
            try {
                String url = BASE_URL + "?pageSize=100";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JSONObject root = new JSONObject(response.body());
                    JSONArray items = root.optJSONArray("items");

                    if (items != null) {
                        for (int i = 0; i < items.length(); i++) {
                            allItems.add(items.getJSONObject(i));
                        }
                    }
                } else {
                    System.err.println("FBI API returned status " + response.statusCode()
                            + " for page " + page);
                }
            } catch (Exception e) {
                System.err.println("Error fetching page " + page + ": " + e.getMessage());
            }
        }

        return allItems;
    }

    /**
     * Gets a default batch of items (200 records).
     *
     * @return list of JSONObjects from the API
     */
    public List<JSONObject> fetchAllItems() {
        return fetchAllItems(DEFAULT_PAGES);
    }

    // ---------- Distribution Methods ----------

    /**
     * Computes crime category distribution from the 'subjects' field.
     * Each wanted person can have multiple subjects, so each is counted individually.
     *
     * @param items list of JSON items from the API
     * @return map of subject name to count, sorted by count descending
     */
    public Map<String, Integer> getSubjectsDistribution(List<JSONObject> items) {
        Map<String, Integer> distribution = new HashMap<>();

        for (JSONObject item : items) {
            JSONArray subjects = item.optJSONArray("subjects");
            if (subjects != null) {
                for (int i = 0; i < subjects.length(); i++) {
                    String subject = subjects.getString(i);
                    distribution.merge(subject, 1, Integer::sum);
                }
            }
        }

        return sortByValueDescending(distribution);
    }

    /**
     * Computes field office distribution from the 'field_offices' field.
     * Returns top N offices with the rest grouped into "Other".
     *
     * @param items list of JSON items from the API
     * @param topN  how many top offices to show individually
     * @return map of office name to count, sorted by count descending
     */
    public Map<String, Integer> getFieldOfficeDistribution(List<JSONObject> items, int topN) {
        Map<String, Integer> rawDistribution = new HashMap<>();

        for (JSONObject item : items) {
            JSONArray offices = item.optJSONArray("field_offices");
            if (offices != null) {
                for (int i = 0; i < offices.length(); i++) {
                    String office = capitalizeOfficeName(offices.getString(i));
                    rawDistribution.merge(office, 1, Integer::sum);
                }
            }
        }

        return groupTopN(rawDistribution, topN);
    }

    /**
     * Computes race/ethnicity distribution from the 'race' field.
     *
     * @param items list of JSON items from the API
     * @return map of race to count, sorted by count descending
     */
    public Map<String, Integer> getRaceDistribution(List<JSONObject> items) {
        Map<String, Integer> distribution = new HashMap<>();

        for (JSONObject item : items) {
            String race = item.optString("race", "unknown");
            if (race.isEmpty() || race.equals("null")) {
                race = "unknown";
            }
            // Capitalize for display
            String displayRace = capitalize(race);
            distribution.merge(displayRace, 1, Integer::sum);
        }

        return sortByValueDescending(distribution);
    }

    /**
     * Computes sex/gender distribution from the 'sex' field.
     *
     * @param items list of JSON items from the API
     * @return map of sex to count, sorted by count descending
     */
    public Map<String, Integer> getSexDistribution(List<JSONObject> items) {
        Map<String, Integer> distribution = new HashMap<>();

        for (JSONObject item : items) {
            String sex = item.optString("sex", "Unknown");
            if (sex.isEmpty() || sex.equals("null")) {
                sex = "Unknown";
            }
            distribution.merge(sex, 1, Integer::sum);
        }

        return sortByValueDescending(distribution);
    }

    /**
     * Computes poster classification distribution.
     *
     * @param items list of JSON items from the API
     * @return map of classification to count, sorted by count descending
     */
    public Map<String, Integer> getPosterClassificationDistribution(List<JSONObject> items) {
        Map<String, Integer> distribution = new HashMap<>();

        for (JSONObject item : items) {
            String classification = item.optString("poster_classification", "unknown");
            if (classification.isEmpty() || classification.equals("null")) {
                classification = "unknown";
            }
            String displayName = formatPosterClassification(classification);
            distribution.merge(displayName, 1, Integer::sum);
        }

        return sortByValueDescending(distribution);
    }

    /**
     * Gets the total number of records available in the FBI database.
     *
     * @return total count, or -1 if the API call fails
     */
    public int getTotalRecordCount() {
        try {
            String url = BASE_URL + "?page=1&pageSize=1";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject root = new JSONObject(response.body());
                return root.optInt("total", -1);
            }
        } catch (Exception e) {
            System.err.println("Error fetching total count: " + e.getMessage());
        }
        return -1;
    }

    // ---------- Helper Methods ----------

    /**
     * Sorts a map by value in descending order and returns a LinkedHashMap
     * to preserve the sorted order.
     */
    private Map<String, Integer> sortByValueDescending(Map<String, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Groups a distribution into top N entries, with the rest combined as "Other".
     */
    private Map<String, Integer> groupTopN(Map<String, Integer> raw, int topN) {
        Map<String, Integer> sorted = sortByValueDescending(raw);
        Map<String, Integer> result = new LinkedHashMap<>();

        int count = 0;
        int otherCount = 0;

        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            if (count < topN) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                otherCount += entry.getValue();
            }
            count++;
        }

        if (otherCount > 0) {
            result.put("Other", otherCount);
        }

        return result;
    }

    /**
     * Capitalizes a field office name by splitting on camelCase boundaries
     * and adding spaces. E.g., "newyork" -> "New York", "washingtondc" -> "Washington DC"
     */
    private String capitalizeOfficeName(String office) {
        // Map of known office names for clean display
        Map<String, String> officeNames = Map.ofEntries(
                Map.entry("newyork", "New York"),
                Map.entry("washingtondc", "Washington DC"),
                Map.entry("losangeles", "Los Angeles"),
                Map.entry("sanfrancisco", "San Francisco"),
                Map.entry("sandiego", "San Diego"),
                Map.entry("sanjuan", "San Juan"),
                Map.entry("saltlakecity", "Salt Lake City"),
                Map.entry("kansascity", "Kansas City"),
                Map.entry("oklahomacity", "Oklahoma City"),
                Map.entry("stlouis", "St. Louis")
        );

        String lower = office.toLowerCase().trim();
        if (officeNames.containsKey(lower)) {
            return officeNames.get(lower);
        }

        // Default: just capitalize the first letter
        return capitalize(lower);
    }

    /**
     * Capitalizes the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Formats a poster_classification value for display.
     */
    private String formatPosterClassification(String classification) {
        return switch (classification) {
            case "default" -> "Wanted (Default)";
            case "missing" -> "Missing Persons";
            case "information" -> "Seeking Information";
            case "ten" -> "Ten Most Wanted";
            case "law-enforcement-assistance" -> "Law Enforcement";
            default -> capitalize(classification);
        };
    }
}
