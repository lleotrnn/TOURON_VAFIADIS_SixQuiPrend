package com.isep.sixquiprend.Core;

import java.util.ArrayList;
import java.util.List;

public class Player {
    protected Game game;
    protected String name;
    protected int ID;
    protected List<Card> hand;
    protected List<Card> cards;
    protected int beefHeadCount;
    protected Card cardChoice;

    public Player(Game game, String name) {
        this.game = game;
        this.name = name;
        cards = new ArrayList<>();
        cardChoice = null;
        beefHeadCount = 0;
    }

    public void setAndOrderHand(List<Card> hand) {
        hand = game.orderCards(hand);
        System.out.println("Distributing cards to " + name);
        System.out.println("Hand size : " + hand.size());
        this.hand = hand;
    }
    public void pickCardByIndex(int index) {
        Card card = hand.get(index);
        game.getGameController().updatePlayerCard(this,card);
        setCardChoice(card);
        hand.remove(index);
    }
    public Integer getCardIndexByValue(int value) {
        for (int i = 0 ; i < hand.size() ; i++) {
            if (hand.get(i).getValue() == value) {
                return i;
            }
        }
        return null;
    }

    public void pickUpRow(Row row) {
        for (Card card : row.getCards()) {
            cards.add(card);
            beefHeadCount += card.getBeefHead();
        }
        game.getGameController().updatePlayerBeefHead(this);
    }

    public void throwAwayChosenCard() {
        hand.remove(cardChoice);
    }
    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getBeefHeadCount() {
        return beefHeadCount;
    }

    public void setCardChoice(Card card) {
        this.cardChoice = card;
    }
    public Card getCardChoice() {
        return cardChoice;
    }
    public boolean hasChosenCard() {
        return cardChoice != null;
    }
}
