package com.test.StepCounter.fragment;

import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TabHost;
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

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Kevin on 2016/11/20.
 * Blog:http://blog.csdn.net/student9128
 * Describe：the first fragment
 */

public class recent_fragment extends Fragment {
    private String DB_NAME = "StepCounter";
    private static List<StepData> list;
    private static List<StepData> nowlist;
    public static int NowMonth;
    //private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        //recyclerView = (RecyclerView) view.findViewById(R.id.recentRecycler);
//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
//        }


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
        if (MainActivity.NowUser == null) {
            list = DbUtils.getQueryAll(StepData.class);
            getFragmentManager().beginTransaction().replace(R.id.container, new PlaceholderFragment()).commit();
            //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            //recyclerView.setAdapter(new RecyclerAdapter());
        } else
            new Thread() {
                public void run() {
                    try {
                        HttpUtils httputils = new HttpUtils();
                        String kv = httputils.postKv(Constant.getUserStepUrl, "username=" + MainActivity.NowUser);
                        Log.d("UT3", kv);
                        Gson gson = new Gson();
                        StepInfor stepInfor = gson.fromJson(kv, StepInfor.class);

                        list = new ArrayList<StepData>(stepInfor.data);
                        DbUtils.insertAll(list);
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    //recyclerView.setAdapter(new RecyclerAdapter());
//                            BadgeItem badgeItem = new BadgeItem();
//                            badgeItem.setHideOnSelect(false)
//                                    .setText(String.valueOf(list.size()))
//                                    .setBackgroundColorResource(R.color.orange)
//                                    .setBorderWidth(0);
//                            BottomNavigationBar mBottomNavigationBar = (BottomNavigationBar) getView().findViewById(R.id.bottom_navigation_bar);
//                            mBottomNavigationBar.get
                                    getFragmentManager().beginTransaction().replace(R.id.container, new PlaceholderFragment()).commit();
                                }
                            });
                        //Type type=new TypeToken<ArrayList<StepData>>(){}.getType();
                        //list =gson.fromJson(stepInfor.data,type);
                        //list = DbUtils.getQueryAll(StepData.class);
                    } catch (IOException e) {
                        Log.d("UT3", e.toString());
                    }

                }
            }.start();

    }

    //    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.imagetext, parent, false));
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(MyViewHolder holder, int position) {
//            holder.tv.setTextSize(20);
//            holder.tv.setText("你在"+list.get(position).getToday()+"走了"+list.get(position).getStep()+"步");
//        }
//
//        @Override
//        public int getItemCount() {
//            return list.size();
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder{
//
//            TextView tv;
//
//            public MyViewHolder(View view) {
//                super(view);
//                tv = (TextView) view.findViewById(R.id.text_view);
//            }
//        }
//
//    }
    public static class PlaceholderFragment extends Fragment {
        public final static String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                "Sep", "Oct", "Nov", "Dec",};

        public final static String[] days = new String[]{"Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun",};

        public final static int[] monthday = new int[]{0,31,28,31,30,31,30,31,31,30,31,30,31};
        private LineChartView chartTop;
        private LineChartView chartBottom;

        private LineChartData lineData;
        private LineChartData columnData;

        private TextView dateTV, stepTV, lengthTV, xiaohaoTV;
        private TextView topview;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.recent_layout, container, false);
            nowlist = new ArrayList<>();
            for(int i=0;i<list.size();i++){
                if(getMonth(list.get(i).getToday()) == NowMonth)nowlist.add(list.get(i));
            }
            topview=(TextView)rootView.findViewById(R.id.recent_infor_top_month);
            //NowMonth=Integer.valueOf(new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis())));
            topview.setText(NowMonth+"月");
            dateTV=(TextView)rootView.findViewById(R.id.recent_infor_date_data);
            stepTV=(TextView)rootView.findViewById(R.id.recent_infor_step_data);
            lengthTV=(TextView)rootView.findViewById(R.id.recent_infor_length_data);
            xiaohaoTV=(TextView)rootView.findViewById(R.id.recent_infor_calorie_data);
            if(nowlist.size()!= 0)setDateText(dateTV,getMonth(nowlist.get(nowlist.size()-1).getToday()),getDay(nowlist.get(nowlist.size()-1).getToday()));
            else setDateText(dateTV,NowMonth,1);
//                setDateText(dateTV,
//                    Integer.valueOf(new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis()))),
//                    Integer.valueOf(new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()))));
            if(nowlist.size()!= 0)setBigSmalltext(stepTV,nowlist.get(nowlist.size()-1).getStep(),"步");
            else setBigSmalltext(stepTV,"0","步");
            if(nowlist.size()!= 0)setBigSmalltext(lengthTV,String.valueOf(Integer.valueOf(nowlist.get(nowlist.size()-1).getStep())*0.8),"米");
            else setBigSmalltext(lengthTV,"0","米");
            if(nowlist.size()!= 0)setBigSmalltext(xiaohaoTV,String.valueOf(Integer.valueOf(nowlist.get(nowlist.size()-1).getStep())*0.1),"千卡");
            else setBigSmalltext(xiaohaoTV,"0","千卡");
            // *** TOP LINE CHART ***
            //chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);

            // Generate and set data for line chart
            //generateInitialLineData();

            // *** BOTTOM COLUMN CHART ***

            chartBottom = (LineChartView) rootView.findViewById(R.id.chart_bottom);

            generateColu1mnData();

            return rootView;
        }

        private void generateColu1mnData() {
            int maxday=0;
            int numColumns = monthday[NowMonth];//months.length;
            int listlen=nowlist.size();
            Log.d("UT3", String.valueOf(numColumns));
            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<Line> columns = new ArrayList<Line>();
            List<PointValue> values =new ArrayList<PointValue>();
            for (int i = 1; i <= numColumns; ++i) {
                boolean flag=false;
                for(int j=0;j<listlen;j++){
                    String[] date=nowlist.get(j).getToday().split("/");
                    if(Integer.valueOf(date[1]) == NowMonth && Integer.valueOf(date[2]) == i){
                        maxday=i;
                        values.add(new PointValue(i ,Integer.valueOf(nowlist.get(j).getStep())));
                        flag=true;
                        break;
                    }

                }
                if(!flag)values.add(new PointValue(i, 0));
                axisValues.add(new AxisValue(i).setLabel(String.valueOf(i)));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_BLUE).setCubic(true);
            line.setCubic(false);
            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            columnData = new LineChartData(lines);
            columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(5));
            chartBottom.setLineChartData(columnData);
            // Set value touch listener that will trigger changes for chartTop.
            chartBottom.setOnValueTouchListener(new ValueTouchListener());
            // Set selection mode to keep selected month column highlighted.
            chartBottom.setValueSelectionEnabled(true);
            chartBottom.setMaxZoom(5);

            //Viewport tempViewport = new Viewport(Math.max(0,maxday-10), 0, 50, 10000) ;
//            Viewport v = new Viewport(0, 1000, 6, 0);
//            chartBottom.setMaximumViewport(v);
//            chartBottom.setCurrentViewport(v);

            final Viewport v = new Viewport(chartBottom.getMaximumViewport());
            v.bottom = 0;
            v.top = 1000;
            v.left = 1;
            v.right = monthday[NowMonth];
            chartBottom.setMaximumViewport(v);
            final Viewport v2 = new Viewport(chartBottom.getMaximumViewport());
            v.bottom = 0;
            v.top = 1000;
            v.left = Math.max(0,maxday-5);
            v.right = Math.min(Math.max(0,maxday+5),monthday[NowMonth]);
            chartBottom.setCurrentViewport(v);
            // chartBottom.setOnClickListener(new View.OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // SelectedValue sv = chartBottom.getSelectedValue();
            // if (!sv.isSet()) {
            // generateInitialLineData();
            // }
            //
            // }
            // });
            chartBottom.setZoomType(ZoomType.HORIZONTAL);
        }

        /**
         * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
         * will select value on column chart.
         */
        private void generateInitialLineData() {
            int numValues = 7;

            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<PointValue> values = new ArrayList<PointValue>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, 0));
                axisValues.add(new AxisValue(i).setLabel(days[i]));
            }
            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            lineData = new LineChartData(lines);
            lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

            chartTop.setLineChartData(lineData);

            // For build-up animation you have to disable viewport recalculation.
            chartTop.setViewportCalculationEnabled(false);

            // And set initial max viewport and current viewport- remember to set viewports after data.
            Viewport v = new Viewport(0, 1000, 6, 0);
            chartTop.setMaximumViewport(v);
            chartTop.setCurrentViewport(v);

            chartTop.setZoomType(ZoomType.HORIZONTAL);
        }

        private void generateLineData(int color, float range) {
            // Cancel last animation if not finished.
            //chartTop.cancelDataAnimation();

            // Modify data targets
            Line line = lineData.getLines().get(0);// For this example there is always only one line.
            line.setColor(color);
            for (PointValue value : line.getValues()) {
                // Change target only for Y value.
                value.setTarget(value.getX(), (float) Math.random() * range);
            }

            // Start new data animation with 300ms duration;
            chartTop.startDataAnimation(300);
        }

        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, PointValue value) {
                dateTV=(TextView)getView().findViewById(R.id.recent_infor_date_data);
                stepTV=(TextView)getView().findViewById(R.id.recent_infor_step_data);
                lengthTV=(TextView)getView().findViewById(R.id.recent_infor_length_data);
                xiaohaoTV=(TextView)getView().findViewById(R.id.recent_infor_calorie_data);
                //generateLineData(value.getColor(), 100);
                setDateText(dateTV,NowMonth,subcolumnIndex);
                setBigSmalltext(stepTV,(int)value.getY()+"","步");
                setBigSmalltext(lengthTV,String.valueOf((int)value.getY()*0.8),"米");
                setBigSmalltext(xiaohaoTV,String.valueOf((int)value.getY()*0.1),"千卡");
            }

            @Override
            public void onValueDeselected() {

                //generateLineData(ChartUtils.COLOR_GREEN, 0);

            }
        }

    }
    private static void setDateText(TextView mTv,int mon,int day){

        Spannable WordtoSpan = new SpannableString(mon+"月"+day+"日");
        int end0,end1,end2,end3;
        if(mon<10)end0=0;
        else end0=1;
        end1=end0+1;
        if(day<10)end2=end1+1;
        else end2=end1+2;
        end3=end2+1;
        WordtoSpan.setSpan(new RelativeSizeSpan((float)1), 0, end0, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        WordtoSpan.setSpan(new RelativeSizeSpan((float)0.5), end0+1, end1+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        WordtoSpan.setSpan(new RelativeSizeSpan((float)1), end1+1, end2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        WordtoSpan.setSpan(new RelativeSizeSpan((float)0.5), end2+1, end3+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mTv.setText(WordtoSpan);
    }
    private static void setBigSmalltext(TextView mTv,String big,String small){

        Spannable WordtoSpan = new SpannableString(big+small);
        int start0=0;
        int end0=big.length()-1;
        int start1=end0+1;
        int end1=start1+small.length();
        WordtoSpan.setSpan(new RelativeSizeSpan((float)1), start0, end0, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        WordtoSpan.setSpan(new RelativeSizeSpan((float)0.5), start1, end1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mTv.setText(WordtoSpan);
    }

    private static int getMonth(String s){
        int mon;
        String[] t=s.split("/");
        mon=Integer.valueOf(t[1]);
        return mon;
    }

    private static int getDay(String s){
        int mon;
        String[] t=s.split("/");
        mon=Integer.valueOf(t[2]);
        return mon;
    }
}
