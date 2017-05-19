package com.test.StepCounter.config;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * Created by base on 2016/1/30.
 */
public class Constant {
    public static final int MSG_FROM_CLIENT = 0;
    public static final int MSG_FROM_SERVER = 1;
    public static final int REQUEST_SERVER = 2;
    public static final int UPLOAD_FAILED = 2;

    public static final String LoginUrl= "http://120.76.142.110/Public/chuangxinshijian/?service=User.userLogin";
    public static final String RegisterUrl= "http://120.76.142.110/Public/chuangxinshijian/?service=User.UserAdd";
    public static final String getUserStepUrl = "http://120.76.142.110/Public/chuangxinshijian/?service=Step.GetUserSteps";
    public static final String pushUserStepUrl = "http://120.76.142.110/Public/chuangxinshijian/?service=Step.PushUserStep";

    public static String LoginPost(String user,String psw){
        return "username="+user+"&password="+psw;
    }

    public static String RegisterPost(String user,String psw,String phone){
        return "username="+user+"&password="+psw+"&phone="+phone;
    }

    public static String postStep(String user,String today,String step){
        return "username="+user+"&today="+today+"&step="+step;
    }
    public static String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }
    public static String getNowtime(){
        Time time = new Time(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(time);
    }
}
