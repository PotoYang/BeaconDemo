package com.example.potoyang.beacondemo.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

@SuppressLint("MissingPermission")
public class MyGps {
    private Location location = null;
    private LocationManager locationManager = null;
    private Context context = null;

    /**
     * 初始化
     *
     * @param context
     */
    MyGps(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(getProvider());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 1000,
                50, locationListener);
    }

    // 获取Location Provider
    private String getProvider() {
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：是
        criteria.setAltitudeRequired(true);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);
        // 是否允许付费：否
        criteria.setCostAllowed(false);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的provider，第2个参数为true说明 , 如果只有一个provider是有效的,则返回当前provider
        return locationManager.getBestProvider(criteria, true);
    }

    private LocationListener locationListener = new LocationListener() {
        // 位置发生改变后调用
        public void onLocationChanged(Location l) {
            if (l != null) {
                location = l;
            }
        }

        // provider 被用户关闭后调用
        public void onProviderDisabled(String provider) {
            location = null;
        }

        // provider 被用户开启后调用
        public void onProviderEnabled(String provider) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l != null) {
                location = l;
            }
        }

        // provider 状态变化时调用
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public Location getLocation() {
        return location;
    }

    public void closeLocation() {
        if (locationManager != null) {
            if (locationListener != null) {
                locationManager.removeUpdates(locationListener);
                locationListener = null;
            }
            locationManager = null;
        }
    }
}
