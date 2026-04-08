package org.example.fugitivefinder.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.UserProfileViewModel;

public class UserProfileController {

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private FlowPane savedTargetsPane;

    private UserProfileViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new UserProfileViewModel();
        viewModel.loadData();

        usernameLabel.textProperty().bind(viewModel.usernameProperty());
        fullNameLabel.textProperty().bind(viewModel.fullNameProperty());
        emailLabel.textProperty().bind(viewModel.emailProperty());

        String avatarPath = viewModel.avatarPathProperty().get();
        if (avatarPath != null && !avatarPath.isBlank()) {
            avatarImageView.setImage(
                    new Image(UserProfileController.class.getResource(avatarPath).toExternalForm())
            );
        }

        renderSavedTargets();
    }

    private void renderSavedTargets() {
        savedTargetsPane.getChildren().clear();

        for (WantedPerson person : viewModel.getSavedTargets()) {
            savedTargetsPane.getChildren().add(createSavedCard(person));
        }
    }

    private VBox createSavedCard(WantedPerson person) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(230);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);

        String imageUrl = person.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            imageView.setImage(new Image(imageUrl, true));
        }

        Label nameLabel = new Label(person.getTitle());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Label statusLabel = new Label(person.getStatus());
        statusLabel.setStyle("-fx-text-fill: #cbd5e1;");

        VBox details = new VBox(5, nameLabel, statusLabel);
        details.setPadding(new Insets(10));

        VBox card = new VBox(imageView, details);
        card.setPrefWidth(230);
        card.setPrefHeight(250);
        card.setStyle("-fx-background-color: #111827; -fx-background-radius: 14; -fx-border-color: #334155; -fx-border-radius: 14;");

        return card;
    }
}