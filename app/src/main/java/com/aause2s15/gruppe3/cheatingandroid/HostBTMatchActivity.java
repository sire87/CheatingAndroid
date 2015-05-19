package com.aause2s15.gruppe3.cheatingandroid;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class HostBTMatchActivity extends ActionBarActivity {

    private CheatingAndroidService mCheatingAndroidService = null;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_btmatch);

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        this.mCheatingAndroidService = new CheatingAndroidService();
        mCheatingAndroidService.start(); // start listening

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String name = mBluetoothAdapter.getName();
        String address = mBluetoothAdapter.getAddress();
        ((TextView)findViewById(R.id.bt_name_address)).setText("Warte auf Verbindung\n\n" +
                "Spiel-Name: "+name+"\nSpiel-Adresse: "+address);

        toastConnectedDevices(null);
        ListView connectedListView = (ListView) findViewById(R.id.bt_connected_devices);
        connectedListView.setAdapter(mCheatingAndroidService.getmConnectedDevicesArrayAdapter());
    }

    public void toastConnectedDevices(View v) {
        String tmp = mCheatingAndroidService.getConnectedDevices();
        Toast.makeText(this,"Verbunden mit: " + tmp,Toast.LENGTH_SHORT).show();
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
