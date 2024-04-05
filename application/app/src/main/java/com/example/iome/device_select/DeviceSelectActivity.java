package com.example.iome.device_select;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.iome.R;
import com.example.iome.services.HeadbandService;
import com.example.iome.services.SpotifyService;
import com.example.iome.ui.menu.HomeActivity;
import com.neurosky.connection.TgStreamReader;

import java.util.Set;

public class DeviceSelectActivity extends AppCompatActivity {
    private static final String TAG = DeviceSelectActivity.class.getSimpleName();
    private TgStreamReader tgStreamReader;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private Button selectDeviceButton;
    private static final int REQUEST_BLUETOOTH_SCAN = 1;
    private String address = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_connect_device);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent spotifyServiceStartIntent = new Intent(this, SpotifyService.class);
        startService(spotifyServiceStartIntent);
        Intent headbandServiceStartIntent = new Intent(this, HeadbandService.class);
        startService(headbandServiceStartIntent);
        try {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error:" + e.getMessage());
            return;
        }
        selectDeviceButton = findViewById(R.id.btn_select_device);
        selectDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    scanDevices();
            }
        });

    }




    private ListView devicesList;
    private BLDeviceListAdapter deviceListAdapter = null;
    private Dialog selectDialog;


    @RequiresApi(api = Build.VERSION_CODES.S)
    private void scanDevices() {

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        createDeviceListView();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();

    }

    private void createDeviceListView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_select_device, null);
        devicesList = (ListView) view.findViewById(R.id.list_select);
        selectDialog = new Dialog(this, R.style.dialog1);
        selectDialog.setContentView(view);
        deviceListAdapter = new BLDeviceListAdapter(this);
        devicesList.setAdapter(deviceListAdapter);
        devicesList.setOnItemClickListener(selectDeviceItemClickListener);
        selectDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {

                Log.e(TAG, "onCancel called!");
                DeviceSelectActivity.this.unregisterReceiver(receiver);
            }

        });

        selectDialog.show();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() == 0){
            Toast.makeText(this, "No paired device found, please enable Bluetooth connection", Toast.LENGTH_LONG).show();
            return;
        }
        for (BluetoothDevice device : pairedDevices) {
            deviceListAdapter.addDevice(device);
        }
        deviceListAdapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener selectDeviceItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            DeviceSelectActivity.this.unregisterReceiver(receiver);

            bluetoothDevice = deviceListAdapter.getDevice(position);
            selectDialog.dismiss();
            selectDialog = null;
            Log.d(TAG, "onItemClick name: " + bluetoothDevice.getName() + " , address: " + bluetoothDevice.getAddress());
            address = bluetoothDevice.getAddress();
            Intent intent = new Intent(DeviceSelectActivity.this, HomeActivity.class);
            intent.putExtra("address", address);
            startActivity(intent);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_SCAN) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    scanDevices();
                }
            } else {
                Toast.makeText(this, "The app needs BLUETOOTH_SCAN permissions to continue", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "Broadcast receiver started.");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Broadcast receiver found device: " + device.getName());
                deviceListAdapter.addDevice(device);
                deviceListAdapter.notifyDataSetChanged();

            }
        }
    };
}
