package org.example.fugitivefinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WantedPerson {

    private String uid;
    private String title;
    private String description;
    private String status;
    private String sex;
    private String race;
    private String nationality;
    private String hair;
    private String eyes;
    private String publication;

    @JsonProperty("rewardText")
    private String reward_text;

    @JsonProperty("rewardMin")
    private Double reward_min;

    @JsonProperty("rewardMax")
    private Double reward_max;

    @JsonProperty("fieldOffices")
    private List<String> field_offices;

    private List<String> aliases;
    private List<String> subjects;
    private List<String> images;

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getHair() { return hair; }
    public void setHair(String hair) { this.hair = hair; }

    public String getEyes() { return eyes; }
    public void setEyes(String eyes) { this.eyes = eyes; }

    public String getPublication() { return publication; }
    public void setPublication(String publication) { this.publication = publication; }

    public String getReward_text() { return reward_text; }
    public void setReward_text(String reward_text) { this.reward_text = reward_text; }

    public Double getReward_min() { return reward_min; }
    public void setReward_min(Double reward_min) { this.reward_min = reward_min; }

    public Double getReward_max() { return reward_max; }
    public void setReward_max(Double reward_max) { this.reward_max = reward_max; }

    public List<String> getField_offices() { return field_offices; }
    public void setField_offices(List<String> field_offices) { this.field_offices = field_offices; }

    public List<String> getAliases() { return aliases; }
    public void setAliases(List<String> aliases) { this.aliases = aliases; }

    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

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
        return aliases == null || aliases.isEmpty() ? "None listed" : String.join(", ", aliases);
    }

    public String getDisplayFieldOffices() {
        return field_offices == null || field_offices.isEmpty()
                ? "Not available"
                : String.join(", ", field_offices);
    }

    public String getWarning_message() { return null; }
    public void setWarning_message(String warning_message) { }
}