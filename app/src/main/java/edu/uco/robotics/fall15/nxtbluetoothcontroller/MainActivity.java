package edu.uco.robotics.fall15.nxtbluetoothcontroller;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnUp, btnLeft, btnStop,
                        btnRight, btnBack, connect;
    private NXTBluetooth nxt = new NXTBluetooth();
    private TextView connectionStatus;
    private NXTListenThread listenThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialize all Image Buttons
        btnUp = (ImageButton) findViewById(R.id.btnup);
        btnLeft = (ImageButton) findViewById(R.id.btnleft);
        btnRight = (ImageButton) findViewById(R.id.btnright);
        btnBack = (ImageButton) findViewById(R.id.btndown);
        btnStop = (ImageButton) findViewById(R.id.btnstop);
        connect = (ImageButton) findViewById(R.id.conn_disconn_button);

        //Initialize TextView for connection status
        connectionStatus = (TextView) findViewById(R.id.status_text);

        connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nxt.isConnected()){
                    //send disconnect message to NXT------------
                    byte msg = 99; //Literally need to send the numeric values - not in 0001 format
                    try {
                        nxt.writeMessage(msg);
                        System.out.println("Sent byte msg " + msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //-----------------------------------------

                    //disconnect NXT
                    nxt.disconnect();

                    //change status text
                    connectionStatus.setText(R.string.disconnected);

                    //update button image
                    connect.setImageResource(R.drawable.connect);
                } else {
                    connectionStatus.setText(R.string.waiting);
                    nxt.enableBluetooth();
                    if (nxt.connectToNXT()){
                        //Success!  Device is connected through Bluetooth to NXT robot
                        connectionStatus.setText(R.string.connected);
                        connect.setImageResource(R.drawable.disconnect);
                        listenThread = new NXTListenThread(nxt, mHandler);
                        listenThread.run();

                    } else {
                        //The device did not connect to the NXT robot
                        connectionStatus.setText(R.string.error_conn);
                    }
                }
            }
        });

        btnUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 19:
                    Toast.makeText(getApplicationContext(), "Received 19", Toast.LENGTH_LONG).show();
                    break;
                case 29:
                    Toast.makeText(getApplicationContext(), "Received 29", Toast.LENGTH_LONG).show();
                    break;
                case 99:
                    nxt.disconnect();
                default:
                    break;
            }
        }
    };
}
