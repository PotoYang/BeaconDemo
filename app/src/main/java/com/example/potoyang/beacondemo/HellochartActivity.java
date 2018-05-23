package com.example.potoyang.beacondemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class HellochartActivity extends AppCompatActivity {

    private LineChartView lineChart;

    private List<PointValue> mPointValues = new ArrayList<>();
    private List<AxisValue> mAxisValues = new ArrayList<>();
    private Map<String, Integer> rssiMap = new HashMap<>();
    private List<Integer> datas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hellochart);
        lineChart = findViewById(R.id.chart);

        final BluetoothClient mClient = new BluetoothClient(HellochartActivity.this);

        beginScale(mClient);
    }

    /**
     * 初始化LineChart的一些设置
     */
    private void initLineChart() {
        int i = 0;
        for (Integer d : datas) {
            mAxisValues.add(new AxisValue(i).setLabel(String.valueOf(i)));
            mPointValues.add(new PointValue(i, Float.valueOf(d.toString())));
            i++;
        }

        Line line = new Line(mPointValues).setColor(Color.BLUE).setCubic(false);  //折线的颜色
        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setHasLabels(true);
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        axisX.setTextSize(7);//设置字体大小
        axisX.setValues(mAxisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部

        Axis axisY = new Axis();  //Y轴
        axisY.setTextColor(Color.BLACK);  //设置字体颜色
        axisY.setMaxLabelChars(7); //默认是3，只能看最后三个数字
        axisY.setName("温度");//y轴标注
        axisY.setTextSize(7);//设置字体大小

        data.setAxisYLeft(axisY);  //Y轴设置在左边

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
    }

    private void beginScale(BluetoothClient mClient) {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(2000, 5)   // 先扫BLE设备1次，每次3s
                .build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                Toast.makeText(HellochartActivity.this, "开始扫描", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                if (device.getAddress().contains("AC:23:3F:20:4A:20")) {
                    rssiMap.put(device.getAddress(), device.rssi);
                    System.out.println("AAAA " + device.rssi);
                    datas.add(device.rssi);
                }
//                else if (device.getAddress().equals("AC:23:3F:20:4A:22")) {
//                    rssiMap.put(device.getAddress(), device.rssi);
//                    System.out.println("BBBB " + device.rssi);
//                } else if (device.getAddress().equals("AC:23:3F:20:24:00")) {
//                    rssiMap.put(device.getAddress(), device.rssi);
//                    System.out.println("CCCC " + device.rssi);
//                } else if (device.getAddress().equals("AC:23:3F:20:5D:53")) {
//                    rssiMap.put(device.getAddress(), device.rssi);
//                    System.out.println("DDDD " + device.rssi);
//                }
            }

            @Override
            public void onSearchStopped() {
                Toast.makeText(HellochartActivity.this, "扫描完成", Toast.LENGTH_SHORT).show();
                initLineChart();
            }

            @Override
            public void onSearchCanceled() {
                Toast.makeText(HellochartActivity.this, "扫描取消", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
