package com.example.anson.godselfrefresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.godseflrefresh.GodSeflRefreshView;
import com.example.godseflrefresh.interfaces.OnFooterRefreshListener;
import com.example.godseflrefresh.interfaces.OnHeaderRefreshListener;

public class MainActivity extends AppCompatActivity {

    private GodSeflRefreshView godSeflRefreshView;
    private RecyclerView recyclerView;
    private Handler handler = new Handler();
    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        godSeflRefreshView = (GodSeflRefreshView) findViewById(R.id.god_self_refresh);
        godSeflRefreshView.setBaseHeaderManager();
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new QuickAdapter(40));
        //下拉刷新监听
        godSeflRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(GodSeflRefreshView view) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        godSeflRefreshView.onHeaderRefreshComplete();
                    }
                }, 2000);
            }
        });
        //上拉加载
        godSeflRefreshView.setBaseFooterManager();
        godSeflRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(GodSeflRefreshView view) {
                i++;
                if (i < 2) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            godSeflRefreshView.onFooterRefreshComplete();
                        }
                    }, 2000);
                } else {
                    godSeflRefreshView.onFooterRefreshOver();
                }
            }
        });
    }
}
