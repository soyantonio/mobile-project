package mx.tec.mobileproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelUuid;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.util.Set;
import java.util.UUID;

import mx.tec.mobileproject.preferences.Preferences;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    private static final String TAG = "main.activity";

    private final Preferences preferences = new Preferences(this);
    private LocationManager locationManager;
    private final String provider = LocationManager.GPS_PROVIDER;

    /**
     * the MAC address for the chosen device
     */
    private TextView timeConnected;
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
        ((TextView) findViewById(R.id.statusTextView)).setText(getString(R.string.connection_status_off));
        Log.d(TAG, "device: " + getIntent().getStringExtra(EXTRA_DEVICE));
        timeConnected = findViewById(R.id.timeTextView);
        timeConnected.setText(getString(R.string.car_timer, "00", "00"));
        preferences.setTimeConnected(0);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initBluetooth();
        checkLocationPermission();
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendData("C");
        disconnect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("R.string.title_location_permission")
                        .setMessage("R.string.text_location_permission")
                        .setPositiveButton("R.string.ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000,
                1, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {

                    }
                });
    }

    private void initTimer() {
        // minutes
        int time = 1;
        new CountDownTimer(time*60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long timerSeconds = 60 - (millisUntilFinished / 1000);
                Log.d(TAG,"seconds: " + timerSeconds);
                int seconds = (int) timerSeconds;
                int minutes = preferences.getTimeConnected() / 60;
                preferences.setTimeConnected(minutes*60 + seconds);
                timeConnected.setText(getString(R.string.car_timer, String.valueOf(minutes), String.valueOf(seconds)));
            }

            public void onFinish() {
                Log.d(TAG,"Done!");
                initTimer();
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        ((TextView) findViewById(R.id.location_text)).setText(Html.fromHtml(getString(R.string.car_location, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()))));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

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
                ((TextView) findViewById(R.id.statusTextView)).setText(getString(R.string.connection_status_on));
                initTimer();
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