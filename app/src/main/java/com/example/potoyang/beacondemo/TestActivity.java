package com.example.potoyang.beacondemo;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class TestActivity extends AppCompatActivity implements Callback {

    public static final String[] UUIDS = {"AC:23:3F:20:5D:53", "AC:23:3F:20:4A:20",
            "AC:23:3F:20:24:00", "AC:23:3F:20:4A:22"};

    private static final int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

    private SurfaceView mSurface;
    private SurfaceHolder mHolder;
    private BluetoothAdapter mBtAdapter;
    //private Message msg ;
    //private Bundle bundle;
    private Vector<String> mDevicesVector;
    private Short[] mRSSINum;
    private Short[][] mRSSIVector;
    private Vector<Paint> mPaint;

    private Map<String, Short> rssiMap = new HashMap<>();

    private int num = 0;
    //消息句柄(线程里无法进行界面更新，所以要把消息从线程里发送出来在消息句柄里进行处理)
    @SuppressLint("HandlerLeak")
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            short now = bundle.getShort("msg");
            Log.d("onGet", String.valueOf(now));
            if (msg.what == 0x01) {
                num = 0;
                draw();
            }
            doDiscovery();
        }

        //画图像
        private void draw() {
            Canvas canvas = mHolder.lockCanvas();
            canvas.drawRGB(255, 255, 255);

            //canvas.drawText(i+": "+mDevicesVector.get(i), 5, i*10+12, mPaint.get(i));
            //canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2,150+mRSSIVector.get(i), mPaint.get(i));
            if (rssiMap.size() != 0) {
                for (Map.Entry entry : rssiMap.entrySet()) {
                    String address = (String) entry.getKey();
                    for (int i = 0; i < UUIDS.length; i++) {
                        if (UUIDS[i].equals(address)) {
                            int iRssi = Math.abs(Integer.valueOf(entry.getValue().toString()));
                            float power = (float) ((iRssi - 65) / (10 * 2.0));
                            float dis = (float) Math.pow(10, power);

                            float cx = canvas.getWidth() / 3;
                            float cy = canvas.getHeight() / 3;
                            if (i / 2 == 0) {
                                canvas.drawCircle(cx + cx * i, cy, dis * 150, mPaint.get(i));
                                canvas.drawText(mDevicesVector.get(i), cx + cx * i - 100, cy, mPaint.get(i));
                            } else {
                                canvas.drawCircle(cx + cx * (i - 2), cy + cy, dis * 150, mPaint.get(i));
                                canvas.drawText(mDevicesVector.get(i), cx + cx * (i - 2) - 100, cy + cy, mPaint.get(i));
                            }
                        }
                    }

                }
            }

//            for (int i = 0; i < mDevicesVector.size(); i++) {

//                    canvas.drawLine(j * 5 + 5, 200 * i + 95, j * 5 + 5, 200 * i + 95 + mRSSIVector[i][j], mPaint.get(i));
//                }
//            }
            mHolder.unlockCanvasAndPost(canvas);// 更新屏幕显示内容
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //msg = new Message();//消息
        ///bundle = new Bundle();
        mDevicesVector = new Vector<>(4);//向量
        mRSSIVector = new Short[50][];
        mRSSINum = new Short[50];
        for (int i = 0; i < 50; i++) {
            mRSSIVector[i] = new Short[100];
            mRSSINum[i] = 0;
        }
        mDevicesVector.addAll(Arrays.asList(UUIDS));

        mPaint = new Vector<Paint>();
        for (int i = 0; i < 4; i++) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Style.STROKE);
            paint.setColor(COLORS[i]);
            paint.setStrokeWidth(4.0f);
            paint.setTextSize(30);
            mPaint.add(paint);
        }

        //SurfaceView框架
        mSurface = findViewById(R.id.surface);
        mHolder = mSurface.getHolder();
        mHolder.addCallback(this);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        // Register for broadcasts when discovery has finished
//        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        /**如果本地蓝牙没有开启，则开启*/
        if (!mBtAdapter.isEnabled()) {
            // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
            // 那么将会收到RESULT_OK的结果，
            // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 0);
        } else {
            Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
            doDiscovery();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙开启成功", Toast.LENGTH_SHORT).show();
                doDiscovery();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "蓝牙开始失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Start device discover with the BluetoothAdapter
    private void doDiscovery() {
        rssiMap.clear();
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    //【查找蓝牙设备】
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive", "OK");
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null && device.getName().equals("BrightBeacon")) {
                    Log.d("Name", device.getName());
                    short rssi = Objects.requireNonNull(intent.getExtras()).getShort(BluetoothDevice.EXTRA_RSSI);
                    rssiMap.put(device.getAddress(), rssi);
                    Log.d("RSSI", device.getName() + "  " + String.valueOf(rssi));
                    num++;
                }

                if (num == 4) {
                    setProgressBarIndeterminateVisibility(false);
                    if (mDevicesVector.size() != 0) {
                        Message msg = new Message();//消息
                        Bundle bundle = new Bundle();
                        bundle.clear();
                        Log.d("onReceive", "1");
                        msg.what = 0x01;//消息类别
                        bundle.putShort("msg", (short) 0);
                        Log.d("onReceive", "2");
                        msg.setData(bundle);
                        Log.d("onReceive", "3");
                        myHandler.sendMessage(msg);
                        Log.d("onReceive", "4");
                    }
                }   // When discovery is finished, change the Activity title
            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}