package com.isep.sixquiprend.GUI;

import com.isep.sixquiprend.Core.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ProfileCreationController {
    @FXML
    private TextField nameTextField;
    @FXML
    private Button numberOfBots1Button;
    @FXML
    private Button numberOfBots2Button;
    @FXML
    private Button numberOfBots3Button;
    @FXML
    private Button botLevelProButton;
    @FXML
    private Button botLevelNoobButton;
    private GameApplication application;
    private Button chosenBotLevelButton;
    private Button chosenNumberOfBotsButton;

    public ProfileCreationController(GameApplication application) {
        this.application = application;
        chosenBotLevelButton = null;
        chosenNumberOfBotsButton = null;
    }

    @FXML
    public void initialize() {
        numberOfBots1Button.setOnAction(this::chooseNumberOfBotsEvent);
        numberOfBots2Button.setOnAction(this::chooseNumberOfBotsEvent);
        numberOfBots3Button.setOnAction(this::chooseNumberOfBotsEvent);
        botLevelProButton.setOnAction(this::chooseBotLevelEvent);
        botLevelNoobButton.setOnAction(this::chooseBotLevelEvent);
        chooseBotLevelButton(botLevelNoobButton);
        chooseNumberOfBotsButton(numberOfBots3Button);
    }

    @FXML
    public void startGame(ActionEvent e) {
        if (nameTextField.getText().isEmpty()) {
            return;
        }
        int numberOfBots = Integer.parseInt(chosenNumberOfBotsButton.getText());
        int difficulty = chosenBotLevelButton.getText().equals("Noob") ? 0 : 1;
        application.startGame(nameTextField.getText(), numberOfBots, difficulty);
    }

    @FXML
    public void chooseBotLevelEvent(ActionEvent e) {
        if (!(e.getSource() instanceof Button)) {return;}
        chooseBotLevelButton((Button) e.getSource());
    }

    @FXML
    public void chooseNumberOfBotsEvent(ActionEvent e) {
        if (!(e.getSource() instanceof Button)) {return;}
        chooseNumberOfBotsButton((Button) e.getSource());
    }

    public void chooseBotLevelButton(Button button) {
        if (chosenBotLevelButton != null) {
            chosenBotLevelButton.setDisable(false);
        }
        button.setDisable(true);
        chosenBotLevelButton = button;
    }

    public void chooseNumberOfBotsButton(Button button) {
        if (chosenNumberOfBotsButton != null) {
            chosenNumberOfBotsButton.setDisable(false);
        }
        button.setDisable(true);
        chosenNumberOfBotsButton = button;
    }
}
