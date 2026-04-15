package org.example.fugitivefinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WantedPerson {

    private String uid;
    private String title;
    private String description;

    @JsonProperty("reward_text")
    private String rewardText;

    @JsonProperty("warning_message")
    private String warningMessage;

    private String status;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("race")
    private String race;

    @JsonProperty("place_of_birth")
    private String placeOfBirth;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("occupation")
    private String occupation;

    @JsonProperty("age_now")
    private Integer ageNow;

    @JsonProperty("subjects")
    private List<String> subjects;

    @JsonProperty("aliases")
    private List<String> aliases;

    @JsonProperty("field_offices")
    private List<String> fieldOffices;

    @JsonProperty("locations")
    private List<String> locations;

    @JsonProperty("images")
    private List<ImageItem> images;

    // Optional (for map if you add later)
    private Double latitude;
    private Double longitude;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageItem {

        @JsonProperty("original")
        private String original;

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }

    // ===================== BASIC GETTERS =====================

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title == null || title.isBlank() ? "Unknown" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description == null || description.isBlank()
                ? "No description available."
                : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReward_text() {
        return rewardText;
    }

    public void setReward_text(String rewardText) {
        this.rewardText = rewardText;
    }

    public String getWarning_message() {
        return warningMessage;
    }

    public void setWarning_message(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public String getStatus() {
        return status == null || status.isBlank() ? "Wanted" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSex() {
        return sex == null ? "" : sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRace() {
        return race == null ? "" : race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getPlace_of_birth() {
        return placeOfBirth == null ? "" : placeOfBirth;
    }

    public void setPlace_of_birth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getNationality() {
        return nationality == null ? "" : nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getOccupation() {
        return occupation == null ? "" : occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Integer getAgeNow() {
        return ageNow;
    }

    public void setAgeNow(Integer ageNow) {
        this.ageNow = ageNow;
    }

    // ===================== LIST HANDLING =====================

    public List<String> getSubjectsList() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public String getSubjects() {
        return subjects == null || subjects.isEmpty()
                ? ""
                : String.join(", ", subjects);
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public List<String> getField_offices() {
        return fieldOffices;
    }

    public void setField_offices(List<String> fieldOffices) {
        this.fieldOffices = fieldOffices;
    }

    public String getFieldOfficesText() {
        return fieldOffices == null || fieldOffices.isEmpty()
                ? ""
                : String.join(", ", fieldOffices);
    }

    public List<String> getLocationsList() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getLocations() {
        return locations == null || locations.isEmpty()
                ? ""
                : String.join(", ", locations);
    }


    public List<ImageItem> getImages() {
        return images;
    }

    public void setImages(List<ImageItem> images) {
        this.images = images;
    }

    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty() && images.get(0) != null) {
            return images.get(0).getOriginal();
        }
        return null;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    public String getDisplaySubjects() {
        return subjects == null || subjects.isEmpty()
                ? "Not available"
                : String.join(", ", subjects);
    }

    public String getDisplayLocations() {
        return locations == null || locations.isEmpty()
                ? "Not available"
                : String.join(", ", locations);
    }
}