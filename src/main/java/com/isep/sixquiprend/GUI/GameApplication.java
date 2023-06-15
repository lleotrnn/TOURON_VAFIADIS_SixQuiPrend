package com.isep.sixquiprend.GUI;

import com.isep.sixquiprend.Core.Game;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class GameApplication extends javafx.application.Application {
    private String GAME_TITLE = "Six Qui Prend";
    private final String GAME_ROOT = "/com/isep/sixquiprend/";
    private final String END_GAME_POPUP_FILE_NAME = "EndGamePopup";
    private DialogueBox dialogueBox;
    private Game game;
    private Stage stage;
    @Override
    public void start(Stage stage) {

        stage.setTitle(GAME_TITLE);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(GAME_ROOT  + "images/scene/Icon.png")));
        this.stage = stage;

        startMenu();
    }

    public static void main(String[] args) {
        launch();
    }

    public void startMenu() {
        setScene("Start", param -> new StartController(this));
    }

    public void startProfileCreation() {
        setScene("ProfileCreation", param -> new ProfileCreationController(this));
    }

    public void startGame(String mainPlayerUsername, int chosenBotCount, int difficulty) {
        this.game = new Game(this, mainPlayerUsername, chosenBotCount, difficulty);
        GameController gameController = new GameController(this, game);
        game.setGameController(gameController);
        setScene("game", param -> gameController);
        dialogueBox = DialogueBox.getInstance(getGame());
        showElement("DialogueBox",param -> dialogueBox,701,426);
        game.start();
    }

    public void startGame() {
        startGame(game.getMainPlayer().getName(),game.getChosenBotCount(),game.getDifficulty());
    }

    public void displayEndGamePopup(int finalPlayerRanking) {
        showElement(END_GAME_POPUP_FILE_NAME, param -> new EndGamePopupController(this,finalPlayerRanking),2.7);
    }

    public void setScene(String name, Callback<Class<?>, Object> callback) {
        URL fxmlURL = getClass().getResource(name + ".fxml");
        if (fxmlURL == null) {
            throw new RuntimeException("FXML file not found: " + name);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
        if (callback != null) {
            fxmlLoader.setControllerFactory(callback);
        }
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML file: " + name, e);
        }
        stage.setScene(scene);
        stage.show();
    }

    public void showElement(String document, Callback<Class<?>, Object> callback, double layoutX, double layoutY, AnchorPane root) {
        FXMLLoader fxmlLoader = loadFXML(document);
        fxmlLoader.setControllerFactory(callback);
        try {
            AnchorPane anchorPane = fxmlLoader.load();
            if (root == null) {
                root = ((AnchorPane) stage.getScene().getRoot());
            }
            if (layoutX == layoutY) { // if layoutx and layouty, the goal is to center
                anchorPane.setLayoutY((root.getHeight() - anchorPane.getHeight()) / layoutY);
                anchorPane.setLayoutX((root.getWidth() - anchorPane.getWidth()) / layoutX);
            }
            else {
                anchorPane.setLayoutX(layoutX);
                anchorPane.setLayoutY(layoutY);
            }
            root.getChildren().add(anchorPane);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void showElement(String document, Callback<Class<?>, Object> callback, double layoutX, double layoutY) {
        showElement(document, callback, layoutX, layoutY,null);
    }

    public void showElement(String document, Callback<Class<?>, Object> callback, double centerCoeff) {
        showElement(document, callback, centerCoeff, centerCoeff);
    }

    public FXMLLoader loadFXML(String name) {
        URL fxmlURL = getClass().getResource(GAME_ROOT + "GUI/" + name + ".fxml");
        if (fxmlURL == null) {
            throw new RuntimeException("FXML file not found: " + name);
        }
        else {
            return new FXMLLoader(fxmlURL);
        }
    }

    public void closeSubWindowById(String id) {
        ((AnchorPane) stage.getScene().getRoot()).getChildren().removeIf((node) -> node != null && node.getId().equals(id));
    }

    public Image getImage(String imagePath) {
        return new Image(getClass().getResourceAsStream(GAME_ROOT + "images/" + imagePath));
    }

    public Game getGame() {
        return game;
    }

    public Stage getStage() {
        return stage;
    }

    public DialogueBox getDialogueBox() {
        return dialogueBox;
    }
}