package com.test.StepCounter.config;

/**
 * Created by base on 2016/1/30.
 */
public class Constant {
    public static final int MSG_FROM_CLIENT = 0;
    public static final int MSG_FROM_SERVER = 1;
    public static final int REQUEST_SERVER = 2;
    public static final String LoginUrl= "http://10.0.2.2:88/xampp/PhalApi/Public/chuangxinshijian/?service=User.userLogin";
    public static final String getUserStepUrl = "http://10.0.2.2:88/xampp/PhalApi/Public/chuangxinshijian/?service=User.getUserStep";
    public static String LoginPost(String user,String psw){
        return "username="+user+"&password="+psw;
    }
}
