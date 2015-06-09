package com.aause2s15.gruppe3.cheatingandroid;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Simon on 21.04.2015.
 */
public class Player {

    private ArrayList<Card> playerCards;
    private String playerName;
    private String playerAddress;

    public Player(String playerName, String playerAddress) {
        this.playerName = playerName;
        this.playerAddress = playerAddress;
        this.playerCards = new ArrayList<>(10);
    }

    public String getPlayerName(){
        return this.playerName;
    }

    public String getPlayerAddress(){
        return this.playerAddress;
    }

    public void drawCard(CardDeck cardDeck) {
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