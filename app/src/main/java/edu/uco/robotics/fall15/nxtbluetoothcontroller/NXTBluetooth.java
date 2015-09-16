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
    final String nxt = "00:16:53:15:A8:79"; //NXT
    //final String nxt = "00:16:53:0D:74:10";  //NXT3

    private BluetoothAdapter localAdapter;
    private BluetoothSocket socket_nxt;
    private boolean connected = false;

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

            connected = true;

        } catch (IOException e) {
            Log.d("Bluetooth", "Error: Device not found or cannot connect");
            connected = false;
        }
        //stopping discovery may speed connection
        localAdapter.cancelDiscovery();
        return connected;
    }

    public boolean isConnected(){
        return connected;
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

    public int readMessage() {
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

    public void disconnect() {

        try{
            if(connected){
                byte msg = 0x30;
                try {
                    writeMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                socket_nxt.close();
                localAdapter = null;
                socket_nxt = null;
                connected = false;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

    }
}