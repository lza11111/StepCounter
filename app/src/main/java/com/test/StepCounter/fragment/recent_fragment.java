package com.test.StepCounter.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.StepCounter.CircleProgressView;
import com.test.StepCounter.MainActivity;
import com.test.StepCounter.R;
import com.test.StepCounter.config.Constant;
import com.test.StepCounter.pojo.StepData;
import com.test.StepCounter.utils.DbUtils;
import com.test.StepCounter.utils.HttpUtils;
import com.test.StepCounter.utils.StepInfor;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2016/11/20.
 * Blog:http://blog.csdn.net/student9128
 * Describe：the first fragment
 */

public class recent_fragment extends Fragment {
    private String DB_NAME = "StepCounter";
    private List<StepData> list;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recent_layout,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recentRecycler);



        //while(list==null);
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recentRecycler);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new RecyclerAdapter());

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        DbUtils.createDb(getContext(), DB_NAME);
        //获取当天的数据，用于展示

        new Thread(){
            public void run(){
                try{
                    HttpUtils httputils=new HttpUtils();
                    String kv=httputils.postKv(Constant.getUserStepUrl,"username="+MainActivity.NowUser);
                    Log.d("UT3",kv);
                    Gson gson=new Gson();
                    StepInfor stepInfor=gson.fromJson(kv,StepInfor.class);
                    //Log.d("UT3",stepInfor.data);
                    //list =new ArrayList<StepData>();
                    int len=stepInfor.data.size();
                    for(int i=0;i<len;i++)Log.d("UT3",stepInfor.data.get(i).getToday());
                    list=new ArrayList<StepData>(stepInfor.data);
                    if (getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new RecyclerAdapter());
//                            BadgeItem badgeItem = new BadgeItem();
//                            badgeItem.setHideOnSelect(false)
//                                    .setText(String.valueOf(list.size()))
//                                    .setBackgroundColorResource(R.color.orange)
//                                    .setBorderWidth(0);
//                            BottomNavigationBar mBottomNavigationBar = (BottomNavigationBar) getView().findViewById(R.id.bottom_navigation_bar);
//                            mBottomNavigationBar.get
                        }
                    });
                    //Type type=new TypeToken<ArrayList<StepData>>(){}.getType();
                    //list =gson.fromJson(stepInfor.data,type);
                    //list = DbUtils.getQueryAll(StepData.class);
                }catch (IOException e){
                    Log.d("UT3",e.toString());
                }

            }
        }.start();

    }
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.imagetext, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setTextSize(20);
            holder.tv.setText("你在"+list.get(position).getToday()+"走了"+list.get(position).getStep()+"步");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.text_view);
            }
        }

    }
}
