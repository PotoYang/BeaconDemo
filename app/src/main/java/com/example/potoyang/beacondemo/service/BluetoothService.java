package com.example.potoyang.beacondemo.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothService extends Service {

    BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter.startLeScan(scaleCallback);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //扫描到设备之后的回调方法
    private BluetoothAdapter.LeScanCallback scaleCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            if (device.getName() != null && device.getAddress() != null) {
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("address", device.getAddress());
                        intent.putExtra("rssi", rssi);
                        intent.setAction("com.example.potoyang.beacondemo.service.BluetoothService");
                        sendBroadcast(intent);
                    }
                };
            }
        }
    };
}
