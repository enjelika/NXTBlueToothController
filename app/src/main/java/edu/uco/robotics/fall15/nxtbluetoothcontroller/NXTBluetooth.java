package edu.uco.robotics.fall15.nxtbluetoothcontroller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class NXTBluetooth {

    //Target NXT mac address
    final String nxt = "00:16:53:15:A8:79";

    BluetoothAdapter localAdapter;
    BluetoothSocket socket_nxt;
    boolean success = false;

    //Enables Bluetooth if not enabled
    public void enableBluetooth() {
        localAdapter = BluetoothAdapter.getDefaultAdapter();
        //if Bluetooth is not enabled
        if(localAdapter.isEnabled() == false) {
            localAdapter.enable();
            while(!(localAdapter.isEnabled())) {
                System.out.println("Trying to enable BlueTooth...");
            }
        }
    }

    //Connect to NXT
    public boolean connectToNXT() {

        //get the Bluetooth device of the NXT
        BluetoothDevice nxt_device = localAdapter.getRemoteDevice(nxt);

        //try to connect to NXT
        try {
            //Bluetooth serial board well-known SPP UUID
            socket_nxt = nxt_device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

            socket_nxt.connect();

            success = true;

        } catch (IOException e) {
            Log.d("Bluetooth", "Error: Device not found or cannot connect");
            success = false;
        }
        return success;
    }

    public void writeMessage(byte msg) throws InterruptedException {
        BluetoothSocket connection_Socket = socket_nxt;

        if(connection_Socket != null) {
            try {
                OutputStreamWriter out = new OutputStreamWriter(connection_Socket.getOutputStream());
                out.write(msg);
                out.flush();

                Thread.sleep(1000);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            //Error
        }

    }

    public int readMessage(String nxt) {
        BluetoothSocket connection_Socket = socket_nxt;
        int n;

        try {
            InputStreamReader in = new InputStreamReader(connection_Socket.getInputStream());
            n = in.read();

            return n;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

}