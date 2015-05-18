package com.aause2s15.gruppe3.cheatingandroid;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Simon on 18.05.2015.
 */
public class CheatingAndroidService extends Application {

    private static CheatingAndroidService singleton;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String NAME = "CheatingAndroidBluetooth";
    private static final UUID MY_UUID = UUID.fromString("b992a855-b913-4a22-80db-b6698bff46f0");

    public static CheatingAndroidService getInstance() {
        return singleton;
    }

    public final void onCreate(){
        super.onCreate();
        singleton = this;
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {}
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    // TODO: manageConnectedSocket(socket);
                    cancel(); // TODO: limit to 3 connections
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {}
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {}
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                return;
            }

            // manageConnectedSocket(mmSocket); // TODO:
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }

    }

    // temp
    public String getText() {
        return "Ist da jemand?";
    }


}
