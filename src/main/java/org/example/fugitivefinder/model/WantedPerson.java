package org.example.fugitivefinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WantedPerson {

    private String uid;
    private String title;
    private String description;
    private String reward_text;
    private String warning_message;
    private String status;
    private List<String> aliases;
    private List<String> field_offices;
    private List<String> images;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageItem {
        private String original;

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReward_text() {
        return reward_text;
    }

    public void setReward_text(String reward_text) {
        this.reward_text = reward_text;
    }

    public String getWarning_message() {
        return warning_message;
    }

    public void setWarning_message(String warning_message) {
        this.warning_message = warning_message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public List<String> getField_offices() {
        return field_offices;
    }

    public void setField_offices(List<String> field_offices) {
        this.field_offices = field_offices;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public String getDisplayReward() {
        return reward_text == null || reward_text.isBlank() ? "No Reward Listed" : reward_text;
    }

    public String getDisplayStatus() {
        return status == null || status.isBlank() ? "Wanted" : status;
    }

    public String getDisplayAliases() {
        return aliases == null || aliases.isEmpty() ? "None listed" : String.join(", ", aliases);
    }

    public String getDisplayFieldOffices() {
        return field_offices == null || field_offices.isEmpty()
                ? "Not available"
                : String.join(", ", field_offices);
    }
}