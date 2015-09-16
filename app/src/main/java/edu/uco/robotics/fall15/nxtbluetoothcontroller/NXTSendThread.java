package edu.uco.robotics.fall15.nxtbluetoothcontroller;

/**
 * Created by Justin on 9/11/15.
 */

public class NXTSendThread extends Thread {

    private NXTBluetooth NXT;
    private byte msg;

    public NXTSendThread(NXTBluetooth NXTB) {
        NXT = NXTB;
    }

    public void run(byte m) {
        msg = m;
        try {
            NXT.writeMessage(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
