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
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnUp, btnLeft, btnStop,
                        btnRight, btnBack, connect;
    private TextView txtState;

    private ToggleButton toggle;

    public static final int SOUND_HELLO = 1;
    public static final int SOUND_BYE = 2;
    public static final int SOUND_OBJECT = 3;
    public static final int SOUND_CARRY_ON = 4;
    public static final int SOUND_DO_WHAT = 5;
    public static final int SOUND_PLACE_HUGE = 6;
    public static final int SOUND_SHOOT_FOR_STARS = 7;
    public static final int SOUND_THIS_WAY = 8;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;

    private String nxt;
    private final String nxt1 = "00:16:53:15:A8:79"; //Debra's NXT robot
    private final String nxt3 = "00:16:53:0D:74:10"; //Stan's NXT robot

    public static final int FORWARD = -1;
    public static final int BACKWARD = -2;
    public static final int STOPPED = -3;

    int CURRENT_STATE = -3;

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
        nxt = "00:16:53:15:A8:79";
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

        txtState = (TextView) findViewById(R.id.btnAction);
        txtState.setText("Stopped");
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
        if(!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            while(!(mBluetoothAdapter.isEnabled())) {
                System.out.println("Trying to enable BlueTooth...");
            }
        }

        /**
         * Disable control buttons
         */
        ButtonEnableState(false);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
                if (mNXTService == null || mNXTService.getState() == 0) {
                    setupNXTBluetoothService();
                    connectDevice();
                    connect.setImageResource(R.drawable.disconnect);
                    ButtonEnableState(true);
                    toggle.setEnabled(false);
                } else if (mNXTService.getState() == 3) {
                    byte message = 99;
                    sendMessage(message);
                    mNXTService.stop();
                    ButtonEnableState(false);
                    setupNXTBluetoothService();
                    connect.setImageResource(R.drawable.connect);
                    toggle.setEnabled(true);
                }
            }
        });

        btnUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bED("up");
                byte message = 19;
                sendMessage(message);
                CURRENT_STATE = -1;
                txtState.setText("Forward");
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bED("back");
                byte message = 29;
                sendMessage(message);
                CURRENT_STATE = -2;
                txtState.setText("Backward");
            }
        });

        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bED("stop");
                byte message = 59;
                sendMessage(message);
                CURRENT_STATE = -3;
                txtState.setText("Stoppted");
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //disable turn right button and set disable image
                    btnRight.setEnabled(false);
                    //show button pressed image
                    btnLeft.setImageResource(R.drawable.left_disabled);
                    //send commands based on state
                    byte message;
                    switch (CURRENT_STATE) {
                        case FORWARD:
                            message = 39;
                            sendMessage(message);
                            break;
                        case STOPPED:
                            message = 38;
                            sendMessage(message);
                            break;
                        case BACKWARD:
                            message = 37;
                            sendMessage(message);
                            break;
                        default:

                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //endable turn right and change image
                    btnRight.setEnabled(true);
                    //show button not pressed image
                    btnLeft.setImageResource(R.drawable.left);
                    //send commands to resume non-turning state
                    byte message;
                    switch (CURRENT_STATE) {
                        case FORWARD:
                            message = 19;
                            sendMessage(message);
                            break;
                        case STOPPED:
                            message = 59;
                            sendMessage(message);
                            break;
                        case BACKWARD:
                            message = 29;
                            sendMessage(message);
                            break;
                        default:

                    }
                }
                return false;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //disable turn left and change image
                    btnLeft.setEnabled(false);
                    //show button pressed image
                    btnRight.setImageResource(R.drawable.right_disabled);
                    //send commands based on state
                    byte message;
                    switch (CURRENT_STATE){
                        case FORWARD:
                            message = 49;
                            sendMessage(message);
                            break;
                        case STOPPED:
                            message = 48;
                            sendMessage(message);
                            break;
                        case BACKWARD:
                            message = 47;
                            sendMessage(message);
                            break;
                        default:

                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    //enable turn left and change image
                    btnLeft.setEnabled(true);
                    //show button not pressed image
                    btnRight.setImageResource(R.drawable.right);
                    //send commands to resume non-turning state
                    byte message;
                    switch (CURRENT_STATE){
                        case FORWARD:
                            message = 19;
                            sendMessage(message);
                            break;
                        case STOPPED:
                            message = 59;
                            sendMessage(message);
                            break;
                        case BACKWARD:
                            message = 29;
                            sendMessage(message);
                            break;
                        default:

                    }
                }
                return false;
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
        mNXTService = new NXTBluetoothService(mHandler);
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

    /**
     * Reverses the enabled state of all the controller buttons
     */
    private void ButtonEnableState(boolean set){
        btnUp.setEnabled(set);
        btnRight.setEnabled(set);
        btnLeft.setEnabled(set);
        btnBack.setEnabled(set);
        btnStop.setEnabled(set);

        if (set == false){
            btnUp.setImageResource(R.drawable.up_dis);
            btnRight.setImageResource(R.drawable.right_dis);
            btnLeft.setImageResource(R.drawable.left_dis);
            btnBack.setImageResource(R.drawable.down_dis);
            btnStop.setImageResource(R.drawable.stop_dis);
        } else {
            btnUp.setImageResource(R.drawable.up);
            btnRight.setImageResource(R.drawable.right);
            btnLeft.setImageResource(R.drawable.left);
            btnBack.setImageResource(R.drawable.down);
            btnStop.setImageResource(R.drawable.stop);
        }
    }

    /**
     * Disables last button pressed and enables previous pressed
     */
    private void bED(String button){
        switch(button){
            case "up":
                btnUp.setEnabled(false);
                btnUp.setImageResource(R.drawable.up_disabled);
                btnLeft.setEnabled(true);
                btnRight.setEnabled(true);
                btnBack.setEnabled(true);
                btnRight.setImageResource(R.drawable.right);
                btnLeft.setImageResource(R.drawable.left);
                btnBack.setImageResource(R.drawable.down);
                break;
            case "back":
                btnBack.setEnabled(false);
                btnBack.setImageResource(R.drawable.down_disabled);
                btnUp.setEnabled(true);
                btnRight.setEnabled(true);
                btnLeft.setEnabled(true);
                btnRight.setImageResource(R.drawable.right);
                btnUp.setImageResource(R.drawable.up);
                btnLeft.setImageResource(R.drawable.left);
                break;
            case "stop":
                btnUp.setEnabled(true);
                btnBack.setEnabled(true);
//                btnRight.setEnabled(true);
//                btnLeft.setEnabled(true);
//                btnRight.setImageResource(R.drawable.right);
                btnUp.setImageResource(R.drawable.up);
//                btnLeft.setImageResource(R.drawable.left);
                btnBack.setImageResource(R.drawable.down);
                break;
        }
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
                        case 68:
                            careful();
                            ButtonEnableState(false);
                            CURRENT_STATE = STOPPED;
                            txtState.setText("Stopped");
                            break;
                        case 69:
                            carryOn();
                            ButtonEnableState(true);
                            //Toast.makeText(MainActivity.this, "Debra has dirty mind for choosing " + Integer.toString(message), Toast.LENGTH_LONG).show();
                            break;
                        default:
                            //Toast.makeText(MainActivity.this, "Message received int = " + Integer.toString(message), Toast.LENGTH_LONG).show();
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
                    }else if(msg.getData().getString(Constants.TOAST) == "Device connection was lost"){
                        mNXTService.stop();
                        fairwell();
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
        soundPoolMap.put(SOUND_OBJECT, soundPool.load(this, R.raw.wheatley_careful, 1));
        soundPoolMap.put(SOUND_CARRY_ON, soundPool.load(this, R.raw.wheatley_carry_on, 1));
        soundPoolMap.put(SOUND_DO_WHAT, soundPool.load(this, R.raw.wheatley_and_do_what, 1));
        soundPoolMap.put(SOUND_PLACE_HUGE, soundPool.load(this, R.raw.wheatley_place_huge, 1));
        soundPoolMap.put(SOUND_SHOOT_FOR_STARS, soundPool.load(this, R.raw.wheatley_shoot_for_stars, 1));
        soundPoolMap.put(SOUND_THIS_WAY, soundPool.load(this, R.raw.wheatley_try_this_way, 1));
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

    public void careful() { playSound(SOUND_OBJECT); }

    public void carryOn() { playSound(SOUND_CARRY_ON); }
}