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
    private final DeviceDialogInterface deviceDialogInterface;
    private DevicesAvailablesAdapter devicesAvailablesAdapter;
    
    public interface DeviceDialogInterface {
        void clickDevice(String deviceName);
    }

    public DevicesAvailablesDialog(Context context, List<String> devices, DeviceDialogInterface deviceDialogInterface) {
        super(context);
        this.context = context;
        this.devices = devices;
        this.deviceDialogInterface = deviceDialogInterface;
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
            devicesAvailablesAdapter = new DevicesAvailablesAdapter(context, devices, deviceDialogInterface);
        }
        RecyclerView recyclerView = findViewById(R.id.devices_availables_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(devicesAvailablesAdapter);
    }
}
