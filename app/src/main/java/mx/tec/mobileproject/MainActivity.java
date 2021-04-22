package mx.tec.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String EXTRA_DEVICE = "EXTRA_DEVICE";

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
        String logger = "main.activity";

        ((Button) findViewById(R.id.backButton)).setOnClickListener(v -> {
            Log.d(logger, "Back");
        });

        ((Button) findViewById(R.id.frontButton)).setOnClickListener(v -> {
            Log.d(logger, "Front");
        });

        ((Button) findViewById(R.id.leftButton)).setOnClickListener(v -> {
            Log.d(logger, "Left");
        });


        ((Button) findViewById(R.id.rightButton)).setOnClickListener(v -> {
            Log.d(logger, "Right");
        });



        // f, b, l, r <- Bluetooth Commands

    }
}