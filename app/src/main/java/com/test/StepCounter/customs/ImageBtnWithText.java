package com.test.StepCounter.customs;

import android.widget.RelativeLayout;
import android.content.*;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import com.test.StepCounter.R;

/**
 * Created by Administrator on 2017/4/7.
 */

public class ImageBtnWithText extends RelativeLayout {
    private TextView mTv;

    public ImageBtnWithText(Context context) {
        this(context, null);
    }

    public ImageBtnWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        //在构造函数中将Xml中定义的布局解析出来。
        //LayoutInflater.from(context).inflate(R.layout.imagetext, this, true);
        //获得当前布局的子View
    }
    public void initView() {
        View view = View.inflate(getContext(),R.layout.imagetext,this);
        mTv = (TextView) view.findViewById(R.id.text_view);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void setTextViewText(String text) {
        mTv.setText(text);
    }
}
