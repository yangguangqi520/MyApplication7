package cn.test.myapplication;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import cn.test.myapplication.date.UserThings;
import cn.test.myapplication.utils.StreamUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class HomePage extends Fragment {
    private static final String TAG ="123";
    private Context main;
    SimpleAdapter myAdapter_news,myAdapter_things;
    List<Map<String, Object>> listitem_news,listitem_things;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;

    private View view_news, view_things;
    private List<View> mViewList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<String>();
    private List<String> list_author = new ArrayList<>();
    private List<String> list_title = new ArrayList<>();
    private List<String> list_description = new ArrayList<>();
    private List<String> list_urlToImage = new ArrayList<>();
    private List<String> list_url = new ArrayList<>();
    private List<String> list_publishedAt = new ArrayList<>();
    private List<String> thing_author = new ArrayList<>();
    private List<String> thing_title = new ArrayList<>();
    private List<String> thing_id = new ArrayList<>();
    private List<String> thing_publishedAt = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_view);
        mTabLayout = view.findViewById(R.id.tabs);
        mInflater = LayoutInflater.from(main);

        isPermissionAllGranted(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 3);
        Bmob.initialize(getActivity().getApplicationContext(), "56404633619c00d9184f451b80010918");

        get_newsinfo();
        get_thingsInfo();
        view_news = mInflater.inflate(R.layout.layout_lists, null);
        listitem_news = new ArrayList<Map<String, Object>>();
        for(int i = 0;i<list_title.size()&&(list_title.size()!=0);i++){
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("list_title", list_title.get(i));
            showitem.put("list_author", list_author.get(i));
            showitem.put("list_publishedAt", list_publishedAt.get(i));
            showitem.put("list_images", returnBitMap(list_urlToImage.get(i)));
            listitem_news.add(showitem);
        }
        myAdapter_news = new SimpleAdapter(getActivity().getApplicationContext(),listitem_news, R.layout.lists_news, new String[]{"list_title","list_author","list_publishedAt","list_images"}, new int[]{R.id.news_title,R.id.news_author,R.id.news_publishedAt,R.id.news_image});
        myAdapter_news.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView iv = (ImageView) view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        ListView listView_news = (ListView) view_news.findViewById(R.id.list_test);
        listView_news.setAdapter(myAdapter_news);
//        设置监听器
        listView_news.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity().getApplicationContext(),NewsView.class);
                intent.putExtra("web_url", list_url.get(arg2));
                startActivity(intent);
            }
        }) ;

        view_things = mInflater.inflate(R.layout.layout_lists, null);
        listitem_things = new ArrayList<Map<String, Object>>();
        for(int i = 0;i<thing_title.size()&&(thing_title.size()!=0);i++){
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("thing_title", thing_title.get(i));
            showitem.put("thing_author", thing_author.get(i));
            showitem.put("thing_publishedAt", thing_publishedAt.get(i));
            listitem_things.add(showitem);
        }
        myAdapter_things = new SimpleAdapter(getActivity().getApplicationContext(),listitem_things, R.layout.lists_things, new String[]{"thing_title","thing_author","thing_publishedAt"}, new int[]{R.id.things_title,R.id.things_author,R.id.things_at});
        ListView listView_things = (ListView) view_things.findViewById(R.id.list_test);
        listView_things.setAdapter(myAdapter_things);
//        设置监听器
        listView_things.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity().getApplicationContext(),ThingView.class);
                intent.putExtra("things_id",thing_id.get(arg2));
                startActivity(intent);
            }
        }) ;
        //添加页卡视图
        mViewList.add(view_news);
        mViewList.add(view_things);
        //添加页卡标题
        mTitleList.add("新闻");
        mTitleList.add("周边新鲜事");

        //添加tab选项卡，默认第一个选中
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)), true);

        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);

        //给ViewPager设置适配器
        mViewPager.setAdapter(mAdapter);
        //将TabLayout和ViewPager关联起来
        mTabLayout.setupWithViewPager(mViewPager);
        //给Tabs设置适配器
        mTabLayout.setTabsFromPagerAdapter(mAdapter);

        return view;
    }
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            main = (Context) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private void get_newsinfo(){
        list_author.clear();
        list_title.clear ();
        list_description.clear();
        list_urlToImage.clear ();
        list_url.clear();
        list_publishedAt.clear();
        new Thread(){
            public void run() {
                try {
                    URL url = new URL("https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=ce81253c97924a7e8f99539c64cc130b");
                    HttpURLConnection openConnection = (HttpURLConnection) url
                            .openConnection();
                    openConnection.setConnectTimeout(40000);
                    openConnection.setReadTimeout(40000);
                    int responseCode = openConnection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = openConnection
                                .getInputStream();
                        String parseSteam = StreamUtils.parseSteam(inputStream);
                        JSONObject jsonObject = new JSONObject(parseSteam);
                        JSONArray jsonArray = jsonObject.getJSONArray("articles");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String author = jsonObject1.getString("author");
                            String title = jsonObject1.getString("title");
                            String description = jsonObject1.getString("description");
                            String urlToImage = jsonObject1.getString("urlToImage");
                            String new_url = jsonObject1.getString("url");
                            String publishedAt = jsonObject1.getString("publishedAt");
                            list_author.add(author);
                            list_title.add(title);
                            list_description.add(description);
                            list_urlToImage.add(urlToImage);
                            list_url.add(new_url);
                            list_publishedAt.add(publishedAt);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                for(int i = 0;i<list_title.size();i++){
                    Map<String, Object> showitem = new HashMap<String, Object>();
                    showitem.put("list_title", list_title.get(i));
                    showitem.put("list_author", list_author.get(i));
                    showitem.put("list_publishedAt", list_publishedAt.get(i));
                    showitem.put("list_images", returnBitMap(list_urlToImage.get(i)));

                    listitem_news.add(showitem);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
    private void get_thingsInfo(){
        thing_author.clear();
        thing_title.clear ();
        thing_id.clear();
        BmobQuery<UserThings> queryall = new BmobQuery<UserThings>();
        queryall.findObjects(new FindListener<UserThings>() {
            @Override
            public void done(List<UserThings> list, BmobException e) {
                if (e == null) {
                    for (int i =0;i<list.size();i++){
                        thing_id.add(list.get(i).getThingId());
                        thing_author.add(list.get(i).getName());
                        thing_title.add(list.get(i).getThingTitle());
                        thing_publishedAt.add(list.get(i).getCreatedAt());
                    }
                }
                for(int i = 0;i<thing_title.size()&&(thing_title.size()!=0);i++){
                    Map<String, Object> showitem = new HashMap<String, Object>();
                    showitem.put("thing_title", thing_title.get(i));
                    showitem.put("thing_author", thing_author.get(i));
                    showitem.put("thing_publishedAt", thing_publishedAt.get(i));
                    listitem_things.add(showitem);
                }
                myAdapter_things.notifyDataSetChanged();
            }
        });
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    myAdapter_news.notifyDataSetChanged();
                break;
            }
        }
    };
    public Bitmap returnBitMap(String url) {
        Resources res = HomePage.this.getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.qq);
        if (!url.endsWith("jpg"))
            return bmp;
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return bmp;
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return bmp;
        }
        return bitmap;
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
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
