package com.isep.sixquiprend.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot extends Player{
    private final int VERY_LOW_VALUE = 5;
    private final int QUITE_LOW_VALUE_TRESHOLD = 10;
    private final int LOW_VALUE_TRESHOLD = 15;
    private int difficulty;

    public Bot(Game game, String name, int difficulty) {
        super(game, name);
        this.difficulty = difficulty;
    }

    public Integer decideCardIndex() {
        if (difficulty == 0) {
            Random random = new Random();
            System.out.println("hand size : " + hand.size());
            int randomCardIndex = random.nextInt(hand.size());
            return randomCardIndex;
        }
        else {
            //First, if the lowest card in the hand is lower than a treshold, then check if there is a row with only one beefhead
            //So that they can get rid of their card without getting too many beefheads
            int lowBeefHeadRowsCount = 0;
            int minCardValue = hand.get(0).getValue();
            if (minCardValue < LOW_VALUE_TRESHOLD) {
                for (Row row : game.getRows()) {
                    if (row.getBeefHeadCount() == 1) {
                        if (minCardValue < VERY_LOW_VALUE) return 0;
                        lowBeefHeadRowsCount++;
                        if (minCardValue < QUITE_LOW_VALUE_TRESHOLD && lowBeefHeadRowsCount == 2) {
                            return 0;
                        }
                        if (minCardValue < LOW_VALUE_TRESHOLD && lowBeefHeadRowsCount == 3) {
                            return 0;
                        }
                    }
                }
            }

            //Search for the lowest card in the hand that is higher than the lowest last card of any on the board
            int minRowLastCardValue = getLowestLastCardValueFromRows();
            for (int i = 0 ; i < hand.size() ; i++) {
                if (hand.get(i).getValue() > minRowLastCardValue) {
                    return i;
                }
            }
            //If no card is higher than the lowest card on the board, then choose the lowest card
            //That way you can pick up the row  you want
            return 0;
        }
    }

    public int getLowestLastCardValueFromRows() {
        int minRowLastCardValue = game.getMaxCardValue();
        for (Row row : game.getRows()) {
            if ((row.getLastCardValue() < minRowLastCardValue) && (!row.isFull())) {
                minRowLastCardValue = row.getLastCardValue();
            }
        }
        return minRowLastCardValue;
    }

    public Row chooseRow() {
        //Choose the row with the least beef heads
        int minBeefHeadCount = 100;
        Row chosenRow = null;
        for (Row row : game.getRows()) {
            if (row == null) continue;
            int rowBeefHeadCount = row.getBeefHeadCount();
            if (rowBeefHeadCount < minBeefHeadCount) {
                minBeefHeadCount = rowBeefHeadCount;
                chosenRow = row;
            }
        }
        return chosenRow;
    }
}
