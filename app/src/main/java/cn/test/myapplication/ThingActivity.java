package cn.test.myapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import cn.test.myapplication.date.UserThings;
import cn.test.myapplication.utils.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import mehdi.sakout.fancybuttons.FancyButton;

public class ThingActivity extends BaseActivity {
    private EditText title, content;
    private FancyButton submit;
    private String user_name="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_thing);
        isPermissionAllGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 3);
        Bmob.initialize(this, "56404633619c00d9184f451b80010918");

        Intent intent = getIntent();//获取传来的intent对象
        user_name=intent.getStringExtra("name_str");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        setListener();
    }

    private void initView() {
        title = findViewById(R.id.thing_title);
        content = findViewById(R.id.thing_content);
        submit =  findViewById(R.id.btn_spotify);
    }
    private void setListener() {
        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final String th_title = title.getText().toString();
                final String th_content = content.getText().toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                final String time = simpleDateFormat.format(date);
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);

                if (th_title.equals("") || th_content.equals("")) {
                    Toast.makeText(ThingActivity.this, "不能输入空内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserThings thingObj = new UserThings();
                thingObj.setThingTitle(th_title);
                thingObj.setThingContent(th_content);
                thingObj.setUsername(user_name);
                thingObj.setThingId(time);
                thingObj.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(ThingActivity.this, "Submit Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ThingActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ThingActivity.this, "Submit Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_newthings, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
