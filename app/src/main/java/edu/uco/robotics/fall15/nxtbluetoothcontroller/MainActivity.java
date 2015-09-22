package edu.uco.robotics.fall15.nxtbluetoothcontroller;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
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
    //private NXTBluetooth nxt = new NXTBluetooth();
    private TextView connectionStatus;
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

        //Initialize all Image Buttons
        btnUp = (ImageButton) findViewById(R.id.btnup);
        btnLeft = (ImageButton) findViewById(R.id.btnleft);
        btnRight = (ImageButton) findViewById(R.id.btnright);
        btnBack = (ImageButton) findViewById(R.id.btndown);
        btnStop = (ImageButton) findViewById(R.id.btnstop);
        connect = (ImageButton) findViewById(R.id.conn_disconn_button);

        //Initialize TextView for connection status
        connectionStatus = (TextView) findViewById(R.id.status_text);

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
                if(mNXTService == null){
                    setupNXTBluetoothService();
                    connectDevice();
                    CharSequence st = "STATUS: Connected";
                    setStatus(st);
                    connect.setImageResource(R.drawable.disconnect);
                }else if(mNXTService.getState() == 3){
                    byte message = 99;
                    sendMessage(message);
                    mNXTService.stop();
                    CharSequence st = "STATUS: Disconnected";
                    setStatus(st);
                    connect.setImageResource(R.drawable.connect);
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



    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
//    private void setStatus(int resId) {
//        FragmentActivity activity = getActivity();
//        if (null == activity) {
//            return;
//        }
//        final ActionBar actionBar = activity.getActionBar();
//        if (null == actionBar) {
//            return;
//        }
//        actionBar.setSubtitle(resId);
//    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setTitle(subTitle);
    }



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case NXTBluetoothService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
                              connectionStatus.setText(R.string.connected);
                            break;
                        case NXTBluetoothService.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
                            connectionStatus.setText(R.string.waiting);
                            break;
                        case NXTBluetoothService.STATE_LISTEN:
                        case NXTBluetoothService.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
                            connectionStatus.setText(R.string.disconnected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    /**Not needed*/
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    /** TODO: code to display message on android app screen here*/
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
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
