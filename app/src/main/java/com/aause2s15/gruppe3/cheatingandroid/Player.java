package com.aause2s15.gruppe3.cheatingandroid;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Player Class
 *
 * @author Simon Reisinger
 * @version 1.0
 * @since 2015-04-21
 */
public class Player {

    private ArrayList<Card> playerCards;
    private String playerName;
    private String playerAddress;

    /**
     * Constructs a Player object
     *
     * @param playerName the name of the player
     * @param playerAddress the MAC address of the player
     */
    public Player(String playerName, String playerAddress) {
        this.playerName = playerName;
        this.playerAddress = playerAddress;
        this.playerCards = new ArrayList<>(10);
    }

    /**
     * Returns the name of a player.
     *
     * @return the name of the player
     */
    public String getPlayerName(){
        return this.playerName;
    }

    /**
     * Returns the MAC address of a player.
     *
     * @return the MAC address of a player
     */
    public String getPlayerAddress(){
        return this.playerAddress;
    }

    /**
     * Draws a card from the deck, adds that card to the players cards and sorts these player
     * cards.
     *
     * @param cardDeck the specified card deck
     */
    public void drawCard(CardDeck cardDeck) {
        Card currentCard = cardDeck.drawTopCard();
        this.playerCards.add(currentCard);
        this.sortPlayerCards();
    }

    /**
     * Adds a card to a players hand.
     *
     * @param card the card that gets added to the players cards
     */
    public void addCard(Card card) {
        this.playerCards.add(card);
        this.sortPlayerCards();
    }

    /**
     * Removes a card from the players hand.
     *
     * @param playedCard the card that gets played by a player
     */
    public void playCard(Card playedCard) {
        this.playerCards.remove(playedCard);
    }

    /**
     * Returns an array with all cards of a players hand.
     *
     * @return the cards of a players hand
     */
    public ArrayList<Card> getPlayerCards() {
        return this.playerCards;
    }

    /**
     * Sorts player cards based on the cards order attribute.
     */
    public void sortPlayerCards() {
        Collections.sort(this.playerCards);
    }

}