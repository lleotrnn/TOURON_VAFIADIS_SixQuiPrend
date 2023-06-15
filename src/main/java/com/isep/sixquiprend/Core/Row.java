package com.isep.sixquiprend.Core;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private final int MAX_NUMBER_OF_CARDS = 5;
    private List<Card> cards;

    public Row() {
        cards = new ArrayList<>();
    }

    public boolean canAddCard(Card card) {

        // If the row is empty, the card can always be added
        if (cards.size() == 0) {
            return true;
        }

        // Otherwise, check if the card can be added to the row according to the game rules
        int lastCardValue = getLastCardValue();
        int cardNumber = card.getValue();
        return (lastCardValue <= cardNumber);
    }


    public void addCard(Card card) {
        cards.add(card);
    }

    public boolean isFull() { return (cards.size() >= MAX_NUMBER_OF_CARDS) ;}

    public List<Card> getCards() {
        return cards;
    }

    public int getBeefHeadCount() {
        int rowBeefHeadCount = 0;
        for (Card card : cards) {
            rowBeefHeadCount += card.getBeefHead();
        }
        return rowBeefHeadCount;
    }

    public int getLastCardValue() {
        return cards.get(cards.size() - 1).getValue();
    }

    public int getLastCardIndex() {
        return cards.size() - 1;
    }
}
