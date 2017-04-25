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

import com.google.gson.Gson;
import com.test.StepCounter.config.Constant;
import com.test.StepCounter.utils.HttpUtils;
import com.test.StepCounter.utils.LoginInfor;

public class RegisterActivity extends Activity {
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;
    private EditText registerUser;
    private EditText registerpsw;
    private EditText registerconfirm;
    private EditText registerphone;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String p=(String)msg.obj;
                    if(p.equals( "success")){
                        Toast.makeText(RegisterActivity.this,"注册成功！", Toast.LENGTH_LONG).show();
                        ReturnToLogin();
                    }

                    if(p.equals( "failed1")){
                        Toast.makeText(RegisterActivity.this,"用户名已存在！", Toast.LENGTH_LONG).show();
                    }

                    break;


                case ERROR:
                    Toast.makeText(RegisterActivity.this,"网络错误,请检查网络设置", Toast.LENGTH_LONG).show();
                    break;

            }
        };
    };
    private void ReturnToLogin(){
        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt("OK",1);
        bundle.putString("username",registerUser.getText().toString());
        intent.setClass(RegisterActivity.this, LoginActivity.class);
        intent.putExtras(bundle);
        setResult(1001,intent);
        RegisterActivity.this.finish();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerUser = (EditText) findViewById(R.id.register_username_edit);
        registerpsw = (EditText) findViewById(R.id.register_password_edit);
        registerconfirm = (EditText) findViewById(R.id.register_confirm_edit);
        registerphone = (EditText) findViewById(R.id.register_phone_edit);
    }

    public void login(View view){
        final String username = registerUser.getText().toString();
        final String password = registerpsw.getText().toString();
        final String confirm = registerconfirm.getText().toString();
        final String phonenum = registerphone.getText().toString();
        if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if(username.length()<5||username.length()>15){
            Toast.makeText(this, "用户名长度错误！", Toast.LENGTH_LONG).show();
            return;
        }
        if(!confirm.equals(password)){
            Toast.makeText(this, "两次输入的密码不一致！", Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length()<8||username.length()>16){
            Toast.makeText(this, "密码长度错误！", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            public void run(){
                try {
                    HttpUtils httpUtils=new HttpUtils();
                    String result=httpUtils.postKv(Constant.RegisterUrl,Constant.RegisterPost(username,password,phonenum));
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
