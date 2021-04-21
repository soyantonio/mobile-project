package mx.tec.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.statusTextView)).setText("Estado: Carrito Desconectado :(");
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