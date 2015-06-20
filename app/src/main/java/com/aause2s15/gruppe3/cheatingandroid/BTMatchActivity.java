package com.aause2s15.gruppe3.cheatingandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * BTMatchActivity Class
 *
 * @author Simon Reisinger
 * @version 1.0
 */
public class BTMatchActivity extends ActionBarActivity implements View.OnClickListener, SensorEventListener {

    private CheatingAndroidService mService;
    private Match match;
    private ArrayList<Player> players = new ArrayList<Player>(4);
    private String cardDeckString = "";
    private int playerID;
    private int previousPlayerID;
    private boolean active = false;
    private boolean host = false;

    private View selectedPlayerCard;
    private View selectedCallableCard;

    // ACCELEROMETER RELATED VARIABLES
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1600;

    private final Handler cardDeckHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    // message from host > store card deck data
                    byte[] readBuf = (byte[]) msg.obj;
                    String receivedMessage = new String(readBuf, 0, msg.arg1);
                    cardDeckString = cardDeckString+receivedMessage;
                    findViewById(R.id.b_deal_cards).setVisibility(View.VISIBLE);
                    break;
                case Constants.MESSAGE_CONNECTION_LOST:
                    // connection was lost > toast it!
                    String tmp = msg.getData().getString("connection_lost");
                    Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_SHORT).show();
                    returnToMainMenu(null);
            }
        }
    };

    private final Handler clientHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    // message from host > update message state
                    byte[] readBuf = (byte[]) msg.obj;
                    String receivedMessage = new String(readBuf, 0, msg.arg1);
                    try {
                        interpretMessage(receivedMessage);
                    }
                    catch (Exception e) {}
                    break;
                case Constants.MESSAGE_CONNECTION_LOST:
                    // connection was lost > toast it!
                    String tmp = msg.getData().getString("connection_lost");
                    Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_SHORT).show();
                    returnToMainMenu(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // HIDE STATUS BAR AND ACTION BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_btmatch);

        // INITIALISING ACCELEROMETER
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Intent intent = getIntent();
        int host = intent.getIntExtra("HOST", 99);

        switch (host) {
            case Constants.HOST:
                setActive(1);
                this.host = true;
                break;
            case Constants.CLIENT:
                setActive(0);
                findViewById(R.id.b_deal_cards).setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
        mService = ((CheatingAndroidApplication)this.getApplicationContext()).caService;
        mService.setHandler(cardDeckHandler);

        parsePlayerData();
        toastPlayerInfo();
    }

    /**
     * Returns to main menu.
     *
     * @param v the view of the end button
     */
    public void returnToMainMenu(View v) {
        mService.stop();
//        Intent i = new Intent(this,MainActivity.class);
//        startActivity(i);
        this.finish();
    }

    /**
     * Called when back button is clicked.
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Willst du das Spiel beenden?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mService.connectionLost();
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Parses player data string and adds all players of a match to an array.
     */
    public void parsePlayerData() {
        String playerData = mService.getPlayerData();
        String[] players = playerData.split("\\-");
        for (int i = 0; i<players.length; i++) {
            String[] player = players[i].split("\\.");
            String playerName = player.length > 0 ? player[0] : "";
            String playerAddress = player.length > 1 ? player[1] : "";
            try {
                this.players.add(new Player(playerName, playerAddress));
            } catch (Exception e) {
                Toast.makeText(this, "Es trat ein Problem beim Parsen der Spielerdaten auf.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Toasts player info (id, name, address) when match is started.
     */
    public void toastPlayerInfo() {
        for (int i = 0; i <this.players.size(); i++) {
            Player p = this.players.get(i);
            String name = p.getPlayerName();
            String address = p.getPlayerAddress();
            String info = "Spieler "+i+": Name = "+name+" Adresse = "+address;
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initializes a new match. Sets own player id and the id of the previous and next player.
     *
     * @param v the view of the dealCards button
     */
    public void initMatch(View v) {

        findViewById(R.id.b_deal_cards).setVisibility(View.INVISIBLE);

        this.match = new Match(this);

        if (this.cardDeckString.equals("")) {
            this.cardDeckString = this.match.getCardDeck().getCardDeckString();
        }

        this.match.getCardDeck().buildCardDeckfromString(this, cardDeckString);

        for (int i = 0; i < this.players.size(); i++){
            this.match.addPlayer(this.players.get(i),i);
        }

        this.playerID = this.match.getPlayerID(mService.getPlayerAddress());
        this.previousPlayerID = getPreviousPlayerID();

        // TESTING TODO: DELETE IF NO NEEDED ANYMORE
        int nextPlayerID = getNextPlayerID();
        Toast.makeText(this, "Meine ID: "+this.playerID, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "vorherige ID: "+this.previousPlayerID, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "nächste ID: "+nextPlayerID, Toast.LENGTH_SHORT).show();

        if (this.host) {
            mService.setHandler(clientHandler);
        }
        else {
            mService.setHandler(clientHandler);
        }

        renderMatch();

        if (this.host)syncDeckWithClients();
    }

    /**
     * Returns the id of the previous player.
     *
     * @return the id of the previous player
     */
    public int getPreviousPlayerID(){
        return this.playerID != 0 ? this.playerID - 1 : this.players.size() - 1;
    }

    /**
     * Returns the id of the next player.
     *
     * @return the id of the next player
     */
    public int getNextPlayerID(){
        return this.playerID != this.players.size() - 1 ? this.playerID + 1 : 0;
    }

    /**
     * Generates a string containing the shuffled card deck and sends it to the other players so
     * that the card deck can be synchronized. Called only by the host of the match when the match
     * is started.
     */
    public void syncDeckWithClients() {
        byte[] send = (Constants.CARD_DECK + cardDeckString).getBytes();
        mService.write(send);
    }

    /**
     * Called when a player made a move. Generates a string containing all necessary information
     * and sends it to the other players so that this move can be synchronized.
     */
    public void syncPlayerMove() {
        String messageCode = Integer.toString(Constants.PLAYER_MOVE);
        String playerID = Integer.toString(this.playerID);
        String playedCardTag = this.match.getPlayedCard().getTag();
        String calledCardTag = this.match.getCalledCard().getTag();
        String moveData = messageCode+playerID+"."+playedCardTag+"."+calledCardTag;
        byte[] send = moveData.getBytes();
        mService.write(send);
    }

    /**
     * Called if a player has to pick up all stacked cards. Generates a string containing all
     * necessary information and sends it to the other players so that this move can be
     * synchronized.
     *
     * @param playerID the id of the player that picked up all stacked cards
     */
    public void syncPlayerPickup(int playerID) {
        String messageCode = Integer.toString(Constants.PLAYER_PICKUP);
        String tmp = Integer.toString(playerID);
        byte[] send = (messageCode+tmp).getBytes();
        mService.write(send);
    }

    /**
     * Called if a player has no cards left and the next player has not flipped the card or if
     * the move was valid. Generates a string containing all necessary information and sends it to
     * the other player so that this move can be synchronized.
     *
     * @param playerID the id of the player that has won the match
     */
    public void syncPlayerWon(int playerID){
        String messageCode = Integer.toString(Constants.PLAYER_WON);
        String tmp = Integer.toString(playerID);
        byte[] send = (messageCode+tmp).getBytes();
        mService.write(send);
    }

    /**
     * Checks if the previous player has played his last card.
     *
     * @return true if previous player has played his last card, otherwise false
     */
    public boolean previousPlayerLastCard() {
        if (this.match.getPlayer(previousPlayerID).getPlayerCards().size()==0){
            Toast.makeText(this, "Letzte Karte gespielt!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * Interpretes the message received by the handler. The first character of the string
     * represents the message type. Depending on the type the according processMessage method is
     * called.
     *
     * @param msg the string containing the received message
     */
    public void interpretMessage(String msg){
        int messageCode = Integer.parseInt(msg.substring(0, 1));
        switch (messageCode) {
            case Constants.PLAYER_MOVE:
                try {
                    processPlayerMoveMessage(msg.substring(1));
                }
                catch (NullPointerException e) {}
                break;
            case Constants.PLAYER_PICKUP:
                try {
                    processPlayerPickupMessage(msg.substring(1));
                }
                catch (NullPointerException e) {}
                break;
            case Constants.PLAYER_WON:
                try {
                    processPlayerWonMessage(msg.substring(1));
                }
                catch (NullPointerException e) {}
                break;
            default:
                break;
        }
    }

    /**
     * Parses playerMoveMessage and updates match data a view.
     *
     * @param move the string containing the player id the tags of the played and the called card
     */
    public void processPlayerMoveMessage(String move) {

        String[] tmp = move.split("\\.");
        int playerID = tmp.length > 0 ? Integer.parseInt(tmp[0]) : 0;
        String playedCardTag = tmp.length > 1 ? tmp[1] : "";
        String calledCardTag = tmp.length > 2 ? tmp[2] : "";

        // if host receives message, send to all clients
        if (this.host) {
            String messageCode = Integer.toString(Constants.PLAYER_MOVE);
            String moveData = messageCode+playerID+"."+playedCardTag+"."+calledCardTag;
            byte[] send = moveData.getBytes();
            mService.write(send);
        }

        // otherwise don't do anything because move was already made!
        if (playerID != this.playerID){

            if (playerID == getPreviousPlayerID()){
                setActive(1); // now it is this players turn
            }

            Card playedCard = this.match.getCardDeck().getCard(playedCardTag);
            Card calledCard = this.match.getCallableCardDeck().getCard(calledCardTag);

            if (calledCard == null) {
                calledCard = this.match.getCardDeck().getCard(calledCardTag);
            }

            // update match data
            this.match.getPlayer(playerID).playCard(playedCard);
            this.match.getStackedCards().add(playedCard);
            this.match.setPlayedCard(playedCard);
            this.match.setCalledCard(calledCard);

            // render called card
            ((ImageView) findViewById(R.id.calledCard)).setImageResource(this.match.getCalledCard().getImage());

            // render played card / stacked cards
            playedCard.getImageView().setImageResource(R.drawable.card_56);
            ((ViewGroup) findViewById(R.id.cardStackContainer)).addView(playedCard.getImageView());

            // toasting info
            previousPlayerLastCard();

            if (this.active) {
                toast("Schüttle dein Gerät, wenn du glaubst, dass dieser Spielzug nicht korrekt war.");
            }

            renderMatch();
        }
    }

    /**
     * Parses player id string to int and calls pickUpCards method.
     *
     * @param pickup the string containing the id of player to pick up cards
     */
    public void processPlayerPickupMessage(String pickup) {
        int playerPickupID = Integer.parseInt(pickup);

        try {
            pickUpCards(playerPickupID);
            String name = players.get(playerPickupID).getPlayerName();
            if (name.equals(players.get(this.playerID).getPlayerName())){
                toast("Die gespielte Karte war NICHT korrekt! Du musst alle Karten aufnehmen!");
            }
            else {
                toast(name+" musste alle Karten aufnehmen!");
            }
        }
        catch (Exception e) {}
    }

    /**
     * Parses player id string to int and informs player on who the winner is.
     *
     * @param won the string containing the id of player that has won the match
     */
    public void processPlayerWonMessage(String won) {
        int playerWonID = Integer.parseInt(won);
        String name = players.get(playerWonID).getPlayerName();
        String myName = players.get(this.playerID).getPlayerName();

        if (this.host) {
            String messageCode = Integer.toString(Constants.PLAYER_WON);
            String tmp = Integer.toString(playerWonID);
            byte[] send = (messageCode+tmp).getBytes();
            mService.write(send);
        }

        if (name.equals(myName)) {
            ((TextView)findViewById(R.id.txt_win)).setText("Gratulation! Du hast das Spiel gewonnen!");
        }
        else {
            ((TextView)findViewById(R.id.txt_win)).setText(name+" hat das Spiel gewonnen!");
        }
        setActive(-1);
    }

    /**
     * calls renderPlayerCards and renderCallableCards method
     */
    public void renderMatch() {
        renderPlayerCards();
        renderCallableCards();
    }

    /**
     * renders player cards
     */
    public void renderPlayerCards() {
        ViewGroup playerCardContainer = (ViewGroup) findViewById(R.id.playerCardContainer);
        playerCardContainer.removeAllViews();

        // TODO: offsetX not suitable for all devices
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int maxWidth = displayMetrics.widthPixels;
        float offsetX = 0;
        float moveX = 80;

        if (this.match.getPlayer(this.playerID).getPlayerCards().size()>0) {
            moveX = (maxWidth-280) / this.match.getPlayer(this.playerID).getPlayerCards().size();
            if (moveX > 80) moveX = 80;
        }

        Iterator it = this.match.getPlayer(this.playerID).getPlayerCards().iterator();

        while (it.hasNext()) {

            Card currentPlayerCard = (Card) it.next();

            currentPlayerCard.getImageView().setClickable(true);
            currentPlayerCard.getImageView().setOnClickListener(this);
            currentPlayerCard.getImageView().setX(offsetX);

            offsetX = offsetX + moveX;

            playerCardContainer.addView(currentPlayerCard.getImageView());
        }
    }

    /**
     * renders callable cards
     */
    public void renderCallableCards() {

        if (this.active && this.match.getCalledCard() != null) {
            Card[] tempCardDeck = this.match.getCallableCardDeck().getCardDeck();
            ArrayList<Card> storeCallableCards = new ArrayList<>(15);
            findViewById(R.id.callableText).setVisibility(View.VISIBLE);

            for (int i=0; i<tempCardDeck.length; i++) {

                if (!tempCardDeck[i].getTag().substring(1).equals(this.match.getCalledCard().getTag().substring(1)) && (
                        tempCardDeck[i].getValue().equals(this.match.getCalledCard().getValue()) ||
                                tempCardDeck[i].getType().equals(this.match.getCalledCard().getType()))) {

                    tempCardDeck[i].getImageView().setY(0);
                    storeCallableCards.add(tempCardDeck[i]);
                }
            }

            ViewGroup callableCardContainer = (ViewGroup) findViewById(R.id.callableCardContainer);
            callableCardContainer.removeAllViews();

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int maxHeight = displayMetrics.heightPixels;
            int offsetY = 0;
            int moveY = (maxHeight-320) / storeCallableCards.size();
            if (moveY > 40) moveY = 40;


            Collections.sort(storeCallableCards);
            Iterator iterator = storeCallableCards.iterator();

            while (iterator.hasNext()) {
                ImageView crrntImgView = ((Card)iterator.next()).getImageView();
                crrntImgView.setY(crrntImgView.getY()+offsetY);
                offsetY = offsetY + moveY;
                crrntImgView.setOnClickListener(this);
                crrntImgView.setClickable(true);
                callableCardContainer.addView(crrntImgView);
            }
        }
    }

    /**
     * plays currently selected card, removing it from player hand and adding it to card stack
     *
     * @param v view of playCard button
     */
    public void playCard(View v) {

        if (this.active && previousPlayerLastCard()) {
            syncPlayerWon(previousPlayerID);
            String tmp = Integer.toString(previousPlayerID);
            processPlayerWonMessage(tmp);
            setActive(-1);
        }

        else if ((this.active && this.selectedPlayerCard != null && this.selectedCallableCard !=null)
                || (this.active && this.selectedPlayerCard != null && this.match.getStackedCards().size()==0)) {

            for (Card c : this.match.getPlayer(this.playerID).getPlayerCards()) {
                if (c.getTag().substring(1).equals(this.selectedPlayerCard.getTag().toString().substring(1))) {
                    this.match.setPlayedCard(c);
                    this.match.setCalledCard(c);
                    break;
                }
            }

            if (this.selectedCallableCard != null) {
                for (Card c : this.match.getCallableCardDeck().getCardDeck()) {
                    if (c.getTag().substring(1).equals(this.selectedCallableCard.getTag().toString().substring(1))) {
                        this.match.setCalledCard(c);
                        break;
                    }
                }
            }

            // IMAGE VIEW
            ((ImageView) findViewById(R.id.calledCard)).setImageResource(this.match.getCalledCard().getImage());
            ((ImageView) this.selectedPlayerCard).setImageResource(R.drawable.card_56);
            this.selectedPlayerCard.setClickable(false);
            this.selectedPlayerCard.setX(0);

            // VIEW GROUP
            ((ViewGroup) findViewById(R.id.playerCardContainer)).removeView(this.selectedPlayerCard);
            ((ViewGroup) findViewById(R.id.cardStackContainer)).addView(this.selectedPlayerCard);

            // DATA
            this.match.getPlayer(this.playerID).playCard(this.match.getPlayedCard());
            this.match.getStackedCards().add(this.match.getPlayedCard());
            toggleSelectPlayerCard(this.selectedPlayerCard);
            if (this.selectedCallableCard != null) toggleSelectCallableCard(this.selectedCallableCard);
            this.selectedCallableCard = null;

            // RENDERING
            setActive(0);
            ((ViewGroup)findViewById(R.id.callableCardContainer)).removeAllViews();
            renderMatch();

            // SYNCING
            syncPlayerMove();
        }
    }

    /**
     * removes all cards from car stack and adds them to the hand of the player with the specified
     * id
     *
     * @param playerID id of the player to pick up cards
     */
    public void pickUpCards(int playerID) {

        if (this.match.getStackedCards().size()>0) {

            ViewGroup stackedCardsContainer = (ViewGroup) findViewById(R.id.cardStackContainer);
            stackedCardsContainer.removeAllViews();

            this.match.setCalledCard(null);
            findViewById(R.id.callableText).setVisibility(View.INVISIBLE);
            this.selectedCallableCard = null;

            ((ImageView)findViewById(R.id.calledCard)).setImageResource(0);
            ((ViewGroup)findViewById(R.id.callableCardContainer)).removeAllViews();

            Iterator iterator = this.match.getStackedCards().iterator();

            while (iterator.hasNext()) {
                Card currentStackedCard = (Card) iterator.next();
                this.match.getPlayer(playerID).addCard(currentStackedCard);
                currentStackedCard.getImageView().setImageResource(currentStackedCard.getImage());
                stackedCardsContainer.removeView(currentStackedCard.getImageView());
                iterator.remove();
                renderPlayerCards();
            }
            // SYNCING
            syncPlayerPickup(playerID);
        }
    }

    /**
     * Flips top card of card stack and checks if the called card matches the flipped card.
     * If it matches the player who flipped the card has to pick up all cards from the card stack,
     * otherwise the previous player has to.
     *
     * @param v the view of the flipCard button
     */
    public void flipCard(View v) {

        if (!this.match.getCardFlipped() && this.match.getStackedCards().size()>0 && this.active) {

            int index = this.match.getStackedCards().size() - 1;
            Card flippedCard = this.match.getStackedCards().get(index);
            this.match.setFlippedCard(flippedCard);
            ImageView imgFlippedCard = flippedCard.getImageView();
            imgFlippedCard.setImageResource(flippedCard.getImage());
            if (validCard() && previousPlayerLastCard()) {
                syncPlayerWon(previousPlayerID);
                String tmp = Integer.toString(previousPlayerID);
                processPlayerWonMessage(tmp);
            }
            else if (validCard()) {
                Toast.makeText(this, "Die gespielte Karte war korrekt! Du musst alle Karten aufnehmen!", Toast.LENGTH_SHORT).show();
                pickUpCards(this.playerID);
            }
            else {
                String name = players.get(this.previousPlayerID).getPlayerName();
                Toast.makeText(this, "Die gespielte Karte war NICHT korrekt! "+name+" muss alle Karten aufnehmen!", Toast.LENGTH_SHORT).show();
                pickUpCards(this.previousPlayerID);
            }
        }
    }

    /**
     * Checks if flipped card matches the called card.
     *
     * @return true if card is valid, otherwise false
     */
    public boolean validCard() {

        if (this.match.getCalledCard() != null) {

            return this.match.getFlippedCard().getTag().substring(1).equals(this.match.getCalledCard().getTag().substring(1));
        }
        return true;
    }

    /**
     * Sets this player to active or inactive.
     *
     * @param b integer -1 indicating game ending, 0 sets player inactive, 1 to active
     */
    public void setActive(int b) {
        if (b == -1) {
            this.active = false;
            findViewById(R.id.txt_win).setVisibility(View.VISIBLE);
            findViewById(R.id.bt_end).setVisibility(View.VISIBLE);
            findViewById(R.id.txt_active).setVisibility(View.INVISIBLE);
            findViewById(R.id.b_flip_card).setVisibility(View.INVISIBLE);
            findViewById(R.id.b_play_card).setVisibility(View.INVISIBLE);
            findViewById(R.id.img_arrow).setVisibility(View.INVISIBLE);
            findViewById(R.id.callableText).setVisibility(View.INVISIBLE);
            findViewById(R.id.playerCardContainer).setVisibility(View.INVISIBLE);
            findViewById(R.id.callableCardContainer).setVisibility(View.INVISIBLE);
            findViewById(R.id.tr_cards).setVisibility(View.INVISIBLE);
            findViewById(R.id.tr_txt).setVisibility(View.INVISIBLE);
        }
        else if (b == 0) {
            this.active = false;
            findViewById(R.id.txt_active).setVisibility(View.INVISIBLE);
            findViewById(R.id.b_flip_card).setVisibility(View.INVISIBLE);
            findViewById(R.id.b_play_card).setVisibility(View.INVISIBLE);
            findViewById(R.id.img_arrow).setVisibility(View.INVISIBLE);
            findViewById(R.id.callableText).setVisibility(View.INVISIBLE);
        }
        else if (b == 1) {
            this.active = true;
            findViewById(R.id.txt_active).setVisibility(View.VISIBLE);
            findViewById(R.id.b_flip_card).setVisibility(View.VISIBLE);
            findViewById(R.id.b_play_card).setVisibility(View.VISIBLE);
            findViewById(R.id.img_arrow).setVisibility(View.VISIBLE);
            findViewById(R.id.callableText).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Selects a card from player hand if clicked by the player. If there is already a selected
     * card, that one gets deselected first.
     *
     * @param v the view of the selected card
     */
    public void toggleSelectPlayerCard(View v) {

        if (this.selectedPlayerCard == v) {
            this.selectedPlayerCard.setY(this.selectedPlayerCard.getY() + 30);
            ((ImageView) this.selectedPlayerCard).setColorFilter(getResources().getColor(R.color.noHighlightColor));
            this.selectedPlayerCard = null;
        }

        else if (this.selectedPlayerCard != null) {
            this.selectedPlayerCard.setY(this.selectedPlayerCard.getY() + 30);
            ((ImageView) this.selectedPlayerCard).setColorFilter(getResources().getColor(R.color.noHighlightColor));
            ((ImageView) v).setColorFilter(getResources().getColor(R.color.highlightColor));
            this.selectedPlayerCard = v;
            this.selectedPlayerCard.setY(this.selectedPlayerCard.getY() - 30);
        }

        else {
            ((ImageView) v).setColorFilter(getResources().getColor(R.color.highlightColor));
            this.selectedPlayerCard = v;
            this.selectedPlayerCard.setY(this.selectedPlayerCard.getY() - 30);
        }
    }

    /**
     * Selects a card from callable cards if clicked by the player. If there is already a selected
     * card, that one gets deselected first.
     *
     * @param v the view of the selected card
     */
    public void toggleSelectCallableCard(View v) {

        if (this.selectedCallableCard == v) {
            this.selectedCallableCard.setX(this.selectedCallableCard.getX() - 30);
            ((ImageView) v).setColorFilter(getResources().getColor(R.color.noHighlightColor));
            this.selectedCallableCard = null;
        }

        else if (this.selectedCallableCard != null) {
            this.selectedCallableCard.setX(this.selectedCallableCard.getX() - 30);
            ((ImageView) this.selectedCallableCard).setColorFilter(getResources().getColor(R.color.noHighlightColor));
            ((ImageView) v).setColorFilter(getResources().getColor(R.color.highlightColor));
            this.selectedCallableCard = v;
            this.selectedCallableCard.setX(this.selectedCallableCard.getX() + 30);
        }

        else {
            ((ImageView) v).setColorFilter(getResources().getColor(R.color.highlightColor));
            this.selectedCallableCard = v;
            this.selectedCallableCard.setX(this.selectedCallableCard.getX() + 30);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_btmatch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * calls toggleSelectPlayerCard method if tag of clicked card tag starts with 1 or
     * toggleSelectCallableCard if clicked card tag starts with 0.
     *
     * @param v the view of the clicked card
     */
    public void onClick(View v) {

        if (v.getTag().toString().startsWith("1")) {
            toggleSelectPlayerCard(v);
        }

        if (v.getTag().toString().startsWith("0")) {
            toggleSelectCallableCard(v);
        }
    }

    /**
     * Called when the device gets shaken. Calls flipCard method.
     *
     * @param sensorEvent the specified sensor event
     */
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(300);
                    flipCard(null);
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Toasts  a specified message.
     *
     * @param message the message to be toasted
     */
    public void toast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}