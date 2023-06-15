package com.isep.sixquiprend.Core;

public class Card {
    private int value;
    private int beefHead;

    public Card(int value) {
        this.value = value;
        if(value == 55){
            beefHead = 7;
        } else if (value % 10 == 5){ //finit en 5
            beefHead = 2;
        } else if (value % 10 == 0){ //finit en 0
            beefHead = 3;
        } else if (value % 11 == 0){ //doublet
            beefHead = 5;
        } else { //autres cartes
            beefHead = 1;
        }
    }

    public int getValue() {
        return value;
    }

    public int getBeefHead() {
        return beefHead;
    }
}
