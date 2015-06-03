package com.aause2s15.gruppe3.cheatingandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
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


public class JoinBTMatchActivity extends ActionBarActivity {

    private CheatingAndroidService mService = null;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_DEVICE_NAME:
                    // new device added > toast it!
                    String mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(), "Verbunden mit: " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_READ:
                    // welcome message from host > toast it!
                    byte[] readBuf = (byte[]) msg.obj;
                    String receivedMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(getApplicationContext(), receivedMessage, Toast.LENGTH_SHORT).show();
                    startTestMatch(null);
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

    public void sendMessageToHost(View v) {
            String welcome = "Hallo Host!";
            byte[] send = welcome.getBytes();
            mService.write(send);
    }

    public void discoverDevices(View v) {
        mBluetoothAdapter.startDiscovery();
    }

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
            } catch (Exception e) {
                toast(e.getMessage());
            }
        }
    };

    public void startTestMatch(View view) {
        Intent intent = new Intent(this, TestMatchActivity.class);
        startActivity(intent);
    }

    public void toast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_btmatch);

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mService = new CheatingAndroidService();
        mService.setHandler(mHandler);
        this.mPairedDevicesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        this.mNewDevicesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        showDevices();

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
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
