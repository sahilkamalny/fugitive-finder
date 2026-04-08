package org.example.fugitivefinder.session;

import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.model.WantedPerson;

public class Session {

    private static final Session instance = new Session();

    private AppUser currentUser;
    private WantedPerson selectedWantedPerson;

    private Session() {
    }

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
    }
}