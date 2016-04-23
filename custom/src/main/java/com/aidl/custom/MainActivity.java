package com.aidl.custom;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;

import com.aidl.custom.veiw.MyLinearLayout;
import com.aidl.custom.veiw.SlidingMenu;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> data = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView menuListView = (ListView) findViewById(R.id.menu_listview);
        ListView mainListView = (ListView) findViewById(R.id.main_listview);
        SlidingMenu mSlideMenu = (SlidingMenu) findViewById(R.id.slideMenu);
        MyLinearLayout mMyLayout = (MyLinearLayout) findViewById(R.id.my_layout);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "hahah", Toast.LENGTH_LONG).show();
            }
        });
        data = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            data.add("牛肉面" + i + "碗");
        }
        mainListView.setAdapter(new MyAdapter());
        menuListView.setAdapter(new MyAdapter());
        //设置滑动状态改变的监听器
        mSlideMenu.setOnslidingMenuStateChangeListener(new SlidingMenu.onSlidingMenuStateChange() {
            @Override
            public void slideopen() {
                //打开的时候，侧边栏的效果

            }

            @Override
            public void slideclose() {
                //关闭的时候的小图标的颤抖

            }

            @Override
            public void sliding(float fraction) {
                //滑动过程中背景色的渐变过程
            }
        });
        //给自定义的布局设置侧滑菜单
        mMyLayout.setSlideMenu(mSlideMenu);


    }







    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(data.get(position));
            tv.setTextSize(28);
            tv.setTextColor(Color.RED);
            return tv;
        }
    }
}
