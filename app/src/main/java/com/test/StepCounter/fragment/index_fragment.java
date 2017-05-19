package com.test.StepCounter.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
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
import com.test.StepCounter.CircleProgressView;
import com.test.StepCounter.MainActivity;
import com.test.StepCounter.R;
import com.test.StepCounter.config.Constant;
import com.test.StepCounter.utils.RouteInfor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2016/11/20.
 * Blog:http://blog.csdn.net/student9128
 * Describe：the second fragment
 */

public class index_fragment extends Fragment implements View.OnTouchListener{

    private int mStep=0;
    private int mMaxstep =1000;
    private ViewFlipper viewFlipper;
    private float touchDownX;  // 手指按下的X坐标
    private float touchUpX;  //手指松开的X坐标


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 取得左右滑动时手指按下的X坐标
            touchDownX = event.getX();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // 取得左右滑动时手指松开的X坐标
            touchUpX = event.getX();
            // 从左往右，看前一个View
            if (touchDownX - touchUpX > 100) {
                // 显示上一屏动画
                viewFlipper.setInAnimation(inFromRightAnimation());
                viewFlipper.setOutAnimation(outToLeftAnimation());
                // 显示上一屏的View
                viewFlipper.showPrevious();
                // 从右往左，看后一个View
            } else if (touchUpX - touchDownX > 100) {
                //显示下一屏的动画
                viewFlipper.setInAnimation(inFromLeftAnimation());
                viewFlipper.setOutAnimation(outToRightAnimation());
                // 显示下一屏的View
                viewFlipper.showNext();
            }
            return true;
        }
        return false;
    }
    /**
     * 定义从右侧进入的动画效果
     * @return
     */
    protected Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(200);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    /**
     * 定义从左侧退出的动画效果
     * @return
     */
    protected Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(200);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    /**
     * 定义从左侧进入的动画效果
     * @return
     */
    protected Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(200);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    /**
     * 定义从右侧退出时的动画效果
     * @return
     */
    protected Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(200);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.index_layout,container,false);
        final CircleProgressView cpv=(CircleProgressView)view.findViewById(R.id.CPV);
        cpv.setmTxtHint1("步数");
        //cpv.setmTxtHint2("今天走了"+String.valueOf(mStep)+"步");
        cpv.setProgress(mStep,mMaxstep);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.index_viewflipper);

        if(viewFlipper!=null)viewFlipper.setOnTouchListener(this);


            return view;

    }


    @Override
    public void onStart() {
        super.onStart();
}

    public static index_fragment newintance(int step) {
        Bundle bundle = new Bundle();
        bundle.putInt("arg",step);
        index_fragment mfragment=new index_fragment();
        mfragment.setArguments(bundle);
        return mfragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




}