package com.aause2s15.gruppe3.cheatingandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Debug;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

    // MATCH LOGIC RELATED VARIABLES
    private Player player1;

    private CardDeck callableCardDeck;

    private ArrayList<Card> stackedCards;
    private Card playedCard;
    private Card calledCard;
    private Card flippedCard;

    private boolean cardFlipped;

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
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        // INITIALISING CARD DECK
        // IMPORTANT: id is crucial for onClick()
        // 0 = callable card deck
        // 1 = player card deck
        this.callableCardDeck = new CardDeck(this,0);
        CardDeck cardDeck = new CardDeck(this,1);
        cardDeck.shuffle(5);

        // INITIALISING STACK
        this.stackedCards = new ArrayList<>(10);
        this.cardFlipped = false;

        // INITIALISING PLAYER
        // TODO: check value for player card count
        this.player1 = new Player("Player 1");
        for (int i = 0; i <13; i++) {
            this.player1.drawCard(cardDeck);
        }

        // RENDERING CARDS
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

        if (this.player1.getPlayerCards().size()>0) {
            moveX = (maxWidth-280) / this.player1.getPlayerCards().size();
            if (moveX > 80) moveX = 80;
        }

        Iterator it = this.player1.getPlayerCards().iterator();

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

        if (this.calledCard != null) {
            Card[] tempCardDeck = this.callableCardDeck.getCardDeck();
            ArrayList<Card> storeCallableCards = new ArrayList<>(15);
            findViewById(R.id.callableText).setVisibility(View.VISIBLE);

            for (int i=0; i<tempCardDeck.length; i++) {

                if (!tempCardDeck[i].getTag().substring(1).equals(this.calledCard.getTag().substring(1)) && (
                        tempCardDeck[i].getValue().equals(this.calledCard.getValue()) ||
                                tempCardDeck[i].getType().equals(this.calledCard.getType()))) {

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
                || (this.selectedPlayerCard != null && this.stackedCards.size()==0)) {

            for (Card c : this.player1.getPlayerCards()) {
                if (c.getTag().substring(1).equals(this.selectedPlayerCard.getTag().toString().substring(1))) {
                    this.playedCard = c;
                    this.calledCard = c;
                    break;
                }
            }

            if (this.selectedCallableCard != null) {
                for (Card c : this.callableCardDeck.getCardDeck()) {
                    if (c.getTag().substring(1).equals(this.selectedCallableCard.getTag().toString().substring(1))) {
                        this.calledCard = c;
                        break;
                    }
                }
            }

            // IMAGE VIEW
            ((ImageView) findViewById(R.id.calledCard)).setImageResource(this.calledCard.getImage());
            ((ImageView) this.selectedPlayerCard).setImageResource(R.drawable.card_56);
            this.selectedPlayerCard.setClickable(false);
            this.selectedPlayerCard.setX(0);

            // VIEW GROUP
            ((ViewGroup) findViewById(R.id.playerCardContainer)).removeView(this.selectedPlayerCard);
            ((ViewGroup) findViewById(R.id.cardStackContainer)).addView(this.selectedPlayerCard);
            Toast.makeText(this,"Angesagte Karte: "+this.calledCard.getTag().substring(1), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Schüttle dein Gerät, wenn du glaubst, dass dieser Spielzug nicht korrekt war.", Toast.LENGTH_SHORT).show();

            // DATA
            this.player1.playCard(playedCard);
            this.stackedCards.add(this.playedCard);
            toggleSelectPlayerCard(this.selectedPlayerCard);
            if (this.selectedCallableCard != null) toggleSelectCallableCard(this.selectedCallableCard);
            this.selectedCallableCard = null;

            // RENDERING
            renderPlayerCards();
            renderCallableCards();
        }
    }

    // move all cards from stack to player
    public void pickUpCards(View v) {

        if (this.stackedCards.size()>0) {

            ViewGroup stackedCardsContainer = (ViewGroup) findViewById(R.id.cardStackContainer);
            stackedCardsContainer.removeAllViews();

            this.calledCard = null;
            findViewById(R.id.callableText).setVisibility(View.INVISIBLE);
            this.selectedCallableCard = null;

            ((ImageView)findViewById(R.id.calledCard)).setImageResource(0);
            ((ViewGroup)findViewById(R.id.callableCardContainer)).removeAllViews();

            Iterator iterator = this.stackedCards.iterator();

            while (iterator.hasNext()) {
                Card currentStackedCard = (Card) iterator.next();
                this.player1.addCard(currentStackedCard);
                currentStackedCard.getImageView().setImageResource(currentStackedCard.getImage());
                stackedCardsContainer.removeView(currentStackedCard.getImageView());
                iterator.remove();
                renderPlayerCards();
            }
        }
    }

    // flip top card of stack
    public void flipCard(View v) {

        if (!cardFlipped && this.stackedCards.size()>0) {

            int index = this.stackedCards.size() - 1;
            Card flippedCard = this.stackedCards.get(index);
            this.flippedCard = flippedCard;
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

        if (this.calledCard != null) {

            if (this.flippedCard.getTag().substring(1).equals(this.calledCard.getTag().substring(1))) {
                return true;
            }

            else {
                return false;
            }
        }
        return true;
    }

    // behaviour for selecting player card
    // TODO: REFACTORING
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
    // TODO: REFACTORING
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

    // for debugging
    public void getMemoryUsage(View View) {
        int usedKBytes = (int) (Debug.getNativeHeapAllocatedSize() / 1024L);
        int freeKbytes = (int) (Debug.getNativeHeapFreeSize() / 1024L);
        String txt = "Memory Used: "+usedKBytes+" KB\n" +
                "Memory Free: "+freeKbytes+" KB";

        Toast.makeText(this,txt, Toast.LENGTH_SHORT).show();
    }
}