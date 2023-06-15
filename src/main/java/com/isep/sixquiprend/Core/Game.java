package com.isep.sixquiprend.Core;

import com.isep.sixquiprend.GUI.DialogueBox;
import com.isep.sixquiprend.GUI.GameApplication;
import com.isep.sixquiprend.GUI.GameController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private boolean DEBUG_MODE = false;
    private final int MAX_BEEFHEAD_COUNT = 25;
    private final int PLAYER_CARDS_PER_ROUND = 10;
    private final int ROW_COUNT = 4;
    private final int HAND_SIZE = 10;
    private final int MAX_PLAYER_COUNT = 4;

    private final int minCardValue = 1;
    private final int maxCardValue = 104;
    private int chosenPlayerCount;
    private GameState gameState;
    private List<Player> players;
    private Player mainPlayer;
    private int currentPlayerIndex;
    private List<Row> rows;
    private GameApplication application;
    private GameController gameController;
    private DialogueBox dialogueBox;
    private int roundNumber;
    private int difficulty;

    public Game(GameApplication application, String mainPlayerUsername, int chosenBotCount, int difficulty) {
        this.application = application;
        this.difficulty = difficulty;
        players = new ArrayList<>();
        mainPlayer = new Player(this,mainPlayerUsername);
        roundNumber = 1;
        chosenPlayerCount = 1 + chosenBotCount;
        players.add(mainPlayer);
        for (int i = 1; i < chosenPlayerCount; i++) {
            players.add(new Bot(this,"Bot " + i,difficulty));
        }
        System.out.println("Game created with " + chosenPlayerCount + " players" + " and difficulty " + difficulty);
    }

    public void start() {
        dialogueBox = DialogueBox.getInstance();
        gameState = GameState.STARTING;
        startNewRound();
    }

    public void startNewRound() {
        dialogueBox.displayInfo("Everyone has received their hand. Let's distribute the cards for each row.");
        dialogueBox.setOnFinish((e) -> distributeCards());
    }

    public void startNewTurn() {
        System.out.println("Starting new turn");
        dialogueBox.displayInfo("A new turn has begun. Please pick the card that you want to play.");
        dialogueBox.setOnFinish((e) ->         gameState = GameState.WAITING_FOR_CARD_PICK);
    }

    public void pickPlayerCard(int cardValue) {
        gameState = GameState.PLAYING;
        System.out.println("Picking card " + cardValue);
        getMainPlayer().pickCardByIndex(getMainPlayer().getCardIndexByValue(cardValue));
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player instanceof Bot) {
                player.pickCardByIndex(((Bot) player).decideCardIndex());
            }
        }
        continueTurn();
    }

    public void continueTurn() {
        orderPlayersByCardChoice();
        currentPlayerIndex = 0;
        executePlayerMove();
    }

    public void executePlayerMove() {
        Player player = players.get(currentPlayerIndex);
        Integer rowIndex = findRow(player.getCardChoice());
        dialogueBox.displayInfo("It is " + player.getName() + "'s turn.");
        if (rowIndex == null) { //if card cannot be placed in any row, the player must pick up a row
            System.out.println("No row found for card " + player.getCardChoice().getValue());
            dialogueBox.displayInfo(player.getName() + " could not place their card " + player.getCardChoice().getValue() + " in any row.");
            if (player instanceof Bot) {
                giveRowToPlayer(((Bot) player).chooseRow(),player);
            }
            else {
                dialogueBox.displayInfo("Pick a row to pick up.");
                dialogueBox.setOnFinish((e) -> gameState = GameState.WAITING_FOR_ROW_PICK);
            }
        }
        else {
            System.out.println("Row found for card " + player.getCardChoice().getValue() + " : " + rowIndex);
            Row rowForCard = rows.get(rowIndex);
            if (rowForCard.isFull()) {
                dialogueBox.displayInfo(player.getName()  + " had to place their card in is full. They must pick up the row.");
                giveRowToPlayer(rowForCard,player);
                return;
            }
            addCardToRow(player.getCardChoice(),rowIndex,false);
            dialogueBox.setOnFinish((e) -> {
                gameController.playCardMovementAnimation(player, rowIndex, rowForCard.getLastCardIndex(), (finish) -> {
                    decideActionAfterMove();
                });
            });
        }
    }

    public void createNewRowFromCard(int rowIndex, Card card) {
        rows.set(rowIndex,new Row());
        gameController.resetRow(rowIndex);
        addCardToRow(card,rowIndex,true);
    }

    public void executeMainPlayerRowPickup(int rowIndex) {
        gameState = GameState.PLAYING;
        giveRowToPlayer(getRow(rowIndex),getMainPlayer());
    }

    public void decideActionAfterMove() {
        System.out.println("Deciding action after move");
        dialogueBox.setOnFinish((e) -> {
            if (!isTurnEnded()) {
                currentPlayerIndex++;
                executePlayerMove();
            }
        });
    }

    public void giveRowToPlayer(Row row, Player player) {
        int rowIndex = rows.indexOf(row);
        dialogueBox.displayInfo(player.getName() + " picked up row " + (rowIndex + 1));
        dialogueBox.setOnFinish((e) -> {
            gameController.resetPlayerCard(player);
            player.pickUpRow(row);
            rows.set(rowIndex,null);
            createNewRowFromCard(rowIndex,player.getCardChoice());
            decideActionAfterMove();
        });
    }

    public Integer findRow(Card card) {
        boolean hasFoundRow = false;
        int cardValue = card.getValue();
        int minDiff = 200;
        int minRowIndex = 0;
        for (int i = 0 ; i < rows.size(); i++) {
            Row row = rows.get(i);
            if (row == null) continue;
            if (row.canAddCard(card)) {
                hasFoundRow = true;
                int diff = Math.abs(row.getLastCardValue() - cardValue);
                if (diff < minDiff) {
                    minDiff = diff;
                    minRowIndex = i;
                }
            }
        }
        // If no row is available, return null
        if (!hasFoundRow) return null;
        // Else, return the index of the row with the smallest difference between its last card and the card to add
        return minRowIndex;

    }

    public void distributeCards() {

        // Create a list with all possible card values
        List<Integer> allCardValues = new ArrayList<>();
        for (int i = minCardValue; i <= maxCardValue; i++) {
            allCardValues.add(i);
        }
        Collections.shuffle(allCardValues);

        for (int i = 0 ; i < players.size(); i++) {
            // Pick the player cards for their deck
            List<Card> playerHand = new ArrayList<>();
            for (int j = 0 ; j < PLAYER_CARDS_PER_ROUND ; j++) {
                int cardValue = allCardValues.remove(0);
                playerHand.add(new Card(cardValue));
                System.out.println("Player " + i + " card " + j + " : " + cardValue);
            }
            players.get(i).setAndOrderHand(playerHand);
        }
        setRows(allCardValues,() -> {
            gameController.updateMainPlayerHand(getMainPlayer().getHand());
            dialogueBox.displayInfo("The cards for this round have been distributed.");
            gameState = GameState.PLAYING;
            startNewTurn();
        });
    }

    public void setRows(List<Integer> allCardValues, Runnable runnable) {
        rows = new ArrayList<>();
        List<Card> rowCards = new ArrayList<>();
        for (int j = 0; j < ROW_COUNT; j ++) {
            rows.add(new Row());
            int cardValue = allCardValues.remove(0);
            rowCards.add(new Card(cardValue));
        }
        rowCards = orderCards(rowCards);
        distributeRow(rowCards,0,runnable);
    }

    public void distributeRow(List<Card> rowCards, int rowIndex, Runnable onFinished) {
        if (rowIndex == ROW_COUNT) {
            onFinished.run();
            return;
        }
        addCardToRow(rowCards.get(rowIndex),rowIndex,false);
        gameController.playDistributeRowCardAnimation(rowIndex,rowCards.get(rowIndex).getValue(),() -> {
            distributeRow(rowCards,rowIndex + 1,onFinished);
        });
    }

    public void addCardToRow(Card card, int rowIndex, boolean instantPlacement) {
        Row row = rows.get(rowIndex);
        row.addCard(card);
        if (instantPlacement) gameController.placeCardInRow(card.getValue(),rowIndex, row.getLastCardIndex());
    }

    public List<Card> orderCards(List<Card> cards) {
            // order the cards in ascending order
            // useful for the bots to pick the best card, and to display the hand and the rows in the right order for the main player
            for (int i = 0; i < cards.size() - 1; i++) {
                for (int j = i + 1; j < cards.size(); j++) {
                    Card card1 = cards.get(i);
                    Card card2 = cards.get(j);

                    // If the value of the property for the first object is greater than the value of the property
                    // for the second object, swap them in the list
                    if (card1.getValue() > card2.getValue()) {
                        cards.set(i, card2);
                        cards.set(j, card1);
                    }
                }
            }
            return cards;
    }
    public void orderPlayersByCardChoice() {
        for (int i = 0; i < players.size() - 1; i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player player1 = players.get(i);
                Player player2 = players.get(j);

                // If the value of the property for the first object is greater than the value of the property
                // for the second object, swap them in the list
                if (player1.getCardChoice().getValue() > player2.getCardChoice().getValue()) {
                    players.set(i, player2);
                    players.set(j, player1);
                }
            }
        }
        dialogueBox.displayInfo("Based on the cards of other players, you are going to play in position " + (players.indexOf(getMainPlayer()) + 1));
    }

    public int getPlayerFinalRanking() {
        for (int i = 0; i < players.size() - 1; i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player player1 = players.get(i);
                Player player2 = players.get(j);
                if (player1.getBeefHeadCount() > player2.getBeefHeadCount()) {
                    players.set(i, player2);
                    players.set(j, player1);
                }
            }
        }
        int playerRanking = players.indexOf(getMainPlayer()) + 1;
/*        if (playerRanking == 1 && players.get(1).getBeefHeadCount() == getMainPlayer().getBeefHeadCount()) {
            playerRanking = 0;
        }*/
        return playerRanking;
    }

    public void finishTurn() {
        System.out.println("Finishing turn.");
        dialogueBox.displayInfo("This turn has ended!");
        startNewTurn();
    }

    public void finishRound() {
        System.out.println("Finishing round.");
        dialogueBox.displayInfo("This round has ended!");
        incrementRoundNumber();
        gameController.prepareForNewRound();
        startNewRound();
    }

    public void end() {
        dialogueBox.displayInfo("The game has ended!");
        dialogueBox.setOnFinish((e) -> {
            application.displayEndGamePopup(getPlayerFinalRanking());
        });
    }

    public boolean isTurnEnded() {
        if (isRoundEnded()) {
            return true;
        }
        if (!(currentPlayerIndex < players.size() - 1)) {
            finishTurn();
            return true;
        }
        return false;
    }

    public boolean isRoundEnded() {
        if (isGameEnded()) {
            end();
            return true;
        }
        if ((getMainPlayer().getHand().size() == 0 && (currentPlayerIndex == players.size() - 1))  || (!isThereAnyRowLeft())) {
            finishRound();
            return true;
        }
        return false;
    }

    public boolean isGameEnded() {
        for (Player player : players) {
            if (player.getBeefHeadCount() >= MAX_BEEFHEAD_COUNT) {
                return true;
            }
            System.out.println(player.getName() + " has " + player.getBeefHeadCount() + " beefheads");
        }
        return false;
    }

    public boolean isThereAnyRowLeft() {
        for (Row row : rows) {
            if (row != null) {
                return true;
            }
        }
        return false;
    }

    public void incrementRoundNumber() {
        roundNumber++;
        gameController.updateRoundNumber(roundNumber);
    }
    public Player getMainPlayer() {
        return mainPlayer;
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public List<Row> getRows() {
        return rows;
    }

    public Row getRow(int index) {
        return rows.get(index);
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isInDebugMode() {
        return DEBUG_MODE;
    }

    public int getMaxHandSize() {
        return HAND_SIZE;
    }

    public int getMaxCardValue() {
        return maxCardValue;
    }

    public int getChosenBotCount() {
        return chosenPlayerCount - 1;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getMaxPlayerCount() {
        return MAX_PLAYER_COUNT;
    }


}
