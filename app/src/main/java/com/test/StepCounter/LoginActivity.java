package com.test.StepCounter;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

import com.google.gson.Gson;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.test.StepCounter.config.Constant;
import com.test.StepCounter.utils.HttpUtils;
import com.test.StepCounter.utils.LoginInfor;

public class LoginActivity extends Activity {
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;
    private EditText et_username;
    private EditText et_password;
    private TextView register;
    private String username;
    private String password;
    private Button loginbutton;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    String p=(String)msg.obj;
                    if(p.equals( "success")){
                        Toast.makeText(LoginActivity.this,"登陆成功,当前用户为"+et_username.getText().toString(), Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt("OK",1);
        bundle.putString("username",username);
        intent.setClass(LoginActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        setResult(1000,intent);
        LoginActivity.this.finish();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_username = (EditText) findViewById(R.id.username_edit);
        et_password = (EditText) findViewById(R.id.password_edit);
        register =(TextView) findViewById(R.id.register_link);
        loginbutton = (Button) findViewById(R.id.signin_button);
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>5&&et_password.getText().toString().length()>5){
                    loginbutton.setEnabled(true);
                }
                else loginbutton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>5&&et_username.getText().toString().length()>5){
                    loginbutton.setEnabled(true);
                }
                else loginbutton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Link link=new Link("注册")
                .setTextColor(Color.parseColor("#259B24"))                  // optional, defaults to holo blue
                .setTextColorOfHighlightedLink(Color.parseColor("#0D3D0C")) // optional, defaults to holo blue
                .setHighlightAlpha(.4f)                                     // optional, defaults to .15f
                .setBold(true)                                              // optional, defaults to false
                .setOnClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String clickedText) {
                        toregister();
                    }
                });
        LinkBuilder.on(register).addLink(link).build();
    }
    public void toregister() {

        Intent intent = new Intent();

        intent.setClass(LoginActivity.this, RegisterActivity.class);

        startActivityForResult(intent, 1001);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001 && resultCode == 1001)
        {
            username = data.getExtras().getString("username");
            et_username.setText(username);
            //mMyfragment.setUsername(NowUser);
        }
    }
    public void login(View view){
        username = et_username.getText().toString();
        password = et_password.getText().toString();

        if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
            Toast.makeText(this, "用户和密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            public void run(){
                try {
                    HttpUtils httpUtils=new HttpUtils();
                    String result=httpUtils.postKv(Constant.LoginUrl,Constant.LoginPost(username,password));
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
