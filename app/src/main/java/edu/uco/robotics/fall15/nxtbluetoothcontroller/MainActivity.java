package edu.uco.robotics.fall15.nxtbluetoothcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnUp, btnLeft, btnStop,
                        btnRight, btnBack, connect;
    //private NXTBluetooth nxt = new NXTBluetooth();
    //private NXTListenThread listenThread;

    // Intent request codes
//    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
//    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
//    private static final int REQUEST_ENABLE_BT = 3;

    private final String nxt = "00:16:53:15:A8:79"; //NXT

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the NXT Bluetooth services
     */
    private NXTBluetoothService mNXTService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("STATUS: Not Connected");

        //Initialize all Image Buttons
        btnUp = (ImageButton) findViewById(R.id.btnup);
        btnLeft = (ImageButton) findViewById(R.id.btnleft);
        btnRight = (ImageButton) findViewById(R.id.btnright);
        btnBack = (ImageButton) findViewById(R.id.btndown);
        btnStop = (ImageButton) findViewById(R.id.btnstop);
        connect = (ImageButton) findViewById(R.id.conn_disconn_button);

        //Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //if Bluetooth is not enabled
        if(mBluetoothAdapter.isEnabled() == false) {
            mBluetoothAdapter.enable();
            while(!(mBluetoothAdapter.isEnabled())) {
                System.out.println("Trying to enable BlueTooth...");
            }
        }

        connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNXTService == null || mNXTService.getState() == 0){
                    setupNXTBluetoothService();
                    connectDevice();
                    connect.setImageResource(R.drawable.disconnect);
                }else if(mNXTService.getState() == 3){
                    byte message = 99;
                    sendMessage(message);
                    mNXTService.stop();
                    connect.setImageResource(R.drawable.connect);
                }
            }
        });

        btnUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                byte message = 19;
//                sendMessage(message);
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                byte message = 29;
//                sendMessage(message);
            }
        });

        btnLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                byte message = 39;
//                sendMessage(message);
            }
        });

        btnRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                byte message = 49;
//                sendMessage(message);
            }
        });

        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                byte message = 59;
//                sendMessage(message);
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

    /** Setup connection and service*/
    private void setupNXTBluetoothService() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mNXTService = new NXTBluetoothService(MainActivity.this, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

//    /**
//     * Establish connection with other device
//     *
//     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
//     * @param secure Socket Security type - Secure (true) , Insecure (false)
//     */
    private void connectDevice() {
        // Get the device MAC address
//        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(nxt);
        // Attempt to connect to the device
        mNXTService.connect(device);
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(byte message) {
        // Check that we're actually connected before trying anything
        if (mNXTService.getState() != NXTBluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        mNXTService.write(message);
        // Check that there's actually something to send
//        if (message.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//            byte[] send = message.getBytes();
//            mNXTService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            //mOutEditText.setText(mOutStringBuffer);
//        }
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case NXTBluetoothService.STATE_CONNECTED:
//                            mConversationArrayAdapter.clear();
                              setTitle("STATUS: Connected");
                            break;
                        case NXTBluetoothService.STATE_CONNECTING:
                            setTitle("STATUS: Waiting...");
                            break;
                        case NXTBluetoothService.STATE_LISTEN:
                        case NXTBluetoothService.STATE_NONE:
                            setTitle("STATUS: Disconnected");
                            break;
                    }
                    break;

                case Constants.MESSAGE_WRITE:
                    break;

                case Constants.MESSAGE_READ:
                    /**Gets int value of message from NXT that is passed from NXTBluetoothService*/
                    int message = msg.arg1;
                    /**TODO: add cases for each message to perform tasks*/
                    switch(message){

                    }
                    break;

                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != activity) {
//                        Toast.makeText(activity, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
                    Toast.makeText(MainActivity.this, "Connected to NXT", Toast.LENGTH_SHORT).show();
                    break;

                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
                    if(msg.getData().getString(Constants.TOAST) == "Unable to connect device"){
                        //CharSequence st = "STATUS: Disconnected";
                        //setStatus(st);
                        setupNXTBluetoothService();
                        connect.setImageResource(R.drawable.connect);
                    }
                    Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
//            switch (msg.arg1) {
//                case 19:
//                    Toast.makeText(getApplicationContext(), "Received 19", Toast.LENGTH_LONG).show();
//                    break;
//                case 29:
//                    Toast.makeText(getApplicationContext(), "Received 29", Toast.LENGTH_LONG).show();
//                    break;
//                case 99:
//                    nxt.disconnect();
//                default:
//                    break;
//            }
        }
    };

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CONNECT_DEVICE_SECURE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, true);
//                }
//                break;
//            case REQUEST_CONNECT_DEVICE_INSECURE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data, false);
//                }
//                break;
//            case REQUEST_ENABLE_BT:
//                // When the request to enable Bluetooth returns
//                if (resultCode == Activity.RESULT_OK) {
//                    // Bluetooth is now enabled, so set up a chat session
//                    setupConnection();
//                } else {
//                    // User did not enable Bluetooth or an error occurred
//                    Log.d(TAG, "BT not enabled");
//                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
//                            Toast.LENGTH_SHORT).show();
//                    getActivity().finish();
//                }
//        }
//    }
}
