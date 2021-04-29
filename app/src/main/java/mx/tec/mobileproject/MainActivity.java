package mx.tec.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    private static final String TAG = "main.activity";

    /**
            * the MAC address for the chosen device
     */
    private String address;
    private ProgressDialog progressDialog;
    private BluetoothAdapter myBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it'
    //This the SPP for the arduino(AVR)
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private int newConnectionFlag = 0;

    public static Intent createIntent(Context context, String deviceName) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_DEVICE, deviceName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.statusTextView)).setText(getString(R.string.connection_status));
        Log.d(TAG, "device: " + getIntent().getStringExtra(EXTRA_DEVICE));
        initTimer();
        initBluetooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        newConnectionFlag++;
        if (address != null) {
            //call the class to connect to bluetooth
            if (newConnectionFlag == 1) {
                new connectBT().execute();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
    }

    private void initTimer() {
        // minutes
        int time = 1;
        new CountDownTimer(time*60000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Log.d(TAG,"Done!");
            }
        }.start();
    }

    private void initBluetooth() {
        Log.d(TAG, "initBluetooth");
        int REQUEST_ENABLE_BT = 1;
        //get the MAC address from the Bluetooth Devices Activity
        Intent newIntent = getIntent();
        address = newIntent.getStringExtra(EXTRA_DEVICE).split("&")[1];
        Log.d(TAG, "address: " + address);

        //check if the device has a bluetooth or not
        //and show Toast message if it does't have
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableIntentBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntentBluetooth, REQUEST_ENABLE_BT);
        }

        // f, b, l, r <- Bluetooth Commands Capital
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Log.d(TAG, "Back");
            sendData("B");
        });

        findViewById(R.id.frontButton).setOnClickListener(v -> {
            Log.d(TAG, "Front");
            sendData("F");
        });

        findViewById(R.id.leftButton).setOnClickListener(v -> {
            Log.d(TAG, "Left");
            sendData("L");
        });

        findViewById(R.id.rightButton).setOnClickListener(v -> {
            Log.d(TAG, "Right");
            sendData("R");
        });
    }

    /**
            * used to send data to the micro controller
     *
             * @param data the data that will send prefer to be one char
     */
    private void sendData(String data) {
        Log.d(TAG, "data to be sent: " + data);
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(data.getBytes());
            } catch (IOException e) {
                makeToast("Error");
            }
        }
    }

    /**
     * An AysncTask to connect to Bluetooth socket
     */
    private class connectBT extends AsyncTask<Void, Void, Void> {
        private boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {

            //show a progress dialog
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Connecting...", "Please wait!!!");
        }

        //while the progress dialog is shown, the connection is done in background
        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (btSocket == null || !isBtConnected) {
                    //get the mobile bluetooth device
                    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    //connects to the device's address and checks if it's available
                    BluetoothDevice bluetoothDevice = myBluetoothAdapter.getRemoteDevice(address);

                    //create a RFCOMM (SPP) connection
                    btSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    //start connection
                    btSocket.connect();
                }

            } catch (IOException e) {
                //if the try failed, you can check the exception here
                connectSuccess = false;
            }

            return null;
        }

        //after the doInBackground, it checks if everything went fine
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e(TAG, connectSuccess + "");
            if (!connectSuccess) {
                makeToast("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                isBtConnected = true;
                makeToast("Connected");
            }
            progressDialog.dismiss();
        }
    }

    /**
     * fast way to call Toast
     */
    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * to disconnect the bluetooth connection
     */
    private void disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                makeToast("Error");
            }
        }
    }
}