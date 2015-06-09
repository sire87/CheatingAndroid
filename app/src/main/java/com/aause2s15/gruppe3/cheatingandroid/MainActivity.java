package com.aause2s15.gruppe3.cheatingandroid;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.aause2s15.gruppe3.cheatingandroid";

    // Bluetooth related stuff
    private BluetoothAdapter mBluetoothAdapter = null;

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Anwendung beendet: Gerät unterstützt kein Bluetooth", Toast.LENGTH_LONG).show();
            this.finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        String defaultPlayerName = mBluetoothAdapter.getName();
        EditText playerName = (EditText) findViewById(R.id.textPlayerName);
        playerName.setText(defaultPlayerName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Test Commit Bernhard
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /** called when the user clicks the create match button */
    public void createMatch(View view) {
        Intent intent = new Intent(this, CreateMatchActivity.class);
        EditText editText = (EditText) findViewById(R.id.textPlayerName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        if (!message.isEmpty()){
            startActivity(intent);
        }

        else {
            Toast.makeText(this, "Bitte gib zunächst deinen Namen an", Toast.LENGTH_SHORT).show();
        }
    }

    public void joinMatch(View view) {
        Intent intent = new Intent(this, JoinMatchActivity.class);
        EditText editText = (EditText) findViewById(R.id.textPlayerName);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        if (!message.isEmpty()){
            startActivity(intent);
        }

        else {
            Toast.makeText(this, "Bitte gib zunächst deinen Namen an", Toast.LENGTH_SHORT).show();
        }
    }
    public void rules (View view){
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }

    public void hostBTMatch(View v) {
        Intent i = new Intent(this, HostBTMatchActivity.class);
        EditText editText = (EditText) findViewById(R.id.textPlayerName);
        String message = editText.getText().toString();
        i.putExtra(EXTRA_MESSAGE, message);
        startActivity(i);
    }

    public void joinBTMatch(View v) {
        Intent i = new Intent(this,JoinBTMatchActivity.class);
        EditText editText = (EditText) findViewById(R.id.textPlayerName);
        String message = editText.getText().toString();
        i.putExtra(EXTRA_MESSAGE, message);
        startActivity(i);
    }

}
