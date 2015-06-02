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
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Simon on 18.05.2015.
 */
public class CheatingAndroidService extends Application {

    private static CheatingAndroidService singleton;
    private static final String NAME = "CheatingAndroidBluetooth";
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private static final UUID[] mUUIDs = {
            UUID.fromString("a0ae0db8-fb75-416d-a436-83ef5415c537"),
            UUID.fromString("b992a855-b913-4a22-80db-b6698bff46f0"),
            UUID.fromString("5df33d47-986c-4a35-a349-3878e2d474cd"),
            UUID.fromString("60e5e24e-789c-4b88-8911-a3f591b5f8d5"),};

    private ArrayList<String> mDeviceAddresses = new ArrayList<String>();
    private ArrayList<ConnectedThread> mConnectedThreads = new ArrayList<ConnectedThread>();
    private ArrayList<BluetoothSocket> mSockets= new ArrayList<BluetoothSocket>();

    private String lastConnectedDevice;

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

        for (int i = 0; i < mConnectedThreads.size(); i++) {
            try {
                ConnectedThread r;
                synchronized (this) {
                    r = mConnectedThreads.get(i);
                }
                r.write(out);
            } catch (Exception e) {}
        }
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

        // Start the thread to connect with the given device trying each UUID one-by-one

        for (int i = 0; i < 4; i++) {
            try {
                mConnectThread = new ConnectThread(device, mUUIDs[i]);
                mConnectThread.start();
            } catch (Exception e) {}
        }
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        mConnectedThreads.add(mConnectedThread);

        lastConnectedDevice = device.getName() + "\n" + device.getAddress();
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, lastConnectedDevice);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket mmServerSocket = null;

        public AcceptThread() {
            BluetoothServerSocket tmp1 = null;
            try {
                tmp1 = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, mUUIDs[3]);

            } catch (IOException e) {}
            mmServerSocket = tmp1;
        }

        public void run() {

            setName("AcceptThread");

//            BluetoothSocket socket = null;
//            try {
//                for (int i = 0; i < 4; i++) {
//                    mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, mUUIDs.get(i));
//                    socket = mmServerSocket.accept();
//                    if (socket != null) {
//                        String address = socket.getRemoteDevice().getAddress();
//                        mSockets.add(socket);
//                        mDeviceAddresses.add(address);
//                        connected(socket, socket.getRemoteDevice());
//                    }
//                }
//
//            } catch (Exception e) {}

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

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            UUID tmpUUID = uuid;

            try {
                tmp = device.createRfcommSocketToServiceRecord(tmpUUID);
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
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    CheatingAndroidService.this.start();
                    break;}
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {}
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }
    }

}
