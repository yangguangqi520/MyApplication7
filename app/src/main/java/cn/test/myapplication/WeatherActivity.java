package cn.test.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import cn.test.myapplication.utils.StreamUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "12345";
    private List<String> list_date = new ArrayList<>();
    private List<String> list_cond_txt_d = new ArrayList<>();
    private List<String> list_tmp_max = new ArrayList<>();
    private MaterialSearchView searchView;
    private String city_name = null;
//    定义控件
    private TextView now_temperature;
    private TextView now_city;
    private TextView now_weather;
    private TextView now_city2;

    private TextView today_temperature;
    private TextView tomorrow_temperature;
    private TextView third_temperature;
    private TextView today_weather;
    private TextView tomorrow_weather;
    private TextView third_weather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_weather);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //不推荐的做法；
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        init();
        get_information(city_name);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(R.id.container), "Query123: " + query, Snackbar.LENGTH_LONG)
                        .show();
                //不推荐的做法；
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                get_information(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }
            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
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
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void get_information(final String location){
        city_name = location;
        new Thread(){
            public void run() {
                try {
                    URL url = new URL("https://free-api.heweather.com/s6/weather/forecast?key=6e2cadce7c004a3e88a9c986f40937e1&location="+location);
                    HttpURLConnection openConnection = (HttpURLConnection) url
                            .openConnection();
                    openConnection.setConnectTimeout(5000);
                    openConnection.setReadTimeout(5000);
                    int responseCode = openConnection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = openConnection
                                .getInputStream();
                        String parseSteam = StreamUtils.parseSteam(inputStream);
                        JSONObject jsonObject = new JSONObject(parseSteam);
                        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                        JSONObject allJsonObject = jsonArray.getJSONObject(0);
                        String daily_forecast = allJsonObject.getString("daily_forecast");
                        JSONArray jsonArray_daily_forecast = new JSONArray(daily_forecast);
                        for (int i = 0; i < jsonArray_daily_forecast.length(); i++) {
                            JSONObject jsonObject1 = jsonArray_daily_forecast.getJSONObject(i);
                            String date = jsonObject1.getString("date");
                            String cond_txt_d = jsonObject1.getString("cond_txt_d");
                            String tmp_max = jsonObject1.getString("tmp_max");
                            list_date.add(date);
                            list_cond_txt_d.add(cond_txt_d);
                            list_tmp_max.add(tmp_max);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }
    private void init(){
        city_name = "加的夫";
        now_temperature = findViewById(R.id.now_temperature);
        now_city = findViewById(R.id.now_city);
        now_city2 = findViewById(R.id.now_city2);
        now_weather = findViewById(R.id.now_weather);
        today_temperature = findViewById(R.id.today_temperature);
        tomorrow_temperature = findViewById(R.id.tomorrow_temperature);
        third_temperature = findViewById(R.id.third_temperature);
        today_weather = findViewById(R.id.today_weather);
        tomorrow_weather = findViewById(R.id.tomorrow_weather);
        third_weather = findViewById(R.id.third_weather);
    }
    private void set_information(){
        today_temperature.setText(list_tmp_max.get(0));
        tomorrow_temperature.setText(list_tmp_max.get(1));
        third_temperature.setText(list_tmp_max.get(2));

        today_weather.setText(list_cond_txt_d.get(0));
        tomorrow_weather.setText(list_cond_txt_d.get(1));
        third_weather.setText(list_cond_txt_d.get(2));

        now_temperature.setText(list_tmp_max.get(0));
        now_city.setText(city_name);
        now_weather.setText(list_cond_txt_d.get(0));
        now_city2.setText(city_name);

        list_tmp_max.clear();
        list_cond_txt_d.clear ();
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    set_information();
                break;
            }
        }
    };

}
