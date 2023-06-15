package com.isep.sixquiprend.GUI;

import com.isep.sixquiprend.Core.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class EndGamePopupController {
    @FXML
    private Button newGameButton;

    @FXML
    private Button quitButton;
    @FXML
    private Label announcementLabel;
    @FXML
    private Label rankingLabel;
    @FXML
    private ImageView endGameBackgroundImage;
    private GameApplication application;
    private int finalPlayerRanking;

    public EndGamePopupController(GameApplication application, int finalPlayerRanking) {
        this.application = application;
        this.finalPlayerRanking = finalPlayerRanking;
    }

    @FXML
    public void initialize() {
        String imagePath = "scene/";
        String announcementText = "";
        String rankingText = "Ranking " + finalPlayerRanking;
        if (finalPlayerRanking == 1) {
            imagePath += "WinPopup.png";
            announcementText = "You won!";
        }
        else {
            imagePath += "WinPopup.png"; //TODO: find a better image
            announcementText = "You lost!";
        }
        endGameBackgroundImage.setImage(application.getImage(imagePath));
        announcementLabel.setText(announcementText);
        rankingLabel.setText(rankingText);

    }

    @FXML
    public void onButtonClick(ActionEvent e) {
        if (e.getSource().equals(newGameButton)) {
            application.startGame();
        }
        if (e.getSource().equals(quitButton)) {
            application.startMenu();
        }
    }
}
