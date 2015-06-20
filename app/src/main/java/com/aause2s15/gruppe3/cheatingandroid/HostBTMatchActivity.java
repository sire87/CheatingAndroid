package com.aause2s15.gruppe3.cheatingandroid;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * HostBTMatchActivity Class
 *
 * @author Simon Reisinger
 * @version 1.0
 */
public class HostBTMatchActivity extends ActionBarActivity {

    private CheatingAndroidService mService;
    private BluetoothAdapter mBluetoothAdapter;
    private String name;
    private String address;
    private ArrayAdapter<String> mConnectedDevicesArrayAdapter;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_DEVICE_NAME:
                    // new device connected > toast it!
                    String mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(), "Verbunden mit: " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    findViewById(R.id.sync).setVisibility(View.VISIBLE);
                    findViewById(R.id.bt_connected).setFadingEdgeLength(View.VISIBLE);
                    refreshConnectedDevices();
                    break;
                case Constants.MESSAGE_CONNECTION_LOST:
                    // connection was lost > toast it!
                    String tmp = msg.getData().getString("connection_lost");
                    Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_SHORT).show();
                    returnToMainMenu();
            }
        }
    };

    /**
     * Sends a message to clients containing all necessarry information about all players that
     * have joined the match an starts the match.
     *
     * @param v the view of the start match button
     */
    public void sendMessageToClients(View v) {
        if (mConnectedDevicesArrayAdapter.getCount() > 0){
            String playerData = this.name+"."+this.address+"-";
            for (int i = 0; i < mService.getmDeviceAddresses().size(); i++) {
                String name = mService.getmDeviceNames().get(i);
                String address = mService.getmDeviceAddresses().get(i);
                playerData = playerData+name+"."+address+"-";
            }
            byte[] send = playerData.getBytes();
            mService.write(send);
            mService.setPlayerData(playerData);
            toast(mService.getPlayerData());
            findViewById(R.id.bt_start_game).setVisibility(View.VISIBLE);
//            startMatch();

        }
    }

    /**
     * Toasts  a specified message.
     *
     * @param message the message to be toasted
     */
    public void toast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts a new Match.
     */
    public void startMatch(View v) {
        Intent intent = new Intent(this, BTMatchActivity.class);
        intent.putExtra("HOST", Constants.HOST);
        startActivity(intent);
        this.finish();
    }

    /**
     * Makes the host device discoverable for other bluetooth devices. Sets the handler for the
     * host to react to messages from clients. Lists all clients that have successfully connected
     * to the host.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_btmatch);

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent = getIntent();
        String playerName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        mBluetoothAdapter.setName(playerName);

        this.mService = ((CheatingAndroidApplication)this.getApplicationContext()).caService;
        mService.stop();
        mService.start(); // start listening
        mService.setHandler(mHandler);

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
        startActivity(discoverableIntent);

        this.name = mBluetoothAdapter.getName();
        this.address = mBluetoothAdapter.getAddress();
        ((TextView) findViewById(R.id.bt_name_address)).setText("Warte auf Verbindung\n\n" +
                "Spiel-Name: " + this.name + "\nSpiel-Adresse: " + this.address);

        ListView connectedListView = (ListView) findViewById(R.id.bt_connected_devices);
        mConnectedDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        connectedListView.setAdapter(mConnectedDevicesArrayAdapter);
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
                        returnToMainMenu();
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
     * Returns to main menu.
     */
    public void returnToMainMenu() {
        mService.stop();
//        Intent i = new Intent(this,MainActivity.class);
//        startActivity(i);
        this.finish();
    }

    /**
     * Updates connected devices ListView
     */
    public void refreshConnectedDevices() {
        mConnectedDevicesArrayAdapter.add(mService.getLastConnectedDevice());
        mConnectedDevicesArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host_btmatch, menu);
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
}
