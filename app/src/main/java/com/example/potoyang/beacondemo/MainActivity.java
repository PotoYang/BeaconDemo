package com.example.potoyang.beacondemo;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.potoyang.beacondemo.adapter.BlueAdapter;
import com.example.potoyang.beacondemo.bean.BlueDevice;
import com.example.potoyang.beacondemo.utlis.BlueUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int MY_PERMISSION_REQUEST_CONSTANT = 111;
    private static final int ENABLE_BLUE = 100;
    private static final String MAINACTIVITY = "MainActivity";
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_public)
    Button btnPublic;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.ls_blue)
    ListView lsBlue;
    @BindView(R.id.btn_stop)
    Button btn_stop;
    @BindView(R.id.btn_barchart)
    Button btn_barchart;
    @BindView(R.id.btn_linechart)
    Button btn_linechart;

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> devices;
    private Set<BlueDevice> setDevices = new HashSet<>();
    private BlueAdapter blueAdapter;
    private IntentFilter mFilter;
    /**
     * 需要连接的蓝牙设备
     */
    private BluetoothDevice currentBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= 6.0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CONSTANT);
        }
//        initData();
        setListener();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("main", "添加权限");
                    //permission granted!
                }
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /**关闭蓝牙*/
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }


    private void initData() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /**注册搜索蓝牙receiver*/
        mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);    //绑定状态监听
        mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);     //搜索完成时监听
        registerReceiver(mReceiver, mFilter);

//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);
//
//        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        registerReceiver(mReceiver, filter);
//
//        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        registerReceiver(mReceiver, filter);
        /**注册配对状态改变监听器*/
//        IntentFilter mFilter = new IntentFilter();
//        mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        mFilter.addAction(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, mFilter);

        getBondedDevices();
    }

    private void setListener() {
        btnStart.setOnClickListener(this);
        btnPublic.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_barchart.setOnClickListener(this);
        lsBlue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<BlueDevice> listDevices = blueAdapter.getListDevices();
                final BlueDevice blueDevice = listDevices.get(i);
                String msg = "";
                /**还没有配对*/
                if (blueDevice.getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
                    msg = "是否与设备" + blueDevice.getName() + "配对并连接？";
                } else {
                    msg = "是否与设备" + blueDevice.getName() + "连接？";
                }
                showDailog(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**当前需要配对的蓝牙设备*/
                        currentBluetoothDevice = blueDevice.getDevice();
                        /**还没有配对*/
                        if (blueDevice.getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
                            showProgressDailog();
                            startPariBlue(blueDevice);
                        }
                    }
                });
            }
        });

        /**长按取消配对*/
        lsBlue.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<BlueDevice> listDevices = blueAdapter.getListDevices();
                final BlueDevice blueDevices = listDevices.get(i);
                /**还没有配对*/
                if (blueDevices.getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
                    return false;
                    /**完成配对的*/
                } else {
                    showDailog("是否取消" + blueDevices.getName() + "配对？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            BlueUtils.unpairDevice(blueDevices.getDevice());
                        }
                    });
                }
                return true;
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (mBluetoothAdapter == null)
            return;
        switch (view.getId()) {
            case R.id.btn_start:
                /**如果本地蓝牙没有开启，则开启*/
                if (!mBluetoothAdapter.isEnabled()) {
                    // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
                    // 那么将会收到RESULT_OK的结果，
                    // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
                    Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(mIntent, ENABLE_BLUE);
                } else {
                    Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                    getBondedDevices();
                }
                break;
            case R.id.btn_public:
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);//180可见时间
                startActivity(intent);
                break;
            case R.id.btn_search:
                showProgressDailog();
                // 如果正在搜索，就先取消搜索
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                Log.i("main", "我在搜索");
                // 开始搜索蓝牙设备,搜索到的蓝牙设备通过广播返回
                mBluetoothAdapter.startDiscovery();
                break;
            case R.id.btn_stop:
                /**关闭蓝牙*/
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                }
                break;
            case R.id.btn_barchart:
                Intent intent1 = new Intent(this, BarchartTestActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    /**
     * 开始配对蓝牙设备
     *
     * @param blueDevice
     */
    private void startPariBlue(BlueDevice blueDevice) {
        BlueUtils blueUtils = new BlueUtils(blueDevice);
        blueUtils.doPair();
        hideDailog();
    }

    /**
     * 取消配对蓝牙设备
     *
     * @param blueDevice
     */
    private void stopPariBlue(BlueDevice blueDevice) {
        BlueUtils.unpairDevice(blueDevice.getDevice());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙开启成功", Toast.LENGTH_SHORT).show();
                getBondedDevices();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "蓝牙开始失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 获取所有已经绑定的蓝牙设备并显示
     */
    private void getBondedDevices() {
        if (!setDevices.isEmpty())
            setDevices.clear();
        devices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : devices) {
            BlueDevice blueDevice = new BlueDevice();
            blueDevice.setName(bluetoothDevice.getName());
            blueDevice.setAddress(bluetoothDevice.getAddress());
            blueDevice.setDevice(bluetoothDevice);
            blueDevice.setStatus("已配对");
            setDevices.add(blueDevice);
        }
        if (blueAdapter == null) {
            blueAdapter = new BlueAdapter(this, setDevices);
            lsBlue.setAdapter(blueAdapter);
        } else {
            blueAdapter.setSetDevices(setDevices);
            blueAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mReceiver);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            /** 搜索到的蓝牙设备*/
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // 搜索到的不是已经配对的蓝牙设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    short rssi = Objects.requireNonNull(intent.getExtras()).getShort(BluetoothDevice.EXTRA_RSSI);
                    BlueDevice blueDevice = new BlueDevice();
                    blueDevice.setName(device.getName() == null ? device.getAddress() : device.getName());
                    blueDevice.setAddress(device.getAddress());
                    blueDevice.setDevice(device);
                    blueDevice.setRssi(rssi);
                    setDevices.add(blueDevice);
                    blueAdapter.setSetDevices(setDevices);
                    blueAdapter.notifyDataSetChanged();
                    Log.d(MAINACTIVITY, "搜索结果......" + device.getName() + device.getAddress());
                }
                /**当绑定的状态改变时*/
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(MAINACTIVITY, "正在配对......");

                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(MAINACTIVITY, "完成配对");
                        hideProgressDailog();
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d(MAINACTIVITY, "取消配对");
                        Toast.makeText(MainActivity.this, "成功取消配对", Toast.LENGTH_SHORT).show();
                        getBondedDevices();
                        break;
                    default:
                        break;
                }

                /**搜索完成*/
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                setProgressBarIndeterminateVisibility(false);
                Log.d(MAINACTIVITY, "搜索完成......");
                hideProgressDailog();
            }
        }
    };
}
