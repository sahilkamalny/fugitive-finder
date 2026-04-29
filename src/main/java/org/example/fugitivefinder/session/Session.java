package org.example.fugitivefinder.session;

import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.model.WantedPerson;

public class Session {

    private static final Session instance = new Session();

    private AppUser currentUser;
    private WantedPerson selectedWantedPerson;

    private String userId;
    private String email;
    
    private String username;
    private String fullName;

    private Session() {}

    public static Session getInstance() {
        return instance;
    }

    public AppUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(AppUser currentUser) {
        this.currentUser = currentUser;
    }

    public WantedPerson getSelectedWantedPerson() {
        return selectedWantedPerson;
    }

    public void setSelectedWantedPerson(WantedPerson selectedWantedPerson) {
        this.selectedWantedPerson = selectedWantedPerson;
    }

    public void clear() {
        currentUser = null;
        selectedWantedPerson = null;
        userId = null;
        email = null;
        username = null;
        fullName = null;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}