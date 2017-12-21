package com.example.anson.godselfrefresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.godseflrefresh.GodSeflRefreshView;
import com.example.godseflrefresh.interfaces.OnFooterRefreshListener;
import com.example.godseflrefresh.interfaces.OnHeaderRefreshListener;

/**
 * Created by anson on 2017/8/25.
 */

public class SelfFragment extends Fragment {

    private GodSeflRefreshView godSeflRefreshView;
    private RecyclerView recyclerView;
    public Handler handler = new Handler();
    private int i = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.not_self,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        godSeflRefreshView = (GodSeflRefreshView) view.findViewById(R.id.god);
        godSeflRefreshView.setBaseHeaderManager(new MeituanRefreshHeaderManager(getContext()));
        recyclerView = (RecyclerView) view.findViewById(R.id.rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new QuickAdapter(40));
        //下拉刷新监听
        godSeflRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {


            @Override
            public void onHeaderRefresh(GodSeflRefreshView view) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        godSeflRefreshView.onHeaderRefreshComplete();
                        i = 0;
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
