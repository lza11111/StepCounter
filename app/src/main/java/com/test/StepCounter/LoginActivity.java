package com.test.StepCounter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.test.StepCounter.config.Constant;
import com.test.StepCounter.utils.HttpUtils;
import com.test.StepCounter.utils.StreamTools;
import com.test.StepCounter.utils.LoginInfor;

public class LoginActivity extends Activity {
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;
    private EditText et_qq;
    private EditText et_psd;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String p=(String)msg.obj;
                    if(p.equals( "success")){
                        Toast.makeText(LoginActivity.this,"登陆成功,当前用户为"+et_qq.getText().toString(), Toast.LENGTH_LONG).show();
                        ReturnToMain();
                    }

                    if(p.equals( "failed")){
                        Toast.makeText(LoginActivity.this,"用户名/密码错误", Toast.LENGTH_LONG).show();
                    }

                    break;

                case ERROR:
                    Toast.makeText(LoginActivity.this,"登录失败,请检查网络设置", Toast.LENGTH_LONG).show();
                    break;

            }
        };
    };
    private void ReturnToMain(){
        //创建一个intent对象
        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt("OK",1);
        bundle.putString("username",et_qq.getText().toString());
        //指定原本的class和要启动的class
        intent.setClass(LoginActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        //调用另外一个新的Activity
        setResult(1000,intent);
        //startActivity(intent);
        //关闭原本的Activity
        LoginActivity.this.finish();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_qq = (EditText) findViewById(R.id.et_qq);
        et_psd = (EditText) findViewById(R.id.et_pwd);

    }

    public void login(View view){
        final String qq = et_qq.getText().toString();
        final String psd = et_psd.getText().toString();

        if(TextUtils.isEmpty(qq)||TextUtils.isEmpty(psd)){
            Toast.makeText(this, "用户和密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            public void run(){
                try {
//                    //http://localhost/xampp/android/login.php
//                    //区别1、url的路径不同
//                    String path = "http://10.0.2.2:88/xampp/login.php";
//                    URL url = new  URL(path);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    //区别2、请求方式post
//                    conn.setRequestMethod("POST");
//                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
//                    //区别3、必须指定两个请求的参数
//                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
//                    String data = "username="+qq+"&password="+psd+"&button=";
//                    Log.d("UT3",data);
//                    conn.setRequestProperty("Content-Length", data.length()+"");//数据的长度
//                    //区别4、记得设置把数据写给服务器
//                    conn.setDoOutput(true);//设置向服务器写数据
//                    byte[] bytes = data.getBytes();
//                    conn.getOutputStream().write(bytes);//把数据以流的方式写给服务器
//
//                    int code = conn.getResponseCode();
                    //System.out.println(code);

                    HttpUtils httpUtils=new HttpUtils();
                    //Log.d("UT3",data+String.valueOf(code));
                    //Log.d("UT3",);
                    String result=httpUtils.postKv(Constant.LoginUrl,Constant.LoginPost(qq,psd));
                    Log.d("UT3",result);
                    Gson gson = new Gson();
                    LoginInfor loginInfor=gson.fromJson(result, LoginInfor.class);
                    Log.d("UT3",String.valueOf(loginInfor.ret));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    if(loginInfor.ret == 1){
//                    Log.d("UT3",data);
//                        InputStream is = conn.getInputStream();
//                        String  result = StreamTools.readStream(is);
                        Message mas= Message.obtain();
                        mas.what = SUCCESS;
                        mas.obj = "success";
                        handler.sendMessage(mas);
                    }else if(loginInfor.ret == 0){
                        Message mas = Message.obtain();
                        mas.what = SUCCESS;
                        mas.obj="failed";
                        handler.sendMessage(mas);
                    }else {
                        Message mas = Message.obtain();
                        mas.what = ERROR;
                        //mas.obj="failed";
                        handler.sendMessage(mas);
                    }
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d("UT3",e.toString());
                    Message mas = Message.obtain();
                    mas.what = ERROR;
                    handler.sendMessage(mas);
                }
            }
        }.start();

    }
}
