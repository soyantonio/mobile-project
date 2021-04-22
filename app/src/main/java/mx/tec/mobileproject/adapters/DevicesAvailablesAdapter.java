package mx.tec.mobileproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mx.tec.mobileproject.R;

public class DevicesAvailablesAdapter extends RecyclerView.Adapter<DevicesAvailablesAdapter.DeviceItem> {
    private final Context context;
    private final List<String> devices;


    public DevicesAvailablesAdapter(Context context, List<String> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public DeviceItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bluetooth_item, parent, false);
        return new DeviceItem(view);
    }

    @Override
    public void onBindViewHolder(DeviceItem holder, int position) {
        String device = devices.get(position);
        holder.deviceName.setText(device);
        holder.deviceName.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class DeviceItem extends RecyclerView.ViewHolder {
        TextView deviceName;

        public DeviceItem(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.devices_item_connection);
        }
    }
}
