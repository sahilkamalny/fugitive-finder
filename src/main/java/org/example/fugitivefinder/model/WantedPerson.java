package org.example.fugitivefinder.model;

import java.util.List;

public class WantedPerson {

    private String uid;
    private String title;
    private String description;
    private String reward_text;
    private String warning_message;
    private String status;

    private String sex;
    private String race;
    private String place_of_birth;
    private String nationality;
    private String occupation;
    private Integer age_now;
    private List<String> subjects;
    private List<String> aliases;
    private List<String> field_offices;
    private List<String> locations;
    private List<ImageItem> images;

    private Double latitude;
    private Double longitude;

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
        return title == null || title.isBlank() ? "Unknown" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description == null || description.isBlank() ? "No description available." : description;
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
        return place_of_birth == null ? "" : place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
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
        return age_now;
    }

    public void setAge_now(Integer age_now) {
        this.age_now = age_now;
    }

    public List<String> getSubjectsList() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public String getSubjects() {
        return subjects == null || subjects.isEmpty() ? "" : String.join(", ", subjects);
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

    public String getFieldOfficesText() {
        return field_offices == null || field_offices.isEmpty() ? "" : String.join(", ", field_offices);
    }

    public List<String> getLocationsList() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getLocations() {
        return locations == null || locations.isEmpty() ? "" : String.join(", ", locations);
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