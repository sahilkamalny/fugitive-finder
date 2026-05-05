package org.example.fugitivefinder.view;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.SceneManager;
import org.example.fugitivefinder.viewModel.UserProfileViewModel;

public class UserProfileController {

    @FXML private ImageView avatarImageView;
    @FXML private Label usernameLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private FlowPane savedTargetsPane;

    private UserProfileViewModel viewModel;

    @FXML
    public void initialize() {

        viewModel = new UserProfileViewModel();

        usernameLabel.textProperty().bind(viewModel.usernameProperty());
        fullNameLabel.textProperty().bind(viewModel.fullNameProperty());
        emailLabel.textProperty().bind(viewModel.emailProperty());


        viewModel.getSavedTargets().addListener(
                (ListChangeListener<WantedPerson>) change -> renderSavedTargets()
        );


        viewModel.loadData();
    }

    @FXML
    private void goToDashboard(MouseEvent event) {
        viewModel.goToDashboard((Node) event.getSource());
    }

    @FXML
    private void goToMaps(MouseEvent event) {
        viewModel.goToMaps((Node) event.getSource());
    }

    @FXML
    private void goToAnalytics(MouseEvent event) {
        viewModel.goToAnalytics((Node) event.getSource());
    }

    @FXML
    private void goToLeaderboard(MouseEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "/org.example.fugitivefinder/leaderboard.fxml", 1440, 900);
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
        nameLabel.setWrapText(true);

        Label statusLabel = new Label(person.getStatus());
        statusLabel.setStyle("-fx-text-fill: #cbd5e1;");

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-background-radius: 8;");

        removeButton.setOnAction(e -> {
            String uid = org.example.fugitivefinder.session.Session.getInstance().getUserId();

            org.example.fugitivefinder.service.FirestoreService.removeTarget(uid, person.getUid());

            viewModel.getSavedTargets().remove(person);
            savedTargetsPane.getChildren().removeIf(node -> node.getUserData() == person);

            System.out.println("Removed saved target: " + person.getUid());

            e.consume();
        });

        VBox details = new VBox(8, nameLabel, statusLabel, removeButton);
        details.setPadding(new Insets(10));

        VBox card = new VBox(imageView, details);
        card.setUserData(person);
        card.setPrefWidth(230);
        card.setPrefHeight(290);
        card.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #334155;" +
                        "-fx-border-radius: 14;"
        );

        card.setOnMouseClicked(e -> {
            org.example.fugitivefinder.session.Session.getInstance().setSelectedWantedPerson(person);

            org.example.fugitivefinder.viewModel.SceneManager.switchScene(
                    card,
                    "/org.example.fugitivefinder/criminal-profile.fxml",
                    1440,
                    900
            );
        });

        return card;
    }
}