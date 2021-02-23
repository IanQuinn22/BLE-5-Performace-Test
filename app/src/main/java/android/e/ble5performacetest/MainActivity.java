package android.e.ble5performacetest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final ParcelUuid SERVICE_UUID =
            ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");
    private static final int SEPARATOR_CHAR = 42;

    private EditText name;
    private EditText shirt;
    private EditText pants;
    private Button broadcast;
    private TextView people_list;
    private Button collect;

    private BluetoothManager manager;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private ArrayList<String> names = new ArrayList<String>();
    private HashMap<String,String> currentShirts = new HashMap<String,String>();
    private HashMap<String,String> currentPants = new HashMap<String,String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeBt();
        createUI();
    }

    private void createUI(){
        name = findViewById(R.id.name);
        shirt = findViewById(R.id.shirt);
        pants = findViewById(R.id.pants);
        broadcast = findViewById(R.id.broadcast);
        collect = findViewById(R.id.collect);
        people_list = findViewById(R.id.people_list);
        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BeaconBroadcast.class);
                intent.putExtra("name",name.getText().toString());
                intent.putExtra("shirt",shirt.getText().toString());
                intent.putExtra("pants",pants.getText().toString());
                startService(intent);
            }
        });
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (collect.getText().toString().equalsIgnoreCase("Collect Broadcasts")){
                    collect.setText("STOP");
                    scanLeDevice(true);
                } else {
                    scanLeDevice(false);
                    Assembler.clear();
                    collect.setText("COLLECT BROADCASTS");
                }
            }
        });
    }

    private void initializeBt(){
        manager = (BluetoothManager) getApplicationContext().getSystemService(
                Context.BLUETOOTH_SERVICE);
        btAdapter = manager.getAdapter();
        if (btAdapter == null) {
            Log.e("Bluetooth Error", "Bluetooth not detected on device");
        } else if (!btAdapter.isEnabled()) {
            Log.e("Error","Need to request Bluetooth");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else if (!btAdapter.isMultipleAdvertisementSupported()) {
            Log.e("Not supported", "BLE advertising not supported on this device");
        }
        bluetoothLeScanner = btAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setLegacy(false)
                .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .build();
        filters = new ArrayList<ScanFilter>();
        byte[] test = new byte[200];
        byte[] mask = new byte [200];
        for (int i = 0; i < 200; i++){
            test[i] = (byte)1;
            mask[i] = (byte)0;
        }
        //filters.add(new ScanFilter.Builder().setServiceData(SERVICE_UUID,test,mask).build());
        filters.add(new ScanFilter.Builder().setServiceUuid(SERVICE_UUID).build());
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            bluetoothLeScanner.startScan(filters, settings, leScanCallback);
        } else {
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            String address = result.getDevice().getName();
            byte[] pData = Assembler.gather(address, result.getScanRecord().getServiceData(SERVICE_UUID));
            if (pData != null) {
                update(pData);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private void update(byte[] data){
        int index = 0;
        while (data[index] != (byte)SEPARATOR_CHAR){
            index++;
        }
        byte[] name = Arrays.copyOfRange(data,0,index);
        index++;
        int shirtStart = index;
        while (data[index] != (byte)SEPARATOR_CHAR){
            index++;
        }
        byte[] shirtData = Arrays.copyOfRange(data,shirtStart,index);
        index++;
        byte[] pantsData = Arrays.copyOfRange(data,index,data.length);
        String nameStr = new String(name);
        String shirtStr = new String(shirtData);
        String pantsStr = new String(pantsData);
        if (!names.contains(nameStr)){
            currentShirts.put(nameStr,shirtStr);
            currentPants.put(nameStr,pantsStr);
            names.add(nameStr);
        }
        updateView();
    }

    private void updateView(){
        String nameList = "";
        for (String name : names){
            nameList = nameList + "\n" + name;
        }
        people_list.setText(nameList);
    }
}