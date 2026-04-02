package org.example.fugitivefinder.model;

import java.util.List;

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
    private String rewardText;
    private Double rewardMin;
    private Double rewardMax;
    private List<String> subjects;
    private List<String> fieldOffices;
    private List<String> images;
    private String publication;

    // Constructor
    public WantedPerson(String uid, String title, String description, String status,
                        String sex, String race, String nationality, String hair, String eyes,
                        String rewardText, Double rewardMin, Double rewardMax,
                        List<String> subjects, List<String> fieldOffices,
                        List<String> images, String publication) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.status = status;
        this.sex = sex;
        this.race = race;
        this.nationality = nationality;
        this.hair = hair;
        this.eyes = eyes;
        this.rewardText = rewardText;
        this.rewardMin = rewardMin;
        this.rewardMax = rewardMax;
        this.subjects = subjects;
        this.fieldOffices = fieldOffices;
        this.images = images;
        this.publication = publication;
    }

    // Getters
    public String getUid() { return uid; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getSex() { return sex; }
    public String getRace() { return race; }
    public String getNationality() { return nationality; }
    public String getHair() { return hair; }
    public String getEyes() { return eyes; }
    public String getRewardText() { return rewardText; }
    public Double getRewardMin() { return rewardMin; }
    public Double getRewardMax() { return rewardMax; }
    public List<String> getSubjects() { return subjects; }
    public List<String> getFieldOffices() { return fieldOffices; }
    public List<String> getImages() { return images; }
    public String getPublication() { return publication; }

    @Override
    public String toString() {
        return "WantedPerson{uid='" + uid + "', title='" + title + "', status='" + status + "'}";
    }
}