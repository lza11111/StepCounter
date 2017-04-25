package com.test.StepCounter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.StepCounter.MainActivity;
import com.test.StepCounter.R;

/**
 * Created by Kevin on 2016/11/20.
 * Blog:http://blog.csdn.net/student9128
 * Describe：the third fragment
 */

public class my_fragment extends Fragment {
    TextView mTV;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_layout,container,false);
        mTV=(TextView) view.findViewById(R.id.TV_userID);
        if(mTV==null) Log.d("UT3","null");
        Log.d("UT3","show");
        mTV.setText(MainActivity.NowUser);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        mTV.setText(MainActivity.NowUser);
    }

}
