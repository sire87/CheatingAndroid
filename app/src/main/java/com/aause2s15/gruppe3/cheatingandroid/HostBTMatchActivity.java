package com.aause2s15.gruppe3.cheatingandroid;

import android.bluetooth.BluetoothAdapter;
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


public class HostBTMatchActivity extends ActionBarActivity {

    private CheatingAndroidService mService;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mConnectedDevicesArrayAdapter;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_DEVICE_NAME:
                    // new device added > toast it!
                    String mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(), "Verbunden mit: " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    refreshConnectedDevices(null);
                    break;
                case Constants.MESSAGE_READ:
                    // message from client > toast it
                    byte[] readBuf = (byte[]) msg.obj;
                    String receivedMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(getApplicationContext(), receivedMessage, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    public void sendMessageToClients(View v) {
        if (mConnectedDevicesArrayAdapter.getCount() > 0){
            String welcome = "Hallo Clients!";
            byte[] send = welcome.getBytes();
            mService.write(send);
            startTestMatch(null);
        }
    }

    public void startTestMatch(View view) {
        Intent intent = new Intent(this, TestMatchActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_btmatch);

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        this.mService = ((CheatingAndroidApplication)this.getApplicationContext()).caService;
        mService.setHandler(mHandler);
        mService.start(); // start listening

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String name = mBluetoothAdapter.getName();
        String address = mBluetoothAdapter.getAddress();
        ((TextView)findViewById(R.id.bt_name_address)).setText("Warte auf Verbindung\n\n" +
                "Spiel-Name: "+name+"\nSpiel-Adresse: "+address);

        ListView connectedListView = (ListView) findViewById(R.id.bt_connected_devices);
        mConnectedDevicesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        connectedListView.setAdapter(mConnectedDevicesArrayAdapter);
    }

    public void refreshConnectedDevices(View v) {
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
