package org.example.fugitivefinder.view;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.viewModel.RewardsViewModel;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class RewardsController {

    @FXML
    private TextField searchField;

    @FXML
    private FlowPane rewardsCardsPane;

    private RewardsViewModel viewModel;

    @FXML
    public void initialize() {
        viewModel = new RewardsViewModel();
        searchField.textProperty().bindBidirectional(viewModel.searchQueryProperty());

        viewModel.loadData();
        renderCards();
    }

    @FXML
    private void handleSearch() {
        viewModel.filter();
        renderCards();
    }
    @FXML
    private void goToDashboard(MouseEvent event) {
        viewModel.goToDashboard((Node) event.getSource());
    }

    @FXML
    private void goToUserProfile(MouseEvent event) {
        viewModel.goToUserProfile((Node) event.getSource());
    }

    private void renderCards() {
        rewardsCardsPane.getChildren().clear();

        for (WantedPerson person : viewModel.getFilteredRewardCases()) {
            rewardsCardsPane.getChildren().add(createRewardCard(person));
        }
    }

    private VBox createRewardCard(WantedPerson person) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(170);
        imageView.setPreserveRatio(false);

        String imageUrl = person.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            imageView.setImage(new Image(imageUrl, true));
        }

        Label rewardBanner = new Label(person.getDisplayReward());
        rewardBanner.setStyle("-fx-background-color: #b91c1c; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-weight: bold;");
        rewardBanner.setMaxWidth(Double.MAX_VALUE);

        Label nameLabel = new Label(person.getTitle());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Label statusLabel = new Label(person.getStatus());
        statusLabel.setStyle("-fx-text-fill: #cbd5e1;");

        VBox details = new VBox(5, nameLabel, statusLabel);
        details.setPadding(new Insets(10));

        VBox card = new VBox(imageView, rewardBanner, details);
        card.setPrefWidth(250);
        card.setPrefHeight(300);
        card.setStyle("-fx-background-color: #111827; -fx-background-radius: 14; -fx-border-color: #334155; -fx-border-radius: 14;");
        card.setOnMouseClicked(event -> viewModel.openCriminalProfile(rewardsCardsPane, person));

        return card;
    }
}