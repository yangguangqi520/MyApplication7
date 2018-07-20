package cn.test.myapplication;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import cn.test.myapplication.login.login;


public class MainActivity extends AppCompatActivity {
    private String[] items = {"新闻首页", "发表新鲜事", "定位导航", "天气预报","退出用户"};
    private HomePage doctor = new HomePage();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();//获取传来的intent对象
        final String user_name=intent.getStringExtra("name_str");
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mToggle.syncState();

        mDrawerLayout.addDrawerListener(mToggle);

        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.ll_content, doctor, "apple")
                .show(doctor)
                .commit();

        ListView lv_drawer = (ListView) findViewById(R.id.lv_drawer);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv_drawer.setAdapter(mAdapter);
        final LinearLayout ll_drawer = (LinearLayout) findViewById(R.id.ll_drawer);
        final android.support.v7.widget.Toolbar finalToolbar = toolbar;
        lv_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchFragment(position);
            }
            public void switchFragment(int fragmentId) {
                mDrawerLayout.closeDrawer(ll_drawer);
                int currentFragmentId = 5;
                if(currentFragmentId == fragmentId)
                    return;
                currentFragmentId = fragmentId;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (fragmentId)
                {
                    case 0:
                        fragmentTransaction.replace(R.id.ll_content, new HomePage());
                        finalToolbar.setTitle("新闻首页");
                        break;
                    case 1:
                        Intent intent_newThings = new Intent(MainActivity.this,ThingActivity.class);
                        intent_newThings.putExtra("name_str", user_name);
                        startActivity(intent_newThings);
                        finalToolbar.setTitle("发表新鲜事");
                        break;
                    case 2:
                        Intent intent_MapsActivity = new Intent(MainActivity.this,MapsActivity.class);
                        startActivity(intent_MapsActivity);
                        finalToolbar.setTitle("定位导航");
                        break;
                    case 3:
                        Intent intent_DefaultActivity = new Intent(MainActivity.this,WeatherActivity.class);
                        startActivity(intent_DefaultActivity);
                        finalToolbar.setTitle("天气预报");
                        break;
                    case 4:
                        Intent intent_exit = new Intent(MainActivity.this,login.class);
                        startActivity(intent_exit);
                        break;
                }
                fragmentTransaction.commit();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_view, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                Toast.makeText(this, "定位", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
