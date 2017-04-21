package com.test.StepCounter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v4.app.Fragment;

import com.test.StepCounter.CircleProgressView;
import com.test.StepCounter.R;

/**
 * Created by Kevin on 2016/11/20.
 * Blog:http://blog.csdn.net/student9128
 * Describe：the second fragment
 */

public class index_fragment extends Fragment {

    private int mStep=0;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.index_layout,container,false);
        final CircleProgressView cpv=(CircleProgressView)view.findViewById(R.id.CPV);
        cpv.setmTxtHint1("步数");
        cpv.setmTxtHint2("今天走了"+String.valueOf(mStep)+"步");
        cpv.setProgress(mStep);
        return view;

    }
    public static index_fragment newintance(int step) {
        Bundle bundle = new Bundle();
        bundle.putInt("arg",step);
        index_fragment mfragment=new index_fragment();
        mfragment.setArguments(bundle);
        return mfragment;
    }
}