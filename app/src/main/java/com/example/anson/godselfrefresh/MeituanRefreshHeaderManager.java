package com.example.anson.godselfrefresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import com.example.godseflrefresh.base.BaseHeaderManager;
import com.example.godseflrefresh.tools.MeasureTools;

/**
 * Created by anson on 2017/8/25.
 */

public class MeituanRefreshHeaderManager extends BaseHeaderManager {
    private ImageView loading;
    private int viewHeight;
    private float pull_distance=0;

    public MeituanRefreshHeaderManager(Context context) {
        super(context);
    }

    @Override
    public View getHeaderView() {
        View mView = mInflater.inflate(R.layout.meituan_header_refresh_layout, null, false);
        loading = (ImageView) mView.findViewById(R.id.loading);
        MeasureTools.measureView(mView);
        viewHeight = mView.getMeasuredHeight();
        return mView;
    }

    @Override
    public void pullViewToRefresh(int deltaY) {
        //这里乘以0.3 是因为UltimateRefreshView 源码中对于滑动有0.3的阻尼系数，为了保持一致
        pull_distance=pull_distance+deltaY*0.3f;
        float scale = pull_distance / viewHeight;
        loading.setScaleX(scale);
        loading.setScaleY(scale);
    }

    @Override
    public void releaseViewToRefresh(int deltaY) {
        loading.setImageResource(R.drawable.mei_tuan_loading_pre);
        AnimationDrawable mAnimationDrawable= (AnimationDrawable) loading.getDrawable();
        mAnimationDrawable.start();
    }

    @Override
    public void headerRefreshing() {
        loading.setImageResource(R.drawable.mei_tuan_loading);
        AnimationDrawable mAnimationDrawable= (AnimationDrawable) loading.getDrawable();
        mAnimationDrawable.start();
    }

    @Override
    public void headerRefreshComplete() {
        loading.setImageResource(R.drawable.pull_image);
        loading.setScaleX(0);
        loading.setScaleY(0);
        pull_distance=0;
    }
}
