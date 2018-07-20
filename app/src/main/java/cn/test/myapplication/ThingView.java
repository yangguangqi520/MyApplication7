package cn.test.myapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import cn.test.myapplication.date.UserComment;
import cn.test.myapplication.date.UserThings;
import cn.test.myapplication.utils.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.test.myapplication.utils.SharedPreferencesHelper;

public class ThingView extends BaseActivity implements View.OnClickListener {
    private boolean isshow = false;
    private TextView th_title,th_author,th_at,th_content,au_comment,te_comment;
    private EditText comment;
    private BootstrapButton sub_comment;
    private String things_id = "";
    private List<String> list_name = new ArrayList<>();
    private List<String> list_comment = new ArrayList<>();
    List<Map<String, Object>> listitem;
    SimpleAdapter myAdapter;
    ListView listView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thing_view);

        isPermissionAllGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 3);
        Bmob.initialize(this, "56404633619c00d9184f451b80010918");

        Intent intent = getIntent();//获取传来的intent对象
        things_id=intent.getStringExtra("things_id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        setinfo();
        initEvent();

        listitem = new ArrayList<Map<String, Object>>();
        myAdapter = new SimpleAdapter(this,listitem, R.layout.layout_comment, new String[]{"list_name","list_comment"}, new int[]{R.id.au_comment,R.id.te_comment});
        listView = (ListView)findViewById(R.id.list_th);
        listView.setAdapter(myAdapter);
    }
    private void initView(){
        th_title = findViewById(R.id.th_title);
        th_author = findViewById(R.id.th_author);
        th_at = findViewById(R.id.th_at);
        th_content = findViewById(R.id.th_content);
        comment = findViewById(R.id.comment);
        sub_comment = findViewById(R.id.sub_comment);
        au_comment = findViewById(R.id.au_comment);
        te_comment = findViewById(R.id.te_comment);
    }
    private void setinfo(){
        list_name.clear();
        list_comment.clear();
        BmobQuery<UserThings> query = new BmobQuery<UserThings>();
        query.addWhereEqualTo("thingId", things_id);
        query.findObjects(new FindListener<UserThings>() {
            @Override
            public void done(List<UserThings> list, BmobException e) {
                if (e == null) {
                    for (UserThings userthing : list) {
                        th_title.setText(userthing.getThingTitle());
                        th_author.setText(userthing.getName());
                        th_content.setText(userthing.getThingContent());
                        th_at.setText(userthing.getThingId());
                    }
                }
            }
        });
        BmobQuery<UserComment> queryall = new BmobQuery<UserComment>();
        queryall.findObjects(new FindListener<UserComment>() {
            @Override
            public void done(List<UserComment> list, BmobException e) {
                if (e == null) {
                    for (int i =0;i<list.size();i++){
                        if (list.get(i).getThingId().equals(things_id)){
                            list_name.add(list.get(i).getName());
                            list_comment.add(list.get(i).getComment());
                        }
                    }
                }
                listitem.clear();
                for(int i = 0;i<list_name.size();i++){
                    Map<String, Object> showitem = new HashMap<String, Object>();
                    showitem.put("list_name", list_name.get(i));
                    showitem.put("list_comment", list_comment.get(i));
                    listitem.add(showitem);
                }
                myAdapter.notifyDataSetChanged();
            }

        });



    }
    private void initEvent(){
        comment.setOnClickListener(this);
        sub_comment.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment:
//                if (!isshow){
//                    sub_comment.setVisibility(View.VISIBLE);
//                    isshow = !isshow;
//                }else{
//                    sub_comment.setVisibility(View.GONE);
//                    isshow = !isshow;
//                }
                break;
            case R.id.sub_comment:
                String user_comment = comment.getText().toString();
                if (user_comment.equals("")) {
                    Toast.makeText(ThingView.this, "不能传入空值", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserComment commentObj = new UserComment();
                String author_name = "";
                author_name = (String) new SharedPreferencesHelper(ThingView.this).getData("author","姓名");
                commentObj.setComment(user_comment);
                commentObj.setUsername(author_name);
                commentObj.setThingId(things_id);
                commentObj.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            setinfo();
                            comment.setText(null);
                            Toast.makeText(ThingView.this, "Submit Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ThingView.this, "Submit Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
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
