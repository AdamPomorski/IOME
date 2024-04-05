package com.example.iome.device_select;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iome.R;

import java.util.ArrayList;

public class BLDeviceListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private Context context;

    public BLDeviceListAdapter( Context context) {
        super();
        this.context = context;
        bluetoothDevices = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void addDevice(BluetoothDevice device) {
        if (!bluetoothDevices.contains(device)) {
            bluetoothDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return bluetoothDevices.get(position);
    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return bluetoothDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.item_device_list, null);
            viewHolder = new ViewHolder();
            viewHolder.unchecked = convertView.findViewById(R.id.unchecked);
            viewHolder.checked = convertView.findViewById(R.id.checked);
            viewHolder.deviceName = convertView.findViewById(R.id.device_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BluetoothDevice device = bluetoothDevices.get(position);
        String deviceName = device.getName();
        String deviceAddress = device.getAddress();
        viewHolder.checked.setVisibility(View.GONE);
        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName+" - "+deviceAddress);
        } else {
            viewHolder.deviceName.setText("Unknown device"+deviceAddress);
        }
        return convertView;
    }
    static class ViewHolder {
        ImageView unchecked;
        ImageView checked;
        TextView deviceName;
    }

}
