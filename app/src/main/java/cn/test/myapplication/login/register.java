package cn.test.myapplication.login;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.test.myapplication.R;
import cn.test.myapplication.date.user;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class register extends AppCompatActivity {

    private FloatingActionButton fab;
    private CardView cvAdd;
    private EditText et_username, et_password, et_repeatpassword;
    private Button bt_go;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register);

        isPermissionAllGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 3);
        Bmob.initialize(this, "56404633619c00d9184f451b80010918");

        ShowEnterAnimation();
        initView();
        setListener();
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }

    private void setListener() {
        bt_go.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                String name_str = et_username.getText().toString();
                String pw_str = et_password.getText().toString();
                String repw_str = et_repeatpassword.getText().toString();
                if (name_str.equals("") || pw_str.equals("")||repw_str.equals("")) {
                    Toast.makeText(register.this, "不能传入空字符", Toast.LENGTH_SHORT).show();
                    return;
                } else if(!pw_str.equals(repw_str)){
                    Toast.makeText(register.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                user userObj = new user();
                userObj.setUsername(name_str);
                userObj.setPassword(pw_str);
                userObj.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(register.this, "Submit Success", Toast.LENGTH_SHORT).show();
                            animateRevealClose();
                        } else {
                            Toast.makeText(register.this, "Submit Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        fab = findViewById(R.id.register_fab);
        cvAdd = findViewById(R.id.cv_add);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_repeatpassword = findViewById(R.id.et_repeatpassword);
        bt_go = findViewById(R.id.bt_go);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                register.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        animateRevealClose();
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
