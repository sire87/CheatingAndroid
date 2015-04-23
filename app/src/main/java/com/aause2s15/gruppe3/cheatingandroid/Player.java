package com.aause2s15.gruppe3.cheatingandroid;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Simon on 21.04.2015.
 */
public class Player {

    private ArrayList<Card> playerCards;
    private String playerName;

    public Player(String playerName) {
        this.playerName = playerName;
        this.playerCards = new ArrayList<>(13);
    }

    public void drawCard(CardDeck cardDeck) {
        cardDeck.drawTopCard();
        Card currentCard = cardDeck.drawTopCard();
        this.playerCards.add(currentCard);
        this.sortPlayerCards();
    }

    public void addCard(Card card) {
        this.playerCards.add(card);
        this.sortPlayerCards();
    }

    public void playCard(Card playedCard) {
        this.playerCards.remove(playedCard);
    }

    public ArrayList<Card> getPlayerCards() {
        return this.playerCards;
    }

    public void sortPlayerCards() {
        Collections.sort(this.playerCards);
    }

}