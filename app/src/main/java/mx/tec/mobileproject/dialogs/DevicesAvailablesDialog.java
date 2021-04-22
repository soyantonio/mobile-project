package mx.tec.mobileproject.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mx.tec.mobileproject.R;
import mx.tec.mobileproject.adapters.DevicesAvailablesAdapter;

public class DevicesAvailablesDialog  extends Dialog {
    private final Context context;
    private final List<String> devices;
    private DevicesAvailablesAdapter devicesAvailablesAdapter;

    public DevicesAvailablesDialog(Context context, List<String> devices) {
        super(context);
        this.context = context;
        this.devices = devices;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.devices_availables_dialog);
        initAdapters();
    }

    private void initAdapters() {
        if (devicesAvailablesAdapter == null) {
            devicesAvailablesAdapter = new DevicesAvailablesAdapter(context, devices);
        }
        RecyclerView recyclerView = findViewById(R.id.devices_availables_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(devicesAvailablesAdapter);
    }
}
