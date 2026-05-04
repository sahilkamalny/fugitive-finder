package org.example.fugitivefinder.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for building the "Most Dangerous" leaderboard.
 * Fetches FBI data and ranks fugitives by a computed danger score
 * based on reward amount and warning indicators.
 *
 * @author Sahil Kamal
 */
public class LeaderboardService {

    private final ChartDataService chartDataService;

    public LeaderboardService() {
        this.chartDataService = new ChartDataService();
    }

    /**
     * A record representing a ranked fugitive for the leaderboard.
     */
    public static class RankedFugitive {
        private final int rank;
        private final String name;
        private final String rewardText;
        private final int rewardAmount;
        private final String dangerLevel;
        private final String fieldOffice;
        private final String imageUrl;
        private final String warningMessage;

        public RankedFugitive(int rank, String name, String rewardText, int rewardAmount,
                              String dangerLevel, String fieldOffice, String imageUrl,
                              String warningMessage) {
            this.rank = rank;
            this.name = name;
            this.rewardText = rewardText;
            this.rewardAmount = rewardAmount;
            this.dangerLevel = dangerLevel;
            this.fieldOffice = fieldOffice;
            this.imageUrl = imageUrl;
            this.warningMessage = warningMessage;
        }

        public int getRank() { return rank; }
        public String getName() { return name; }
        public String getRewardText() { return rewardText; }
        public int getRewardAmount() { return rewardAmount; }
        public String getDangerLevel() { return dangerLevel; }
        public String getFieldOffice() { return fieldOffice; }
        public String getImageUrl() { return imageUrl; }
        public String getWarningMessage() { return warningMessage; }
    }

    /**
     * Fetches fugitives from the FBI API and returns them ranked by danger score.
     *
     * @param maxResults how many top results to return
     * @return list of RankedFugitive sorted by danger score descending
     */
    public List<RankedFugitive> getMostDangerous(int maxResults) {
        List<JSONObject> items = chartDataService.fetchAllItems();
        return rankFugitives(items, maxResults);
    }

    /**
     * Ranks a list of FBI records by computed danger score.
     */
    public List<RankedFugitive> rankFugitives(List<JSONObject> items, int maxResults) {
        List<ScoredItem> scored = new ArrayList<>();

        for (JSONObject item : items) {
            int score = computeDangerScore(item);
            if (score > 0) {
                scored.add(new ScoredItem(item, score));
            }
        }

        // Sort by score descending
        scored.sort((a, b) -> Integer.compare(b.score, a.score));

        List<RankedFugitive> results = new ArrayList<>();
        int limit = Math.min(maxResults, scored.size());

        for (int i = 0; i < limit; i++) {
            JSONObject item = scored.get(i).item;
            int score = scored.get(i).score;

            String name = item.optString("title", "Unknown");
            String rewardText = item.optString("reward_text", "No reward");
            int rewardMax = item.optInt("reward_max", 0);
            String dangerLevel = classifyDangerLevel(score);
            String fieldOffice = getFirstFieldOffice(item);
            String imageUrl = getFirstImageUrl(item);
            String warning = item.optString("warning_message", "");

            results.add(new RankedFugitive(
                    i + 1, name, rewardText, rewardMax,
                    dangerLevel, fieldOffice, imageUrl, warning));
        }

        return results;
    }

    /**
     * Computes a danger score (0-100) for a fugitive based on:
     * - Reward amount (higher = more dangerous)
     * - Warning message presence ("ARMED AND DANGEROUS" etc.)
     * - Poster classification (Ten Most Wanted = highest)
     * - Subject categories (terrorism, violent crimes score higher)
     */
    private int computeDangerScore(JSONObject item) {
        int score = 0;

        // Reward component (0-40 points)
        int rewardMax = item.optInt("reward_max", 0);
        if (rewardMax >= 1000000) score += 40;
        else if (rewardMax >= 250000) score += 30;
        else if (rewardMax >= 100000) score += 25;
        else if (rewardMax >= 50000) score += 20;
        else if (rewardMax >= 10000) score += 10;
        else if (rewardMax > 0) score += 5;

        // Warning message component (0-25 points)
        String warning = item.optString("warning_message", "").toUpperCase();
        if (warning.contains("ARMED") && warning.contains("DANGEROUS")) score += 25;
        else if (warning.contains("ARMED")) score += 20;
        else if (warning.contains("DANGEROUS")) score += 20;
        else if (warning.contains("EXTREME")) score += 20;
        else if (warning.contains("CAUTION")) score += 10;
        else if (!warning.isEmpty()) score += 5;

        // Poster classification component (0-20 points)
        String classification = item.optString("poster_classification", "");
        if (classification.equals("ten")) score += 20;
        else if (classification.equals("default")) score += 5;

        // Subject categories component (0-15 points)
        JSONArray subjects = item.optJSONArray("subjects");
        if (subjects != null) {
            String subjectsStr = subjects.toString().toUpperCase();
            if (subjectsStr.contains("TERRORISM")) score += 15;
            else if (subjectsStr.contains("VIOLENT")) score += 12;
            else if (subjectsStr.contains("MURDER")) score += 12;
            else if (subjectsStr.contains("CRIMINAL ENTERPRISE")) score += 10;
            else if (subjectsStr.contains("CYBER")) score += 8;
            else if (subjectsStr.contains("COUNTERINTELLIGENCE")) score += 8;
        }

        return score;
    }

    /**
     * Classifies a numeric score into a danger level label.
     */
    private String classifyDangerLevel(int score) {
        if (score >= 70) return "CRITICAL";
        if (score >= 50) return "HIGH";
        if (score >= 30) return "MODERATE";
        if (score >= 15) return "LOW";
        return "MINIMAL";
    }

    /**
     * Gets the first field office name from a record, or "N/A".
     */
    private String getFirstFieldOffice(JSONObject item) {
        JSONArray offices = item.optJSONArray("field_offices");
        if (offices != null && offices.length() > 0) {
            return offices.getString(0);
        }
        return "N/A";
    }

    /**
     * Gets the first image URL from a record.
     */
    private String getFirstImageUrl(JSONObject item) {
        JSONArray images = item.optJSONArray("images");
        if (images != null && images.length() > 0) {
            Object first = images.opt(0);
            if (first instanceof JSONObject) {
                return ((JSONObject) first).optString("original", "");
            } else if (first instanceof String) {
                return (String) first;
            }
        }
        return "";
    }

    /**
     * Internal helper to pair an item with its score for sorting.
     */
    private static class ScoredItem {
        final JSONObject item;
        final int score;
        ScoredItem(JSONObject item, int score) {
            this.item = item;
            this.score = score;
        }
    }
}
