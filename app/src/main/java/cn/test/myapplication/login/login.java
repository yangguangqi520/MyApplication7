package cn.test.myapplication.login;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.test.myapplication.MainActivity;
import cn.test.myapplication.R;
import cn.test.myapplication.date.user;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.test.myapplication.utils.SharedPreferencesHelper;

public class login extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private CardView cv;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
//        bmob云数据预处理。
        isPermissionAllGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 3);
        Bmob.initialize(this, "56404633619c00d9184f451b80010918");
//      获得控件id。
        initView();
//        设置监听器。
        setListener();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        cv = findViewById(R.id.cv);
        fab = findViewById(R.id.login_fab);
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final String name_str = etUsername.getText().toString();
                final String pw_str = etPassword.getText().toString();
                final String[] query_str = {""};
                Explode explode = new Explode();
                explode.setDuration(500);
                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
//              BmobQuery工具查询bmob数据。
                BmobQuery<user> query = new BmobQuery<user>();
                query.addWhereEqualTo("username", name_str);
                query.findObjects(new FindListener<user>() {
                    @Override
                    public void done(List<user> list, BmobException e) {
                        if (e == null) {
                            String str = "";
                            for (user user : list) {
                                str = user.getPassword();
                            }
                            query_str[0] = str;
                        }
                        if (query_str[0].equals(pw_str)){
//                            利用SharedPreferences类系统保存数据。这里保存了作者姓名，保存之后，在接下来的几个类里都可以调用这个数据。
                            SharedPreferencesHelper sp = new SharedPreferencesHelper(login.this);
                            sp.clearData();
                            sp.saveData("author",name_str);
                            etUsername.setText(null);
                            etPassword.setText(null);
//                              动画跳转，但是方式太老，这里暂时注释
//                            ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(login.this);
//                            Intent intent = new Intent(login.this,MainActivity.class);
//                            intent.putExtra("name_str", name_str);
//                            startActivity(intent, oc2.toBundle());
//                            界面跳转inint
                            Intent intent = new Intent(login.this,MainActivity.class);
                            intent.putExtra("name_str", name_str);
                            startActivity(intent);
                        }else{
                            Toast.makeText(login.this, "用户名密码不一致", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login.this, fab, fab.getTransitionName());
                startActivity(new Intent(login.this, register.class), options.toBundle());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    protected void isPermissionAllGranted(String[] permissions, int requestCode) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return ;
        }

        //获得批量请求但被禁止的权限列表
        List<String> deniedPerms = new ArrayList<String>();
        for(int i = 0; permissions != null && i < permissions.length;i++){
            if (!hasPermission(new String[]{permissions[i]})) {
                deniedPerms.add(permissions[i]);
            }
        }
        //进行批量请求
        int denyPermNum = deniedPerms.size();
        if(denyPermNum != 0){
            requestPermissions(deniedPerms.toArray(new String[denyPermNum]), requestCode);
        }
    }
    public boolean hasPermission(String[] permissions) {

        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
