package com.song.login_;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class MainActivity extends AppCompatActivity{

    private Button btn_fangwen;
    private Button btn_login;
    private Handler handler;
    private String result;
    private TextView resultTV;

    private String SessionId="null";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTV=(TextView)findViewById(R.id.resultTV);


        //处理访问按钮的点击事件
        btn_fangwen=(Button)findViewById(R.id.btn_fangwen);
        btn_fangwen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        access();

                        Message msg= handler.obtainMessage();   //获取一个Message
                        handler.sendMessage(msg);

                    }
                }).start();
            }
        });


        //处理登陆按钮的点击-连接到LoginActivity进行处理
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,0x11);
            }
        });


        handler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(result!= null){
                    resultTV.setText(result);
                }
                super.handleMessage(msg);
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==0x11&&resultCode==0x11){
            Bundle bundle = data.getExtras();
            result = bundle.getString("result");
            SessionId = bundle.getString("SessionId");
            resultTV.setText(result);
        }
    }

    public void access(){

        String target="http://10.0.2.2:8080/login/index.jsp";

        URL url;
        try{
            url = new URL(target);

            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(8000);
            urlConnection.setReadTimeout(8000);

            if(SessionId!="null"){
                urlConnection.setRequestProperty("Cookie",SessionId);
            }


            if(urlConnection.getResponseCode() ==HttpURLConnection.HTTP_OK){

                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


                String inputLine = null;
                while((inputLine = bufferedReader.readLine())!= null){
                    result+=inputLine+"\n";
                }
                result+="访问请求成功!";
            }else{
                result="访问请求失败!";
            }



        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
