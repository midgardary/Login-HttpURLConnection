package com.song.login_;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity{

    private String username;
    private String password;
    private String result="";
    private Handler handler;
    private String SessionId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button btn_back = (Button)findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button btn_login = (Button)findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = ((EditText)findViewById(R.id.username)).getText().toString();
                password = ((EditText)findViewById(R.id.password)).getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        login();
                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(result!= null){
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putString("result",result);

                    bundle.putString("SessionId",SessionId);

                    intent.putExtras(bundle);
                    setResult(0x11,intent);     //设置返回的结果码，并返回调用该activity的MainActivity
                    finish();           //关闭当前Activity
                }
                super.handleMessage(msg);
            }
        };
    }

    public void login(){

        String target="http://10.0.2.2:8080/login/login.jsp";

        URL url;
        try{
            url = new URL(target);

            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   //设置内容类型

            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            String param="username="+username+"&password="+password;

            outputStream.writeBytes(param);

            outputStream.flush();
            outputStream.close();

            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){

                String cookieValue = urlConnection.getHeaderField("Set-Cookie");
                SessionId = cookieValue.substring(0,cookieValue.indexOf(";"));
                Log.e("Login",SessionId);
                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  //获取输入流对象

                String inputLine=null;

                while((inputLine=bufferedReader.readLine())!=null){
                    result+=inputLine;
                }
                result="登陆请求成功！";
            }else{
                result="登陆请求失败！";
            }

            urlConnection.disconnect();

        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
