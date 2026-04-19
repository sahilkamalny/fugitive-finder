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

    public String getDisplayReward() {
        return rewardText == null || rewardText.isBlank()
                ? "No Reward Listed"
                : rewardText;
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