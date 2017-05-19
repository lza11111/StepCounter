package com.test.StepCounter;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.test.StepCounter.base.StepMode;
import com.test.StepCounter.config.Constant;
import com.test.StepCounter.customs.ImageBtnWithText;
import com.test.StepCounter.fragment.recent_fragment;
import com.test.StepCounter.fragment.my_fragment;
import com.test.StepCounter.fragment.index_fragment;
import com.test.StepCounter.pojo.StepData;
import com.test.StepCounter.service.StepService;
import com.test.StepCounter.utils.DbUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener,Handler.Callback{
    private BottomNavigationBar mBottomNavigationBar;
    private recent_fragment mRecentfragment;
    private index_fragment mIndexfragment;
    private my_fragment mMyfragment;
    private String DB_NAME = "StepCounter";
    private  List<StepData> list;
    private LinearLayout recentll;
    private int mStep=0;
    private int mMaxstep = 1000;
    public static String NowUser = null;
    private final String TAG = com.test.StepCounter.MainActivity.class.getSimpleName();
    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL = 500;
    private CircleProgressView C_PV;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private String CURRENTDATE;
    private Handler delayHandler;

    private int Nowtimes = 0;
    private double Nowsumtime = 0.0;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER:
                // 更新界面上的步数
                mStep=msg.getData().getInt("step");
                C_PV=(CircleProgressView) findViewById(R.id.CPV);
                if(C_PV!=null) {
                    //C_PV.setmTxtHint2("今天走了" + String.valueOf(mStep) + "步");
                    C_PV.setProgress(mStep,mMaxstep);
                }
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        Log.d("UT3","init");
        startServiceForStrategy();
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
//        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        mBottomNavigationBar.setBarBackgroundColor(R.color.white);//set background color for navigation bar
        //mBottomNavigationBar.setInActiveColor(R.color.white);//unSelected icon color
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(android.R.drawable.ic_menu_recent_history, R.string.Recent))
                .addItem(new BottomNavigationItem(R.mipmap.person, R.string.Index))
                .addItem(new BottomNavigationItem(R.mipmap.my, R.string.My))
                .setFirstSelectedPosition(1)
                .initialise();

        mBottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();

    }

    /**
     * set the default fragment
     */
    private void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mIndexfragment = index_fragment.newintance(mStep);
        transaction.replace(R.id.ll_content, mIndexfragment).commit();
    }

    @Override
    public void onTabSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mMyfragment==null){
            mMyfragment=new my_fragment();
            transaction.add(R.id.ll_content,mMyfragment);
        }
        if(mRecentfragment==null){
            mRecentfragment=new recent_fragment();
            transaction.add(R.id.ll_content,mRecentfragment);
        }
        if(mIndexfragment==null){
            mIndexfragment=index_fragment.newintance(mStep);
            transaction.add(R.id.ll_content,mIndexfragment);
        }

        switch (position) {
            case 0:
                transaction.show(mRecentfragment);
                transaction.hide(mIndexfragment);
                transaction.hide(mMyfragment);
                //transaction.commit();
                //transaction.replace(R.id.ll_content, mRecentfragment).commit();

                break;
            case 1:
                transaction.show(mIndexfragment);
                transaction.hide(mRecentfragment);
                transaction.hide(mMyfragment);
                //transaction.commit();
                break;
            case 2:
//                if (NowUser == null) {
//                    Log.d("UT3","tologin");
//                    tologin();
//                    Log.d("UT3","loginok");
//                }
                transaction.show(mMyfragment);
                transaction.hide(mIndexfragment);
                transaction.hide(mRecentfragment);
                break;

        }
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    public void tologin(View view) {
        if(NowUser != null)return;
        Intent intent = new Intent();

        intent.setClass(MainActivity.this, LoginActivity.class);

        startActivityForResult(intent, 1000);

    }

    public void decmonth(View view){
        recent_fragment.NowMonth --;
        if (recent_fragment.NowMonth == 0)recent_fragment.NowMonth = 12;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new recent_fragment.PlaceholderFragment()).commit();
    }
    public void incmonth(View view){
        recent_fragment.NowMonth ++;
        if (recent_fragment.NowMonth == 13)recent_fragment.NowMonth = 1;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new recent_fragment.PlaceholderFragment()).commit();
    }

    public void startrun(View view){
        if(NowUser != null)return;
        Intent intent = new Intent();

        intent.setClass(MainActivity.this, LocationActivity.class);

        startActivityForResult(intent, 1005);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 1000)
        {
            NowUser = data.getExtras().getString("username");
            //mMyfragment.setUsername(NowUser);
        }
        else if(requestCode == 1005 && resultCode == 1005){
            Double t = new Double(0.0);
            TextView sum = (TextView) findViewById(R.id.tab_sport_mile_tv);
            t=Double.valueOf(sum.getText().toString());
            t+=data.getDoubleExtra("meter",0.0);
            DecimalFormat    df   = new DecimalFormat("######0.0");
            sum.setText(String.valueOf(df.format(t/1000)));

            sum= (TextView) findViewById(R.id.tab_sport_num_tv);
            sum.setText(++Nowtimes+"");

            sum =(TextView)findViewById(R.id.tab_sport_time_num_tv);
            Nowsumtime+=data.getDoubleExtra("time",0.0);
            sum.setText(Nowsumtime+"");
        }
    }
    private void startServiceForStrategy() {
        if (!isServiceWork(this, StepService.class.getName())) {
            setupService(true);
            Log.d("UT3","start");
            //Toast.makeText(MainActivity.this,"start",Toast.LENGTH_SHORT).show();
        } else {
            setupService(false);
            //Toast.makeText(MainActivity.this,"stop",Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        DbUtils.createDb(this, DB_NAME);
        //获取当天的数据，用于展示
        mRecentfragment.NowMonth=Integer.valueOf(new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis())));
        CURRENTDATE= Constant.getTodayDate();
        list = DbUtils.getQueryAll(StepData.class);
        int tempStep = StepMode.CURRENT_SETP;
        //List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepData data = new StepData();
            data.setToday(CURRENTDATE);
            data.setStep(tempStep + "");
            DbUtils.insert(data);
        }
        //DbUtils.deleteAll(StepData.class);

        C_PV = (CircleProgressView) findViewById(R.id.CPV);
        delayHandler = new Handler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 启动service
     *
     * @param flag true-bind和start两种方式一起执行 false-只执行bind方式
     */
    private void setupService(boolean flag) {
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Log.d("UT3","setupService");
        if (flag) {
            startService(intent);
        }
    }
    public void ExitLogin(View view){
        if(NowUser == null){
            Toast.makeText(MainActivity.this,"您还没有登录！", Toast.LENGTH_LONG).show();
            return;
        }
        new AlertDialog.Builder(this).setTitle("确认注销当前用户吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NowUser= null;
                        TextView mTv=(TextView)findViewById(R.id.TV_userID);
                        mTv.setText("点击登录");
                        Toast.makeText(MainActivity.this,"注销成功！",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();

    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {

            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}