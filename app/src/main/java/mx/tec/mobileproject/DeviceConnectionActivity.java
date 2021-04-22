package mx.tec.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mx.tec.mobileproject.dialogs.DevicesAvailablesDialog;

public class DeviceConnectionActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private DevicesAvailablesDialog devicesAvailablesDialog;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String logger = "connection.activity";
        setContentView(R.layout.activity_device_connection);
        int REQUEST_ENABLE_BT = 1;
        Context context = this;

        ((Button) findViewById(R.id.searchButton)).setOnClickListener(v -> {
            if (bluetoothAdapter == null) {
                String error = "The device do not support bluetooth";
                Log.d(logger, error);
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }


            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                List<String> devices = new ArrayList<>();
                List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d(logger, deviceName);
                    Log.d(logger, deviceHardwareAddress);

                    bluetoothDevices.add(device);
                    devices.add(deviceName + " " + deviceHardwareAddress);
                }
                if (!devices.isEmpty()) {
                    devicesAvailablesDialog = new DevicesAvailablesDialog(context, devices, deviceName -> {
                        Intent intent = MainActivity.createIntent(context, deviceName);
                        startActivity(intent);
                    });
                    devicesAvailablesDialog.show();
                }
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (devicesAvailablesDialog != null && devicesAvailablesDialog.isShowing()) {
            devicesAvailablesDialog.dismiss();
        }
    }
}