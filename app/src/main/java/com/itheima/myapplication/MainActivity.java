package com.itheima.myapplication;

import android.annotation.TargetApi;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
private RefreashListView listview;
    private ArrayList<String> listdatas;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        listview= (RefreashListView) findViewById(R.id.listview);

        listview.setRefreshListener(new RefreashListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(){
                    public void run(){
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        listdatas.add(0,"我是下拉刷新的数据");
                        runOnUiThread(new Runnable(){
                            //@TargetApi(Build.VERSION_CODES.N)
                            //@RequiresApi(api = Build.VERSION_CODES.N)
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                listview.onRefreshComplete();
                            }
                        });
                    }
                }.start();

            }
        });
        listdatas = new ArrayList<>();
        for(int i=0;i<30;i++){
            listdatas.add("哈哈:"+i);
        }
        mAdapter = new MyAdapter();
        listview.setAdapter(mAdapter);


    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listdatas.size();
        }

        @Override
        public Object getItem(int i) {
            return listdatas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView=new TextView(viewGroup.getContext());
            textView.setTextSize(25f);
            textView.setText(listdatas.get(i));
            return textView;
        }

    }

}
