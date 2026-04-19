package org.example.fugitivefinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WantedPerson {

    private String uid;
    private String title;
    private String description;
    private String rewardText;
    private String warning_message;
    private String status;
    private List<String> aliases;
    private List<String> fieldOffices;
    private List<String> images;

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRewardText() { return rewardText; }
    public void setRewardText(String rewardText) { this.rewardText = rewardText; }

    public String getWarning_message() { return warning_message; }
    public void setWarning_message(String warning_message) { this.warning_message = warning_message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getAliases() { return aliases; }
    public void setAliases(List<String> aliases) { this.aliases = aliases; }

    public List<String> getFieldOffices() { return fieldOffices; }
    public void setFieldOffices(List<String> fieldOffices) { this.fieldOffices = fieldOffices; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    // ✅ FIXED IMAGE LOGIC
    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public double getRewardAmount() {
        if (reward_max != null && reward_max > 0) {
            return reward_max;
        }
        if (reward_text != null && !reward_text.isBlank()) {
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("\\$([\\d,]+)")
                    .matcher(reward_text);
            if (matcher.find()) {
                try {
                    return Long.parseLong(matcher.group(1).replace(",", ""));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    public String getDisplayReward() {
        if (reward_max != null && reward_max > 0) {
            return String.format("$%,.0f", reward_max);
        }
        if (reward_text != null && !reward_text.isBlank()) {
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("\\$([\\d,]+)")
                    .matcher(reward_text);
            if (matcher.find()) {
                String numStr = matcher.group(1).replace(",", "");
                try {
                    long amount = Long.parseLong(numStr);
                    return String.format("$%,d", amount);
                } catch (NumberFormatException e) {
                    return reward_text;
                }
            }
        }
        return "No Reward Listed";
    }

    public String getDisplayStatus() {
        return status == null || status.isBlank() ? "Wanted" : status;
    }

    public String getDisplayAliases() {
        return aliases == null || aliases.isEmpty()
                ? "None listed"
                : String.join(", ", aliases);
    }

    public String getDisplayFieldOffices() {
        return fieldOffices == null || fieldOffices.isEmpty()
                ? "Not available"
                : String.join(", ", fieldOffices);
    }
}