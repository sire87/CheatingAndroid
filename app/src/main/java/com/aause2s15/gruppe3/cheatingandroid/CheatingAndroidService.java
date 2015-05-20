package com.aause2s15.gruppe3.cheatingandroid;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Simon on 18.05.2015.
 */
public class CheatingAndroidService extends Application {

    private static CheatingAndroidService singleton;
    private Handler mHandler = null;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String NAME = "CheatingAndroidBluetooth";
    private static final UUID MY_UUID = UUID.fromString("b992a855-b913-4a22-80db-b6698bff46f0");

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private String lastConnectedDevice = "Keine Verbindung";

    public static CheatingAndroidService getInstance() {
        return singleton;
    }

    public final void onCreate(){
        super.onCreate();
        singleton = this;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public String getLastConnectedDevice() {
        return lastConnectedDevice;
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            r = mConnectedThread;
        }
        r.write(out);
    }

    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device) {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // TODO: Cancel the accept thread, when 3 devices connected or match started

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // TODO: Do something with device.getName()
        lastConnectedDevice = device.getName() + "\n" + device.getAddress();
        Message msg = mHandler.obtainMessage(1);
        Bundle bundle = new Bundle();
        bundle.putString("device_name", lastConnectedDevice);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {}
            mmServerSocket = tmp;
        }

        public void run() {

            setName("AcceptThread");

            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    synchronized (CheatingAndroidService.this){
                        connected(socket, socket.getRemoteDevice());
                        break;
                    }
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

            // manageConnectedSocket(mmSocket); // TODO: TEST
            synchronized (CheatingAndroidService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    // TODO: do something with message
                    mHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    CheatingAndroidService.this.start();
                    break;}
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                mHandler.obtainMessage(3, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {}
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }
    }

}
