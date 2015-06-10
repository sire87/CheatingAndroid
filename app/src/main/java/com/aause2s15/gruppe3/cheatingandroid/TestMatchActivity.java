package com.aause2s15.gruppe3.cheatingandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class TestMatchActivity extends ActionBarActivity implements View.OnClickListener, SensorEventListener {

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
            }
        }
    };

    private final Handler clientHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    // message from host > update game state
                    byte[] readBuf = (byte[]) msg.obj;
                    String receivedMessage = new String(readBuf, 0, msg.arg1);
                    parsePlayerMove(receivedMessage);
                    break;
            }
        }
    };

    private CheatingAndroidService mService;
    private Match match;
    private ArrayList<Player> players = new ArrayList<Player>(4);
    private String cardDeckString = "";
    private int playerID;
    private boolean active = false; //TODO: only allow interaction if true

    private View selectedPlayerCard;
    private View selectedCallableCard;

    // ACCELEROMETER RELATED VARIABLES
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // HIDE STATUS BAR AND ACTION BAR
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_test_match);

        // INITIALISING ACCELEROMETER
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Intent intent = getIntent();
        int host = intent.getIntExtra("HOST", 99);

        switch (host) {
            case Constants.HOST:
                this.active = true;
                break;
            case Constants.CLIENT:
                findViewById(R.id.b_sync).setVisibility(View.INVISIBLE);
                findViewById(R.id.b_deal_cards).setVisibility(View.INVISIBLE);
                break;
            default:
                Toast.makeText(this, "Keine BT-Verbindung!", Toast.LENGTH_SHORT).show();
                findViewById(R.id.b_sync).setVisibility(View.INVISIBLE);
        }
        mService = ((CheatingAndroidApplication)this.getApplicationContext()).caService;
        mService.setHandler(cardDeckHandler);
        parsePlayerData();
        toastPlayerInfo();
    }

    public void toastMessage(String msg){
        Toast.makeText(this, "Nachricht = "+msg, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Laenge = "+msg.length(), Toast.LENGTH_LONG).show();
    }

    public void parsePlayerData() {
        String playerData = mService.getPlayerData();
        String[] players = playerData.split("\\-");
        for (int i = 0; i<players.length; i++) {
            String[] player = players[i].split("\\.");
            String playerName = player.length > 0 ? player[0] : "";
            String playerAddress = player.length > 1 ? player[1] : "";
            try {
                this.players.add(new Player(playerName, playerAddress));
            } catch (Exception e) {}
        }
    }

    public void toastPlayerInfo() {
        for (int i = 0; i <this.players.size(); i++) {
            Player p = this.players.get(i);
            int id = i;
            String name = p.getPlayerName();
            String address = p.getPlayerAddress();
            String info = "ID: "+id+" Name: "+name+" Adresse: "+address;
            Toast.makeText(this, info, Toast.LENGTH_LONG).show();
        }
    }

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

        // TODO: problem?
        this.playerID = this.match.getPlayerID(mService.getPlayerAddress());
        Toast.makeText(this,"Meine ID ="+this.playerID,Toast.LENGTH_LONG).show();

        // RENDERING CARDS
        renderMatch();

        // CHANGE HANDLER
        mService.setHandler(clientHandler);
    }

    public void syncDeckWithClients(View v) {
        byte[] send = (Constants.CARD_DECK + cardDeckString).getBytes();
        mService.write(send);
        findViewById(R.id.b_sync).setVisibility(View.INVISIBLE);
    }

    public void syncPlayerMoveWithClients() {
        String playerID = Integer.toString(this.playerID);
        String playedCardTag = this.match.getPlayedCard().getTag();
        String calledCardTag = this.match.getCalledCard().getTag();
        String moveData = playerID+"."+playedCardTag+"."+calledCardTag;
        byte[] send = moveData.getBytes();
        mService.write(send);
    }

    public void parsePlayerMove(String move) {
        /*TODO:
        * 1. parse - done
        * 2. update match data - tbd
        * 3. update view - tbd*/

        Toast.makeText(this, "Nachricht = "+move, Toast.LENGTH_LONG).show();

        String[] tmp = move.split("\\.");
        int playerID = tmp.length > 0 ? Integer.parseInt(tmp[0]) : 0;
        String playedCardTag = tmp.length > 1 ? tmp[1] : "";
        String calledCardTag = tmp.length > 2 ? tmp[2] : "";

        Toast.makeText(this, "Spieler ID = "+playerID, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "gespielte Karte = "+playedCardTag.substring(1), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "angesagte Karte = "+calledCardTag.substring(1), Toast.LENGTH_SHORT).show();

        // TODO: solve issue: calledCard = null but playedCard not
        Card playedCard = this.match.getCardDeck().getCard(playedCardTag);
        Card calledCard = this.match.getCallableCardDeck().getCard(calledCardTag);
        // TESTING...
        if (calledCard != null){
            String test = calledCard.getTag();
            Toast.makeText(this, test, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "solve issue with calledCard", Toast.LENGTH_LONG).show();
        }

/*      this.match.getPlayer(playerID).playCard(playedCard);
        this.match.getStackedCards().add(playedCard);
        this.match.setPlayedCard(playedCard);
        this.match.setCalledCard(calledCard);

        ((ImageView) findViewById(R.id.calledCard)).setImageResource(this.match.getCalledCard().getImage());
        Toast.makeText(this, "Angesagte Karte: " + this.match.getCalledCard().getTag().substring(1), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Schüttle dein Gerät, wenn du glaubst, dass dieser Spielzug nicht korrekt war.", Toast.LENGTH_SHORT).show();*/

        renderMatch();
    }

    public void renderMatch() {
        renderPlayerCards();
        renderCallableCards();
    }

    // render player cards
    public void renderPlayerCards() {

        ViewGroup playerCardContainer = (ViewGroup) findViewById(R.id.playerCardContainer);
        playerCardContainer.removeAllViews();

        // TODO: find solution: offsetX not suitable for all devices
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

    // render callable cards
    public void renderCallableCards() {

        if (this.match.getCalledCard() != null) {
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

    // move card from player to stack
    public void playCard(View v)     {

        if ((this.selectedPlayerCard != null && this.selectedCallableCard !=null)
                || (this.selectedPlayerCard != null && this.match.getStackedCards().size()==0)) {

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
            Toast.makeText(this, "Angesagte Karte: " + this.match.getCalledCard().getTag().substring(1), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Schüttle dein Gerät, wenn du glaubst, dass dieser Spielzug nicht korrekt war.", Toast.LENGTH_SHORT).show();

            // DATA
            this.match.getPlayer(this.playerID).playCard(this.match.getPlayedCard());
            this.match.getStackedCards().add(this.match.getPlayedCard());
            toggleSelectPlayerCard(this.selectedPlayerCard);
            if (this.selectedCallableCard != null) toggleSelectCallableCard(this.selectedCallableCard);
            this.selectedCallableCard = null;

            // RENDERING
            renderMatch();

            // SYNCING
            syncPlayerMoveWithClients();
        }
    }

    // move all cards from stack to player
    public void pickUpCards(View v) {

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
                this.match.getPlayer(this.playerID).addCard(currentStackedCard);
                currentStackedCard.getImageView().setImageResource(currentStackedCard.getImage());
                stackedCardsContainer.removeView(currentStackedCard.getImageView());
                iterator.remove();
                renderPlayerCards();
            }
        }
    }

    // flip top card of stack
    public void flipCard(View v) {

        if (!this.match.getCardFlipped() && this.match.getStackedCards().size()>0) {

            int index = this.match.getStackedCards().size() - 1;
            Card flippedCard = this.match.getStackedCards().get(index);
            this.match.setFlippedCard(flippedCard);
            ImageView imgFlippedCard = flippedCard.getImageView();
            imgFlippedCard.setImageResource(flippedCard.getImage());
            if (validCard()) {
                Toast.makeText(this, "Du hast eine korrekte Karte gespielt!", Toast.LENGTH_SHORT).show();
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("LÜGNER!");
                alertDialog.setMessage("Du hast KEINE korrekte Karte gespielt und musst nun alle Karten aufnehmen!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                pickUpCards(null);
            }
        }
    }

    // check if flippedCard matches called card
    public boolean validCard() {

        if (this.match.getCalledCard() != null) {

            if (this.match.getFlippedCard().getTag().substring(1).equals(this.match.getCalledCard().getTag().substring(1))) {
                return true;
            }

            else {
                return false;
            }
        }
        return true;
    }

    // behaviour for selecting player card
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

    // behaviour for selecting callable card
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
        getMenuInflater().inflate(R.menu.menu_test_match, menu);
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

    @Override
    // called when card is clicked
    public void onClick(View v) {

        if (v.getTag().toString().startsWith("1")) {
            toggleSelectPlayerCard(v);
        }

        if (v.getTag().toString().startsWith("0")) {
            toggleSelectCallableCard(v);
        }
    }

    @Override
    // called when device is shaken
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

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

}