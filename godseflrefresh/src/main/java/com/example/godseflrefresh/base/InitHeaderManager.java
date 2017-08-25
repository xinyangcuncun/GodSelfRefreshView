package com.example.godseflrefresh.base;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.godseflrefresh.R;

import static android.view.View.VISIBLE;

/**
 * Created by Anson on 2017/4/26.
 */

public class InitHeaderManager extends BaseHeaderManager {

    //
    private TextView headerText;
    public InitHeaderManager(Context context) {
        super(context);
    }


    @Override
    public View getHeaderView() {
        View mHeaderView = mInflater.inflate(R.layout.ulti_header_layout, null, false);
        headerText = (TextView) mHeaderView.findViewById(R.id.header_text);
        return mHeaderView;
    }

    @Override
    public void pullViewToRefresh(int deltaY) {
        headerText.setText("下拉刷新");
    }


    @Override
    public void releaseViewToRefresh(int deltaY) {
        headerText.setText("松开刷新");
    }

    @Override
    public void headerRefreshing() {
        headerText.setText("正在刷新");
    }

    @Override
    public void headerRefreshComplete() {
        headerText.setVisibility(VISIBLE);
        headerText.setText("下拉刷新");
    }
}
