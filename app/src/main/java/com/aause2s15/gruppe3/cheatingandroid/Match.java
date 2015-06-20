package com.aause2s15.gruppe3.cheatingandroid;

import java.util.ArrayList;
import android.content.Context;

/**
 * Match Class
 *
 * @author Simon Reisinger
 * @version 1.0
 * @since 2015-05-13
 */
public class Match {

    private ArrayList<Player> players;
    private ArrayList<Card> stackedCards;

    private CardDeck cardDeck;
    private CardDeck callableCardDeck;

    private Card playedCard;
    private Card calledCard;
    private Card flippedCard;

    private boolean cardFlipped;

    /**
     * Constructs a Match object.
     *
     * @param context the current context.
     */
    public Match(Context context) {
        this.callableCardDeck = new CardDeck(context, 0);
        this.cardDeck = new CardDeck(context, 1);
        this.cardDeck.shuffle(5);
        this.stackedCards = new ArrayList<>(10);
        this.cardFlipped = false;
        this.players = new ArrayList<>(4);

    }

    /**
     * Adds a player to a match.
     *
     * @param p the Player object to add to a match
     * @param index the index of the player
     */
    public void addPlayer(Player p, int index) {
        this.players.add(p);
        for (int i = 0; i < 10; i++) {
            this.players.get(index).drawCard(this.cardDeck);
        }
    }

    /**
     * Returns the id of the player with a specified MAC address.
     *
     * @param playerAddress the MAC address of a player
     * @return the id of the player
     */
    public int getPlayerID(String playerAddress) {
        for (int i=0; i<players.size(); i++){
            if (players.get(i).getPlayerAddress().equals(playerAddress))
                return i;
        }
        return 0;
    }

    /**
     * Checks if a card is currently flipped.
     *
     * @return true, if a card is currently flipped, otherwise false
     */
    public boolean getCardFlipped() {
        return this.cardFlipped;
    }

    /**
     * Returns a player with a specified index.
     *
     * @param i the index of the player
     * @return the player object with the specified index
     */
    public Player getPlayer(int i) {
        return this.players.get(i);
    }

    /**
     * Returns the players of a match.
     *
     * @return an ArrayList with all Player objects of the match
     */
    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    /**
     * Returns the currently called card.
     *
     * @return the currently called card
     */
    public Card getCalledCard() {
        return this.calledCard;
    }

    /**
     * Sets the currently called card.
     *
     * @param c the card that is called
     */
    public void setCalledCard(Card c) {
        this.calledCard = c;
    }

    /**
     * Returns the currently played card.
     *
     * @return the currently played card
     */
    public Card getPlayedCard() {
        return this.playedCard;
    }

    /**
     * Sets the currently played card.
     *
     * @param c the card that is played
     */
    public void setPlayedCard(Card c) {
        this.playedCard = c;
    }

    /**
     * Returns the currently flipped card.
     *
     * @return the currently flipped card.
     */
    public Card getFlippedCard() {
        return this.flippedCard;
    }

    /**
     * Sets the currently flipped card.
     *
     * @param c the card that is flipped
     */
    public void setFlippedCard(Card c) {
        this.flippedCard = c;
    }

    /**
     * Returns the callable card deck of a match, which is basically a normal, ordered card deck
     * and used for comparison methods.
     *
     * @return the callable card deck
     */
    public CardDeck getCallableCardDeck() {
        return this.callableCardDeck;
    }

    /**
     * Returns the currenty stacked cards.
     *
     * @return an ArrayList with all currently stacked cards
     */
    public ArrayList<Card> getStackedCards() {
        return this.stackedCards;
    }

    /**
     * Returns the card deck of a match.
     *
     * @return the card deck
     */
    public CardDeck getCardDeck() {return this.cardDeck;}

    /**
     * Returns the id of a player with no cards left. Returns -1 if there is no such player.
     *
     * @return the id of a player with no cards left if there is one, otherwise -1
     */
    public int playerWithNoCardsExists() {
        for (int i = 0; i < this.players.size(); i++){
            if (this.players.get(i).getPlayerCards().size() == 0) {
                return i;
            }
        }
        return -1;
    }

}
