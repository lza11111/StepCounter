package com.test.StepCounter.fragment;

import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2016/11/20.
 * Blog:http://blog.csdn.net/student9128
 * Describe：the first fragment
 */

public class recent_fragment extends Fragment {
    private String DB_NAME = "StepCounter";
    private static List<StepData> list;
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
        if (MainActivity.NowUser.equals("ID")) {
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
                        //Log.d("UT3",stepInfor.data);
                        //list =new ArrayList<StepData>();
                        int len = stepInfor.data.size();
                        for (int i = 0; i < len; i++)
                            Log.d("UT3", stepInfor.data.get(i).getToday());
                        list = new ArrayList<StepData>(stepInfor.data);
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

        private LineChartView chartTop;
        private ColumnChartView chartBottom;

        private LineChartData lineData;
        private ColumnChartData columnData;

        private TextView dateTV, stepTV, lengthTV, xiaohaoTV;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.recent_layout, container, false);

            // *** TOP LINE CHART ***
            //chartTop = (LineChartView) rootView.findViewById(R.id.chart_top);

            // Generate and set data for line chart
            //generateInitialLineData();

            // *** BOTTOM COLUMN CHART ***

            chartBottom = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);

            generateColumnData();

            return rootView;
        }

        private void generateColumnData() {

            int numSubcolumns = 1;
            int numColumns = list.size();//months.length;
            Log.d("UT3", String.valueOf(numColumns));
            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<Column> columns = new ArrayList<Column>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {

                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue((float) Integer.valueOf(list.get(i).getStep()), ChartUtils.pickColor()));
                }

                axisValues.add(new AxisValue(i).setLabel(list.get(i).getToday()));

                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
            }

            columnData = new ColumnChartData(columns);

            columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

            chartBottom.setColumnChartData(columnData);

            // Set value touch listener that will trigger changes for chartTop.
            chartBottom.setOnValueTouchListener(new ValueTouchListener());

            // Set selection mode to keep selected month column highlighted.
            chartBottom.setValueSelectionEnabled(true);

            chartBottom.setZoomType(ZoomType.HORIZONTAL);

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
            Viewport v = new Viewport(0, 110, 6, 0);
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

        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                dateTV=(TextView)getView().findViewById(R.id.recent_infor_date_data);
                stepTV=(TextView)getView().findViewById(R.id.recent_infor_step_data);
                lengthTV=(TextView)getView().findViewById(R.id.recent_infor_length_data);
                xiaohaoTV=(TextView)getView().findViewById(R.id.recent_infor_calorie_data);
                //generateLineData(value.getColor(), 100);
                setBigSmalltext(dateTV,list.get(columnIndex).getToday(),"日");
                setBigSmalltext(stepTV,list.get(columnIndex).getStep(),"步");
                setBigSmalltext(lengthTV,String.valueOf(Integer.valueOf(list.get(columnIndex).getStep())*0.8),"米");
                setBigSmalltext(xiaohaoTV,String.valueOf(Integer.valueOf(list.get(columnIndex).getStep())*0.1),"千卡");
            }

            @Override
            public void onValueDeselected() {

                //generateLineData(ChartUtils.COLOR_GREEN, 0);

            }
        }

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
}
