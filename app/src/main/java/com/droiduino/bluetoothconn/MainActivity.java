package com.droiduino.bluetoothconn;

import static android.content.ContentValues.TAG;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.anastr.speedviewlib.PointerSpeedometer;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // UI Initialization
        final Button buttonConnect = findViewById(R.id.buttonConnect);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        final TextView textViewInfo = findViewById(R.id.textViewInfo);
        final ImageButton carpanel = findViewById(R.id.carpanel);
        final ImageButton carstart = findViewById(R.id.carstart);
        final ImageButton Leftblinker = findViewById(R.id.Leftblinker);
        final ImageButton rightblinker = findViewById(R.id.rightblinker);
        final ImageButton blinkeroff = findViewById(R.id.blinkeroff);
        final ImageButton Doorlockopen = findViewById(R.id.DLC);
        final ImageButton Doorlockclose = findViewById(R.id.DLO);
        final ImageButton CarAc = (ImageButton)findViewById(R.id.CarAc);
        final TextView ACtext = findViewById(R.id.textAC);
        final TextView MusicText = findViewById(R.id.musictext);
        final ImageButton CarMusic = findViewById(R.id.CarMusic);
        final TextView DTE = findViewById(R.id.DTE);
        final ImageButton windowup = findViewById(R.id.CWD);
        final ImageButton windowdown = findViewById(R.id.CWU);


        //toggle button for panel check state
        final String[] btnStateP = {"on"};
        //toggle buton for AC check state
        final String[] btnState = {"ACON"};
        //toggle button for Music player check state
        final String[] btnStateMusic = {"ONMUSIC"};


        carpanel.setEnabled(false);
        carstart.setEnabled(false);
        Leftblinker.setEnabled(false);
        rightblinker.setEnabled(false);
        blinkeroff.setEnabled(false);
        Doorlockclose.setEnabled(false);
        Doorlockopen.setEnabled(false);
        CarAc.setEnabled(false);

        // Manage gauge values
        PointerSpeedometer pointerSpeedometerguage= (PointerSpeedometer) findViewById(R.id.pointerSpeedometer);
        pointerSpeedometerguage.setUnit("%");
        pointerSpeedometerguage.setWithTremble(false);

        // Manage Temperature value
        PointerSpeedometer pointerSpeedometerTemperature= (PointerSpeedometer) findViewById(R.id.pointerSpeedometer1);
        pointerSpeedometerTemperature.setUnit("%");
        pointerSpeedometerTemperature.setWithTremble(false);




        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progress and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            progressBar.setVisibility(View.VISIBLE);
            buttonConnect.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }

        /*
        Second most important piece of Code. GUI Handler
         */
        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                carpanel.setEnabled(true);
                                carstart.setEnabled(true);
                                Leftblinker.setEnabled(true);
                                rightblinker.setEnabled(true);
                                blinkeroff.setEnabled(true);
                                Doorlockclose.setEnabled(true);
                                Doorlockopen.setEnabled(true);
                                CarAc.setEnabled(true);
                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        //read the msg that coming from Arduino read first char to check that is fuel gauge value
                        if (arduinoMsg.charAt(0)=='F'){
                            //change the position
                            //remove the f from start of the string
                            //convert the string into float and move the position of guage
                            pointerSpeedometerguage.speedTo(Float.parseFloat(arduinoMsg.substring(1, arduinoMsg.length()-1)),4000);

                        }
                        else if (arduinoMsg.charAt(0)=='T'){
                            //change the position
                            //remove the t from start of the string
                            //convert the string into float and move the position of guage
                            pointerSpeedometerTemperature.speedTo(Float.parseFloat(arduinoMsg.substring(1, arduinoMsg.length()-1)),4000);

                        }
                        else if (arduinoMsg.charAt(0)=='D'){

                            DTE.setText(arduinoMsg.substring(1, arduinoMsg.length()-1));


                        }
                        else {
                            //show msg come from arduino
                            switch (arduinoMsg.toLowerCase()) {
                                case "car panel is on":
                                case "car panel is off":
                                case "engine is started":
                                case "ignition is stopped":
                                case "right blinker is on":
                                case "left blinker is on":
                                case "blinker is off":
                                case "door is closed":
                                case "door is open":
                                case "ac on":
                                case "ac off":
                                case "car window up":
                                case "car window down":
                                    Toast.makeText(getApplicationContext(), arduinoMsg, Toast.LENGTH_LONG).show();//display the text
                                    break;

                            }
                        }
                        break;
                }
            }
        };

         //Select Bluetooth Device
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to adapter list
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });


        // ToggleButton to ON/OFF carpenal on Arduino Board
        carpanel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String cmdText = null;

                if (btnStateP[0].equals("on")){
                    btnStateP[0] = "off";
                    cmdText = "<turn on>\n";
                }else{
                    if(btnStateP[0].equals("off")){
                        btnStateP[0] = "on";
                        cmdText = "<turn off>\n";
                    }
                }
                // Send command to Arduino board

                connectedThread.write(cmdText);
            }

        });

        // ToggleButton to ON/OFF AC on Arduino Board
        CarAc.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String cmdText = null;

                if (btnState[0].equals("ACON")){
                    btnState[0] = "ACOFF";
                    ACtext.setText("ACON");
                    cmdText = "acon\n";
                }else{
                    if(btnState[0].equals("ACOFF")){
                        btnState[0] = "ACON";
                        ACtext.setText("ACOFF");
                        cmdText = "acoff\n";
                    }
                }
                // Send command to Arduino board

                connectedThread.write(cmdText);
            }

        });

        // ToggleButton to ON/OFF MUSIC on Arduino Board
        CarMusic.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String cmdText = null;

                if (btnStateMusic[0].equals("ONMUSIC")){
                    btnStateMusic[0] = "OFFMUSIC";
                    MusicText.setText("MUSICON");
                    cmdText = "mon\n";
                }else{
                    if(btnStateMusic[0].equals("OFFMUSIC")){
                        btnStateMusic[0] = "ONMUSIC";
                        MusicText.setText("MUSICOFF");
                        cmdText = "moff\n";
                    }
                }
                // Send command to Arduino board

                connectedThread.write(cmdText);
            }

        });

        // Button to use start the car and send command to Arduino Borad
        carstart.setOnTouchListener((arg0, arg1) -> {
            switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    connectedThread.write("start\n");
                    break;
                }
                case MotionEvent.ACTION_UP:{
                    connectedThread.write("stop\n");
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + arg1.getAction());
            }
            return true;
        });
        //button to use to down the door window
        windowdown.setOnTouchListener((arg0, arg1) -> {
            switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    connectedThread.write("windowdown\n");
                    break;
                }
                case MotionEvent.ACTION_UP:{
                    connectedThread.write("window\n");
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + arg1.getAction());
            }
            return true;
        });

        //button to use to up the door window
        windowup.setOnTouchListener((arg0, arg1) -> {
            switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    connectedThread.write("windowup\n");
                    break;
                }
                case MotionEvent.ACTION_UP:{
                    connectedThread.write("window\n");
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + arg1.getAction());
            }
            return true;
        });



        //button to use close door lock  on car
        Doorlockclose.setOnTouchListener((arg0, arg1) -> {
            switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    connectedThread.write("dcl\n");//send command to Arduino Borad to close the door
                    break;
                }
                case MotionEvent.ACTION_UP:{
                    connectedThread.write("door\n");//send command to Arduino Borad to close the door
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + arg1.getAction());
            }
            return true;
        });




        //button to use open door lock  on car
        Doorlockopen.setOnTouchListener((arg0, arg1) -> {
            switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    connectedThread.write("dop\n");//send command to Arduino Borad to close the door
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    connectedThread.write("door\n");//send command to Arduino Borad to close the door
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + arg1.getAction());
            }
            return true;
        });


        //button to use on right blinker  on car
        rightblinker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connectedThread.write("ron\n");//send command to Arduino Borad to on right blinker
            }
        });

        // button to use on Left blinker on car
        Leftblinker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connectedThread.write("lon\n");//send command to Arduino Borad to on left blinker
            }
        });

        // button to use off both blinker on car
        blinkeroff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connectedThread.write("bof\n");//send command to Arduino Borad to on left blinker
            }
        });
    }

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {

                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }


        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException ignored) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
