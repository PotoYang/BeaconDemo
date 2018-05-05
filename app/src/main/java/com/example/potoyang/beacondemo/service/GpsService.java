package com.example.potoyang.beacondemo.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

public class GpsService extends Service {
    private MyGps gps = null;
    private boolean threadDisable = false;
    private final static String TAG = GpsService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new MyGps(GpsService.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!threadDisable) {
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (gps != null) { //当结束服务时gps为空
                        //获取经纬度
                        Location location = gps.getLocation();
                        //发送广播
                        Intent intent = new Intent();
                        intent.putExtra("lat", location == null ? "" : location.getLatitude() + "");
                        intent.putExtra("lon", location == null ? "" : location.getLongitude() + "");
                        intent.setAction("com.example.wyf.suidaodemo.service.GpsService");
                        sendBroadcast(intent);
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        threadDisable = true;
        if (gps != null) {
            gps.closeLocation();
            gps = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
