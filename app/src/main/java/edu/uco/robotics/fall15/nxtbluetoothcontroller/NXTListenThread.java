package edu.uco.robotics.fall15.nxtbluetoothcontroller;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

/**
 * Created by Justin on 9/14/15.
 */
public class NXTListenThread extends Thread {
    private NXTBluetooth NXT;
    private final Handler mHandler;
    private int mState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private NXTListenThread(NXTBluetooth NXTB, Handler handler){
        NXT = NXTB;
        mHandler = handler;
        if (NXT.isConnected() == true){
            mState = STATE_CONNECTED;
        } else {
            mState = STATE_NONE;
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while(true) {
            bytes = NXT.readMessage();
            mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
        }
    }
}
