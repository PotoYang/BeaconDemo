package com.example.potoyang.beacondemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BarchartTestActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = BarchartTestActivity.class.getSimpleName();

    private static final String[] UUIDS = {"AC:23:3F:20:5D:53", "AC:23:3F:20:4A:20",
            "AC:23:3F:20:24:00", "AC:23:3F:20:4A:22"};

    private static final int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.BLACK};

    @BindView(R.id.btn_begin)
    Button btn_begin;
    @BindView(R.id.sv_showdata)
    SurfaceView sv_showdata;

    private Map<String, Integer> rssiMap = new HashMap<>(8, 0.75F);
    private SurfaceHolder mHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barchart_test);

        ButterKnife.bind(this);

        final BluetoothClient mClient = new BluetoothClient(BarchartTestActivity.this);
        mHolder = sv_showdata.getHolder();
        mHolder.addCallback(this);

        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginScale(mClient);
            }
        });
    }

    private void beginScale(BluetoothClient mClient) {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(1000, 20)   // 先扫BLE设备1次，每次3s
                .build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                Toast.makeText(BarchartTestActivity.this, "开始扫描", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                if (device.getAddress().equals("AC:23:3F:20:4A:20")) {
                    rssiMap.put(device.getAddress(), device.rssi);
                    System.out.println(device.rssi);
                }
            }

            @Override
            public void onSearchStopped() {
                Toast.makeText(BarchartTestActivity.this, "扫描完成", Toast.LENGTH_SHORT).show();
                draw();
            }

            @Override
            public void onSearchCanceled() {
                Toast.makeText(BarchartTestActivity.this, "扫描取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void draw() {
        Canvas canvas = mHolder.lockCanvas();
        canvas.drawRGB(255, 255, 255);
        Paint paint = new Paint();
        paint.setTextSize(20.0F);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        if (rssiMap.size() != 0) {
            for (Map.Entry entry : rssiMap.entrySet()) {
                String address = (String) entry.getKey();
                for (int i = 0; i < UUIDS.length; i++) {
                    if (UUIDS[i].equals(address)) {
                        paint.setColor(COLORS[i]);
                        int iRssi = Math.abs(Integer.valueOf(entry.getValue().toString()));
                        float power = (float) ((iRssi - 65) / (10 * 3.0));
                        float dis = (float) Math.pow(10, power);

                        float cx = canvas.getWidth() / 3;
                        float cy = canvas.getHeight() / 3;
                        if (i / 2 == 0) {
                            paint.setStrokeWidth(5.0F);
                            canvas.drawCircle(cx + cx * i, cy, dis * 250, paint);
                            canvas.drawLine(cx + cx * i, cy, cx + cx * i, cy - dis * 250, paint);
                            paint.setStrokeWidth(1.0F);
                            canvas.drawText(address, cx + cx * i - 100, cy, paint);
                            canvas.drawText(String.valueOf(dis) + "," + iRssi, cx + cx * i - 100, cy - dis * 125, paint);
//                            drawLine(float startX, float startY, float stopX, float stopY, Paintpaint)
                            //画线，参数一起始点的x轴位置，参数二起始点的y轴位置，参数三终点的x轴水平位置，参数四y轴垂直位置，最后一个参数为Paint 画刷对象。
                        } else {
                            paint.setStrokeWidth(5.0F);
                            canvas.drawCircle(cx + cx * (i - 2), cy + cy, dis * 250, paint);
                            canvas.drawLine(cx + cx * (i - 2), cy + cy, cx + cx * (i - 2), cy + cy - dis * 250, paint);
                            paint.setStrokeWidth(1.0F);
                            canvas.drawText(address, cx + cx * (i - 2) - 100, cy + cy, paint);
                            canvas.drawText(String.valueOf(dis) + "," + iRssi, cx + cx * (i - 2) - 100, cy + cy - dis * 125, paint);
                        }
                        break;
                    }
                }

            }
        }

        mHolder.unlockCanvasAndPost(canvas);// 更新屏幕显示内容
    }

    private static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
