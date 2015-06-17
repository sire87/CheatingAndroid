package com.aause2s15.gruppe3.cheatingandroid;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;

/**
 * JoinBTMatchActivity Class
 *
 * @author Simon Reisinger
 * @version 1.0
 */
public class JoinBTMatchActivity extends ActionBarActivity {

    private CheatingAndroidService mService;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_DEVICE_NAME:
                    // connected to host > toast it!
                    String mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(), "Verbunden mit: " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    sendClientPlayerDataToHost();
                    break;
                case Constants.MESSAGE_CONNECTION_LOST:
                    // connection was lost > toast it!
                    String tmp = msg.getData().getString("connection_lost");
                    Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG).show();
                    returnToMainMenu(null);
                case Constants.MESSAGE_READ:
                    // welcome message with player data from host > store data and start game
                    byte[] readBuf = (byte[]) msg.obj;
                    String receivedMessage = new String(readBuf, 0, msg.arg1);
                    mService.addPlayerData(receivedMessage);
                    startMatch();
                    break;
            }
        }

    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBluetoothAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Toast.makeText(getApplicationContext(), "Adresse: "+address, Toast.LENGTH_LONG).show();
            // now connect to that device
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            try {
                mService.connect(device);
                ((TextView)findViewById(R.id.txt_connected_to_var)).setText(device.getName());
                findViewById(R.id.txt_connected_to_var).setVisibility(View.VISIBLE);
                findViewById(R.id.txt_connected_to_fix).setVisibility(View.VISIBLE);
                findViewById(R.id.txt_waiting).setVisibility(View.VISIBLE);
                findViewById(R.id.bt_new_devices).setVisibility(View.INVISIBLE);
                findViewById(R.id.bt_scan).setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                toast(e.getMessage());
            }
        }
    };

    /**
     * Updates ListView displaying all discovered bluetooth devices.
     */
    public void showDevices() {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
/*        ListView pairedListView = (ListView) findViewById(R.id.bt_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);*/

        ListView newDevicesListView = (ListView) findViewById(R.id.bt_new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
    }

    /**
     * Sends a message to the host containing player name and player MAC address.
     */
    public void sendClientPlayerDataToHost() {
        Intent intent = getIntent();
        String playerName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String playerAddress = mBluetoothAdapter.getAddress();
        String playerData = playerName+"."+playerAddress+"-";
        byte[] send = playerData.getBytes();
        mService.write(send);
    }

    /**
     * Starts searching for discoverable bluetooth deveces.
     *
     * @param v the view of the start discovery button
     */
    public void discoverDevices(View v) {
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * Called if the host clicks on the start match button. Starts BTMatchActivity.
     */
    public void startMatch() {
        Intent intent = new Intent(this, BTMatchActivity.class);
        intent.putExtra("HOST", Constants.CLIENT);
        startActivity(intent);
        finish();
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
     * Sets the handler for clients to react on messages from the host. Lists all bluetooth devices
     * that were found during discovery.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_btmatch);

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mService = ((CheatingAndroidApplication)this.getApplicationContext()).caService;
        mService.setHandler(mHandler);
        this.mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        this.mNewDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        showDevices();

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    /**
     * Called when back button is clicked.
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Willst du abbrechen?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        returnToMainMenu(null);
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
     *
     * @param v the view of the abort button
     */
    public void returnToMainMenu(View v) {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_btmatch, menu);
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
