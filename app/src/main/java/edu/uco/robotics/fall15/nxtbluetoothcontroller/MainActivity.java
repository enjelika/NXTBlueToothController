package edu.uco.robotics.fall15.nxtbluetoothcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnUp, btnLeft, btnStop,
                        btnRight, btnBack, connect;

    private ToggleButton toggle;

    public static final int SOUND_HELLO = 1;
    public static final int SOUND_BYE = 2;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;

    private String nxt;
    private final String nxt1 = "00:16:53:15:A8:79"; //Debra's NXT robot
    private final String nxt3 = "00:16:53:0D:74:10"; //Stan's NXT robot

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
        nxt = "00:16:53:0D:74:10";
        initSounds();

        /**
         * Initialize all Image Buttons
         */
        btnUp = (ImageButton) findViewById(R.id.btnup);
        btnLeft = (ImageButton) findViewById(R.id.btnleft);
        btnRight = (ImageButton) findViewById(R.id.btnright);
        btnBack = (ImageButton) findViewById(R.id.btndown);
        btnStop = (ImageButton) findViewById(R.id.btnstop);
        connect = (ImageButton) findViewById(R.id.conn_disconn_button);

        /**
         * Initialize Toggle Button
         */
        toggle = (ToggleButton) findViewById(R.id.nxt_toggle);

        /**
         * Get local Bluetooth adapter
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /**
         * If Bluetooth is not enabled
         */
        if(mBluetoothAdapter.isEnabled() == false) {
            mBluetoothAdapter.enable();
            while(!(mBluetoothAdapter.isEnabled())) {
                System.out.println("Trying to enable BlueTooth...");
            }
        }

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    /**
                     * NXT 1 is selected
                     */
                    nxt = nxt1;
                } else {
                    /**
                     * NXT 3 is selected
                     */
                    nxt = nxt3;
                }
            }
        });

        connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNXTService == null || mNXTService.getState() == 0){
                    setupNXTBluetoothService();
                    connectDevice();
                    connect.setImageResource(R.drawable.disconnect);
                    toggle.setEnabled(false);
                }else if(mNXTService.getState() == 3){
                    byte message = 99;
                    sendMessage(message);
                    mNXTService.stop();
                    fairwell();
                    setupNXTBluetoothService();
                    connect.setImageResource(R.drawable.connect);
                    toggle.setEnabled(true);
                }
            }
        });

        btnUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                byte message = 19;
                sendMessage(message);
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                byte message = 29;
                sendMessage(message);
            }
        });

        btnLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                byte message = 39;
                sendMessage(message);
            }
        });

        btnRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                byte message = 49;
                sendMessage(message);
            }
        });

        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                byte message = 59;
                sendMessage(message);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         * Inflate the menu; this adds items to the action bar if it is present.
         */
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();

        /**
         * no inspection SimplifiableIfStatement
         */
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup connection and service
     */
    private void setupNXTBluetoothService() {
        /**
         * Initialize the BluetoothChatService to perform bluetooth connection
         */
        mNXTService = new NXTBluetoothService(MainActivity.this, mHandler);
    }

    /**
     * Establish connection with NXT robot
     */
    private void connectDevice() {
        /**
         * Get the BluetoothDevice object
         */
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(nxt);

        /**
         * Attempt to connect to the device
         */
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
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case NXTBluetoothService.STATE_CONNECTED:
                            setTitle("STATUS: Connected");
                            greeting();
                            break;
                        case NXTBluetoothService.STATE_CONNECTING:
                            setTitle("STATUS: Waiting...");
                            break;
                        case NXTBluetoothService.STATE_LISTEN:
                            break;
                        case NXTBluetoothService.STATE_NONE:
                            setTitle("STATUS: Disconnected");
                            break;
                    }
                    break;

                case Constants.MESSAGE_WRITE:
                    break;

                case Constants.MESSAGE_READ:
                    /**
                     * Gets int value of message from NXT that is passed from NXTBluetoothService
                     */
                    int message = msg.arg1;
                    /**
                     * TODO: add cases for each message to perform tasks
                     */
                    switch(message){
                        case 69:
                            Toast.makeText(MainActivity.this, "Debra has dirty mind for choosing " + Integer.toString(message), Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, "Message received int = " + Integer.toString(message), Toast.LENGTH_LONG).show();
                    }
                    break;

                case Constants.MESSAGE_DEVICE_NAME:
                    Toast.makeText(MainActivity.this, "Connected to NXT", Toast.LENGTH_SHORT).show();
                    break;

                case Constants.MESSAGE_TOAST:
                    if(msg.getData().getString(Constants.TOAST) == "Unable to connect device"){
                        setupNXTBluetoothService();
                        connect.setImageResource(R.drawable.connect);
                        toggle.setEnabled(true);
                    }
                    Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

	private void initSounds() {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<>();
		soundPoolMap.put(SOUND_HELLO, soundPool.load(this, R.raw.wheatley_hello, 1));
        soundPoolMap.put(SOUND_BYE, soundPool.load(this, R.raw.wheatley_bye, 1));
	}

	public void playSound(int sound) {
		// Updated: The next 4 lines calculate the current volume in a scale of 0.0 to 1.0
		AudioManager mgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;

		// Play the sound with the correct volume
		soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
	}

	public void greeting() {
        playSound(SOUND_HELLO);
	}

    public void fairwell() { playSound(SOUND_BYE); }
}
