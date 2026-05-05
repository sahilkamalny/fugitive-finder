package org.example.fugitivefinder.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that computes region-based crime statistics by grouping
 * FBI field offices into geographic regions (Northeast, South, Midwest, West).
 *
 * @author Sahil Kamal
 */
public class RegionStatsService {

    private final ChartDataService chartDataService;

    /**
     * Represents aggregated statistics for a single region.
     */
    public static class RegionStat {
        private final String regionName;
        private final int totalCases;
        private final String topCrimeCategory;
        private final int officeCount;
        private final double avgReward;
        private final List<String> offices;

        public RegionStat(String regionName, int totalCases, String topCrimeCategory,
                          int officeCount, double avgReward, List<String> offices) {
            this.regionName = regionName;
            this.totalCases = totalCases;
            this.topCrimeCategory = topCrimeCategory;
            this.officeCount = officeCount;
            this.avgReward = avgReward;
            this.offices = offices;
        }

        public String getRegionName() { return regionName; }
        public int getTotalCases() { return totalCases; }
        public String getTopCrimeCategory() { return topCrimeCategory; }
        public int getOfficeCount() { return officeCount; }
        public double getAvgReward() { return avgReward; }
        public List<String> getOffices() { return offices; }
    }

    // FBI field offices grouped by US region
    private static final Map<String, List<String>> REGION_OFFICES = Map.of(
            "Northeast", List.of("newyork", "newark", "boston", "philadelphia",
                    "pittsburgh", "albany", "buffalo", "newhaven"),
            "South", List.of("miami", "atlanta", "tampa", "charlotte",
                    "richmond", "norfolk", "memphis", "neworleans",
                    "houston", "dallas", "sanantonio", "elpaso",
                    "sanjuan", "jackson", "jacksonville", "birmingham",
                    "knoxville", "louisville", "littlerock", "mobile"),
            "Midwest", List.of("chicago", "detroit", "indianapolis", "kansascity",
                    "milwaukee", "minneapolis", "omaha", "stlouis",
                    "cleveland", "cincinnati", "springfield", "desmoines"),
            "West", List.of("losangeles", "sanfrancisco", "sandiego", "seattle",
                    "portland", "sacramento", "lasvegas", "phoenix",
                    "denver", "saltlakecity", "honolulu", "anchorage",
                    "albuquerque", "billings")
    );

    public RegionStatsService() {
        this.chartDataService = new ChartDataService();
    }

    /**
     * Fetches data from the API and computes stats for all 4 regions.
     *
     * @return map of region name to RegionStat
     */
    public Map<String, RegionStat> getRegionStats() {
        List<JSONObject> items = chartDataService.fetchAllItems();
        return computeRegionStats(items);
    }

    /**
     * Computes region statistics from a list of FBI records.
     */
    public Map<String, RegionStat> computeRegionStats(List<JSONObject> items) {
        // Group items by region
        Map<String, List<JSONObject>> regionItems = new LinkedHashMap<>();
        for (String region : List.of("Northeast", "South", "Midwest", "West")) {
            regionItems.put(region, new ArrayList<>());
        }

        for (JSONObject item : items) {
            JSONArray offices = item.optJSONArray("fieldOffices");
            if (offices == null) continue;

            for (int i = 0; i < offices.length(); i++) {
                String office = offices.getString(i).toLowerCase().trim();
                String region = getRegionForOffice(office);
                if (region != null) {
                    regionItems.get(region).add(item);
                    break; // count each item once per region
                }
            }
        }

        // Compute stats for each region
        Map<String, RegionStat> results = new LinkedHashMap<>();
        for (Map.Entry<String, List<JSONObject>> entry : regionItems.entrySet()) {
            String regionName = entry.getKey();
            List<JSONObject> regionData = entry.getValue();

            int totalCases = regionData.size();
            String topCrime = getTopCrimeForItems(regionData);
            int officeCount = countUniqueOfficesInRegion(regionData, regionName);
            double avgReward = computeAverageReward(regionData);
            List<String> offices = getActiveOfficesInRegion(regionData, regionName);

            results.put(regionName, new RegionStat(
                    regionName, totalCases, topCrime, officeCount, avgReward, offices));
        }

        return results;
    }

    /**
     * Returns which region a field office belongs to.
     */
    private String getRegionForOffice(String office) {
        for (Map.Entry<String, List<String>> entry : REGION_OFFICES.entrySet()) {
            if (entry.getValue().contains(office)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Gets the most common crime category for a set of items.
     */
    private String getTopCrimeForItems(List<JSONObject> items) {
        Map<String, Integer> counts = new HashMap<>();
        for (JSONObject item : items) {
            JSONArray subjects = item.optJSONArray("subjects");
            if (subjects != null) {
                for (int i = 0; i < subjects.length(); i++) {
                    counts.merge(subjects.getString(i), 1, Integer::sum);
                }
            }
        }
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    /**
     * Counts unique active offices in a region from the data.
     */
    private int countUniqueOfficesInRegion(List<JSONObject> items, String regionName) {
        Set<String> unique = new HashSet<>();
        List<String> regionOfficeList = REGION_OFFICES.getOrDefault(regionName, List.of());

        for (JSONObject item : items) {
            JSONArray offices = item.optJSONArray("fieldOffices");
            if (offices != null) {
                for (int i = 0; i < offices.length(); i++) {
                    String office = offices.getString(i).toLowerCase().trim();
                    if (regionOfficeList.contains(office)) {
                        unique.add(office);
                    }
                }
            }
        }
        return unique.size();
    }

    /**
     * Returns list of offices that have active cases in a region.
     */
    private List<String> getActiveOfficesInRegion(List<JSONObject> items, String regionName) {
        Set<String> unique = new LinkedHashSet<>();
        List<String> regionOfficeList = REGION_OFFICES.getOrDefault(regionName, List.of());

        for (JSONObject item : items) {
            JSONArray offices = item.optJSONArray("fieldOffices");
            if (offices != null) {
                for (int i = 0; i < offices.length(); i++) {
                    String office = offices.getString(i).toLowerCase().trim();
                    if (regionOfficeList.contains(office)) {
                        unique.add(office);
                    }
                }
            }
        }
        return new ArrayList<>(unique);
    }

    /**
     * Computes average reward across items that have a reward.
     */
    private double computeAverageReward(List<JSONObject> items) {
        long totalReward = 0;
        int rewardCount = 0;

        for (JSONObject item : items) {
            String rewardText = item.optString("rewardText", "");
            int reward = extractRewardAmount(rewardText);
            if (reward > 0) {
                totalReward += reward;
                rewardCount++;
            }
        }

        return rewardCount > 0 ? (double) totalReward / rewardCount : 0;
    }

    /**
     * Extracts a numeric reward amount from the reward text.
     */
    private int extractRewardAmount(String rewardText) {
        if (rewardText == null || rewardText.isEmpty() || rewardText.equals("null")) {
            return 0;
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\$([0-9,]+)").matcher(rewardText);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1).replace(",", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
