package cn.test.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import cn.test.myapplication.R;

public class welcome extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGHT = 3000;  //延迟3秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_welcome);

//        开启线程等待3秒跳转到login界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                跳转界面
                Intent intent = new Intent(welcome.this, login.class);
                welcome.this.startActivity(intent);
                welcome.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
