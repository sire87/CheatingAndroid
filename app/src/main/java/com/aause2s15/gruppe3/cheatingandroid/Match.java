package com.aause2s15.gruppe3.cheatingandroid;

import java.io.Serializable;
import java.util.ArrayList;
import android.content.Context;


/**
 * Created by Simon on 13.05.2015.
 */
public class Match implements Serializable {

    private ArrayList<Player> players;
    private ArrayList<Card> stackedCards;

    private CardDeck cardDeck;
    private CardDeck callableCardDeck;

    private Card playedCard;
    private Card calledCard;
    private Card flippedCard;

    private boolean cardFlipped;

    public Match(Context context) {
        this.callableCardDeck = new CardDeck(context, 0);
        this.cardDeck = new CardDeck(context, 1);
        this.cardDeck.shuffle(5);
        this.stackedCards = new ArrayList<>(10);
        this.cardFlipped = false;
        this.players = new ArrayList<>(4);

    }

    public void addPlayer(Player p) {
        this.players.add(p);
        for (int i = 0; i < 10; i++) {
            this.players.get(0).drawCard(this.cardDeck);
        }
    }

    public int getPlayerID(String name) {
        return 0; // TODO: return ACTUAL ID
    }

    public boolean getCardFlipped() {
        return this.cardFlipped;
    }

    public Player getPlayer(int i) {
        return this.players.get(i);
    }

    public Card getCalledCard() {
        return this.calledCard;
    }

    public void setCalledCard(Card c) {
        this.calledCard = c;
    }

    public Card getPlayedCard() {
        return this.playedCard;
    }

    public void setPlayedCard(Card c) {
        this.playedCard = c;
    }

    public Card getFlippedCard() {
        return this.flippedCard;
    }

    public void setFlippedCard(Card c) {
        this.flippedCard = c;
    }

    public CardDeck getCallableCardDeck() {
        return this.callableCardDeck;
    }

    public ArrayList<Card> getStackedCards() {
        return this.stackedCards;
    }


}
