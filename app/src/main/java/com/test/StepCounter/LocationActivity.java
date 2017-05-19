package com.test.StepCounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.google.gson.Gson;
import com.test.StepCounter.config.Constant;
import com.test.StepCounter.utils.RouteInfor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * AMapV1地图中简单介绍显示定位小蓝点
 */
public class LocationActivity extends Activity implements LocationSource,
        AMapLocationListener {
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private List<LatLng> latLngs = new ArrayList<>();
    private Chronometer timer ;
    private TextView meterview,speedview;
    private double metersum = 0;
    private boolean Locsuccess = false;
    private boolean RunStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        timer = (Chronometer) findViewById(R.id.timer);
        meterview = (TextView) findViewById(R.id.metertext);
        speedview = (TextView) findViewById(R.id.speedtext);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        Button button=(Button) findViewById(R.id.startRunbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunStart = true;
                timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                timer.start();
            }
        });
        button = (Button) findViewById(R.id.stopRunbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RunStart == true && !latLngs.isEmpty()){
                    saveRoute();
                }
                RunStart = false;
                timer.stop();
            }
        });
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if(Locsuccess == false){
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                    Locsuccess = true;
                }
                if(RunStart == true){
                    latLngs.add(new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude()));//获取经度)
                    aMap.addPolyline(new PolylineOptions().
                            addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));
                    float distance =0;
                    if(latLngs.size()>=2)distance = AMapUtils.calculateLineDistance(latLngs.get(latLngs.size()-2),latLngs.get(latLngs.size()-1));
                    metersum+=distance;
                    DecimalFormat df = new DecimalFormat("######0.0");
                    meterview.setText(df.format(metersum)+"米");
                    String t=timer.getText().toString();
                    String[] p = t.split(":");
                    int spend= Integer.valueOf(p[1])+ Integer.valueOf(p[0])*60;
                    speedview.setText(df.format(metersum/spend)+"米/秒");
                    //speedview.setText(metersum);
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
                if(Locsuccess == false){
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                    Locsuccess = true;
                }
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    public void saveRoute(){
        new AlertDialog.Builder(this).setTitle("是否结束当前跑步？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReturnToMain();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();

    }
    private void ReturnToMain(){
        //        RouteInfor routeInfor=new RouteInfor();
//        routeInfor.username= MainActivity.NowUser;
//        routeInfor.date= Constant.getTodayDate();
//        routeInfor.time= Constant.getNowtime();
//        routeInfor.points=new ArrayList<>();
//        for(int i=0;i<latLngs.size();i++){
//            routeInfor.points.add(new ArrayList<Double>());
//            routeInfor.points.get(i).add(latLngs.get(i).latitude);
//            routeInfor.points.get(1).add(latLngs.get(i).longitude);
//        }
        String t=timer.getText().toString();
        String[] p = t.split(":");
        int spend= Integer.valueOf(p[1])+ Integer.valueOf(p[0])*60;
//        routeInfor.spend=spend;
//        Gson gson = new Gson();
//        String ans=gson.toJson(routeInfor);
//        Toast.makeText(LocationActivity.this,ans,Toast.LENGTH_SHORT).show();
//        Log.d("UT3",ans);


        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt("OK",1);
        bundle.putDouble("meter",metersum);
        bundle.putDouble("time",spend/60);
        intent.setClass(LocationActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        setResult(1005,intent);
        LocationActivity.this.finish();
    }
}
