package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.FbiApiService;
import org.example.fugitivefinder.session.Session;

import java.util.List;

public class UserProfileViewModel {

    private final StringProperty username = new SimpleStringProperty("Username");
    private final StringProperty fullName = new SimpleStringProperty("Full Name");
    private final StringProperty email = new SimpleStringProperty("Email");
    private final StringProperty avatarPath = new SimpleStringProperty("");
    private final ObservableList<WantedPerson> savedTargets = FXCollections.observableArrayList();

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty avatarPathProperty() {
        return avatarPath;
    }

    public ObservableList<WantedPerson> getSavedTargets() {
        return savedTargets;
    }

    public void loadData() {
        AppUser currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        username.set(currentUser.getUsername());
        fullName.set(currentUser.getFullName());
        email.set(currentUser.getEmail());
        avatarPath.set(currentUser.getProfilePicUrl());

        List<WantedPerson> allPeople = FbiApiService.getWantedPeople();

        savedTargets.clear();
        for (WantedPerson person : allPeople) {
            if (person.getUid() != null && currentUser.getSavedTargetIds().contains(person.getUid())) {
                savedTargets.add(person);
            }
        }
    }
}