package org.example.fugitivefinder.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.fugitivefinder.viewModel.CriminalProfileViewModel;
import org.example.fugitivefinder.viewModel.SceneManager;

public class CriminalProfileController {

    @FXML
    private ImageView criminalImageView;

    @FXML
    private Label nameLabel;

    @FXML
    private Label aliasesLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label fieldOfficesLabel;

    @FXML
    private Label rewardLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label warningLabel;

    private CriminalProfileViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new CriminalProfileViewModel();
        viewModel.loadSelectedPerson();

        nameLabel.textProperty().bind(viewModel.nameProperty());
        aliasesLabel.textProperty().bind(viewModel.aliasesProperty());
        statusLabel.textProperty().bind(viewModel.statusProperty());
        fieldOfficesLabel.textProperty().bind(viewModel.fieldOfficesProperty());
        rewardLabel.textProperty().bind(viewModel.rewardProperty());
        descriptionLabel.textProperty().bind(viewModel.descriptionProperty());
        warningLabel.textProperty().bind(viewModel.warningProperty());

        String imageUrl = viewModel.imageUrlProperty().get();
        System.out.println("PROFILE IMAGE URL: " + imageUrl);
        if (imageUrl != null && !imageUrl.isBlank()) {
            criminalImageView.setImage(new Image(imageUrl, true));
        }
    }

    @FXML
    private void goToDashboard() {
        SceneManager.switchScene(criminalImageView, "/org.example.fugitivefinder/dashboard.fxml", 1440, 900);
    }

    @FXML
    private void goToMap() {
        SceneManager.switchScene(criminalImageView, "/org.example.fugitivefinder/maps-view.fxml", 1440, 900);
    }

    @FXML
    private void goToAnalytics() {
        SceneManager.switchScene(criminalImageView, "/org.example.fugitivefinder/analytics.fxml", 1440, 900);
    }

    @FXML
    private void goToUserProfile() {
        SceneManager.switchScene(criminalImageView, "/org.example.fugitivefinder/user-profile.fxml", 1440, 900);
    }
}