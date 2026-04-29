package org.example.fugitivefinder.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.fugitivefinder.model.AppUser;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.service.UserService;
import org.example.fugitivefinder.session.Session;

public class CriminalProfileViewModel {

    private WantedPerson selectedPerson;

    private final StringProperty name = new SimpleStringProperty("Name");
    private final StringProperty aliases = new SimpleStringProperty("ALIASES:");
    private final StringProperty status = new SimpleStringProperty("STATUS:");
    private final StringProperty fieldOffices = new SimpleStringProperty("FIELD OFFICES:");
    private final StringProperty reward = new SimpleStringProperty("No Reward Listed");
    private final StringProperty description = new SimpleStringProperty("No description available.");
    private final StringProperty warning = new SimpleStringProperty("No warning provided.");
    private final StringProperty imageUrl = new SimpleStringProperty("");
    private final StringProperty sex = new SimpleStringProperty("SEX:");
    private final StringProperty race = new SimpleStringProperty("RACE:");
    private final StringProperty subjects = new SimpleStringProperty("SUBJECTS:");
    private final StringProperty saveButtonText = new SimpleStringProperty("Save Target");

    public StringProperty saveButtonTextProperty() {
        return saveButtonText;
    }
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty aliasesProperty() {
        return aliases;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty fieldOfficesProperty() {
        return fieldOffices;
    }

    public StringProperty rewardProperty() {
        return reward;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty warningProperty() {
        return warning;
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }
    public StringProperty sexProperty() {
        return sex;
    }

    public StringProperty raceProperty() {
        return race;
    }

    public StringProperty subjectsProperty() {
        return subjects;
    }

    public void loadSelectedPerson() {
        selectedPerson = Session.getInstance().getSelectedWantedPerson();
        if (selectedPerson == null) {
            return;
        }

        sex.set("SEX: " + selectedPerson.getDisplaySex());
        race.set("RACE: " + selectedPerson.getDisplayRace());
        subjects.set("SUBJECTS: " + selectedPerson.getDisplaySubjects());
        name.set(selectedPerson.getTitle());
        aliases.set("ALIASES: " + selectedPerson.getDisplayAliases());
        status.set("STATUS: " + selectedPerson.getStatus());
        fieldOffices.set("FIELD OFFICES: " + selectedPerson.getDisplayFieldOffices());
        reward.set(selectedPerson.getDisplayReward());
        description.set(selectedPerson.getDescription());
        warning.set(selectedPerson.getWarning_message() == null || selectedPerson.getWarning_message().isBlank()
                ? "No warning provided."
                : selectedPerson.getWarning_message());

        String url = selectedPerson.getPrimaryImageUrl();
        if (url != null && !url.isBlank()) {
            imageUrl.set(url);
        } else {
            imageUrl.set("https://via.placeholder.com/150");
        }
        AppUser currentUser = Session.getInstance().getCurrentUser();

        if (currentUser != null && selectedPerson.getUid() != null
                && currentUser.hasSavedTarget(selectedPerson.getUid())) {
            saveButtonText.set("Remove from Watchlist");
        } else {
            saveButtonText.set("Save Target");
        }
    }

    public void saveTarget() {
        AppUser currentUser = Session.getInstance().getCurrentUser();

        if (currentUser == null || selectedPerson == null || selectedPerson.getUid() == null) {
            System.out.println("Save failed. User or selected person is null.");
            return;
        }

        if (currentUser.hasSavedTarget(selectedPerson.getUid())) {
            UserService.removeTargetForUser(currentUser, selectedPerson.getUid());
            saveButtonText.set("Save Target");
        } else {
            UserService.saveTargetForUser(currentUser, selectedPerson.getUid());
            saveButtonText.set("Remove from Watchlist");
        }

        Session.getInstance().setCurrentUser(currentUser);

    }
}