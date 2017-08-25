package com.example.godseflrefresh.base;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.godseflrefresh.R;


/**
 * Created by Anson on 2017/4/29.
 */

public class InitFooterManager extends BaseFooterManager {
    private TextView headerText;

    public InitFooterManager(Context context) {
        super(context);
    }

    @Override
    public View getFooterView() {
        View mFooterView = mInflater.inflate(R.layout.ulti_footer_layout, null, false);
        headerText = (TextView) mFooterView.findViewById(R.id.footer_text);
        return mFooterView;
    }

    @Override
    public void pullViewToRefresh(int deltaY) {
        headerText.setText("上拉加载");
    }


    @Override
    public void releaseViewToRefresh(int deltaY) {
        headerText.setText("释放加载");
    }

    @Override
    public void footerRefreshing() {
        headerText.setText("正在加载");
    }

    @Override
    public void footerRefreshComplete() {
        headerText.setVisibility(View.VISIBLE);
        headerText.setText("上拉加载");
    }

    @Override
    public void footerRefreshOver() {
        headerText.setText("已经到底了");
    }

    @Override
    public void footerRefreshNext() {

    }
}
