package org.example.fugitivefinder.model;

import java.util.ArrayList;
import java.util.List;

public class AppUser {

    private String uid;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicUrl;
    private List<String> savedTargetIds = new ArrayList<>();

    public AppUser() {
    }

    public AppUser(String uid, String username, String firstName, String lastName,
                   String email, String profilePicUrl, List<String> savedTargetIds) {
        this.uid = uid;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
        this.savedTargetIds = savedTargetIds;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public List<String> getSavedTargetIds() {
        return savedTargetIds;
    }

    public void setSavedTargetIds(List<String> savedTargetIds) {
        this.savedTargetIds = savedTargetIds == null ? new ArrayList<>() : savedTargetIds;
    }

    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }
}