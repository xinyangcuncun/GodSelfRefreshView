package com.example.godseflrefresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.godseflrefresh.base.BaseFooterManager;
import com.example.godseflrefresh.base.BaseHeaderManager;
import com.example.godseflrefresh.base.InitFooterManager;
import com.example.godseflrefresh.base.InitHeaderManager;
import com.example.godseflrefresh.interfaces.OnFooterRefreshListener;
import com.example.godseflrefresh.interfaces.OnHeaderRefreshListener;
import com.example.godseflrefresh.tools.MeasureTools;

/**
 * Created by anson on 2017/8/25.
 */

public class GodSeflRefreshView extends LinearLayout {

    private static final String TAG = GodSeflRefreshView.class.getSimpleName();
    // 刷新时状态
    private static final int PULL_TO_REFRESH = 2; // 下拉刷新  上拉加载
    private static final int RELEASE_TO_REFRESH = 3; // 释放下拉刷新  上方上拉加载
    private static final int REFRESHING = 4; // 正在刷新的状态
    private static final int PULL_TO_OVER = 5; // 上拉加载完成（没有数据的时候）
    private static final int PULL_TO_NORMOl = -1; // 上拉加载完成（没有数据的时候）
    // pull state
    private static final int PULL_UP_STATE = 0; // 判断是否是尾部
    private static final int PULL_DOWN_STATE = 1;  //判断是否是头部
    // 滑动最小灵敏度  大于此距离时交给滑动
    private static final int MIN_SCROLL_PX = 3;
    private int mPullState;   // 用于判断是尾部还是头部

    /**
     * list or grid
     */
    private AdapterView<?> mAdapterView;
    /**
     * RecyclerView
     */
    private RecyclerView mRecyclerView;
    /**
     * ScrollView
     */
    private ScrollView mScrollView;
    /**
     * WebView
     */
    private WebView mWebView;


    //Header
    private int mHeaderState; // 头部目前的状态
    private View mHeaderView; //头部布局
    private int mHeadViewHeight; // 头部高度
    //Footer
    private int mFooterState; // 底部目前的状态
    private View mFooterView; // 底部布局
    private int mFooterViewHeight; // 底部的高度
    //action
    private int lastY;

    private BaseHeaderManager mBaseHeaderManager;
    private BaseFooterManager mBaseFooterManager;
    private OnHeaderRefreshListener mOnHeaderRefreshListener;
    private OnFooterRefreshListener mOnFooterRefreshListener;

    private Context mContext;

    /**
     * 一个参数的构造：用于外部new调用
     * @param context
     */
    public GodSeflRefreshView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 两个参数的构造：用于在xml文件中定义
     * @param context
     * @param attrs
     */
    public GodSeflRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 按个参数的构造：用于xml定义且可以添加style属性
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public GodSeflRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //设置为垂直布局，避免每次在xml中修改(LinearLayout 默认为horizontal)
        setOrientation(VERTICAL);
        mContext = context;

    }


    public void setBaseHeaderManager(BaseHeaderManager baseHeaderAdapter) {
        mBaseHeaderManager = baseHeaderAdapter;
        initHeaderView();
        initSubViewType();
    }

    public void setBaseHeaderManager() {
        mBaseHeaderManager = new InitHeaderManager(mContext);
        initHeaderView();
        initSubViewType();
    }

    public void setBaseFooterManager(BaseFooterManager baseFooterManager) {
        mBaseFooterManager = baseFooterManager;
        initFooterView();
    }

    public void setBaseFooterManager() {
        mBaseFooterManager = new InitFooterManager(mContext);
        initFooterView();
    }

    /**
     * 计算顶部view 高度，将其隐藏
     */
    private void initHeaderView() {
        mHeaderView = mBaseHeaderManager.getHeaderView();
        MeasureTools.measureView(mHeaderView);
        mHeadViewHeight = mHeaderView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mHeadViewHeight);
        params.topMargin = -mHeadViewHeight;
        addView(mHeaderView, 0, params);
    }

    /**
     * 计算底部view的高度
     */
    private void initFooterView() {
        mFooterView = mBaseFooterManager.getFooterView();
        MeasureTools.measureView(mFooterView);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                mFooterViewHeight);
        addView(mFooterView, params);
    }


    /**
     * 确定UltimateRefreshView 内部子视图类型
     */
    private void initSubViewType() {
        int count = getChildCount();
        if (count < 2) {
            return;
        }

        View view = getChildAt(1);

        if (view instanceof AdapterView<?>) {
            mAdapterView = (AdapterView<?>) view;
        }

        if (view instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) view;
        }

        if (view instanceof ScrollView) {
            mScrollView = (ScrollView) view;
        }

        if (view instanceof WebView) {
            mWebView = (WebView) view;
        }

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - lastY;
                if (Math.abs(deltaY) > MIN_SCROLL_PX) {
                    if (isParentViewScroll(deltaY)) {
                        return true; //此时,触发onTouchEvent事件
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - lastY;
                if (mPullState == PULL_DOWN_STATE) {
                    if (getNowRefreshState()) break;
                    initHeaderViewToRefresh(deltaY);
                } else if (mPullState == PULL_UP_STATE) {
                    if (getNowRefreshState()) break;
                    initFooterViewToRefresh(deltaY);
                }
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int topMargin = getHeaderTopMargin();
                if (mPullState == PULL_DOWN_STATE) {
                    if (getNowRefreshState()) break;
                    if (topMargin >= 0) {
                        headerRefreshing();
                    } else {
                        reSetHeaderTopMargin(-mHeadViewHeight);
                    }
                } else if (mPullState == PULL_UP_STATE) {
                    if (getNowRefreshState()) break;
                    if (Math.abs(topMargin) >= mHeadViewHeight
                            + mFooterViewHeight) {
                        // 开始执行footer 刷新
                        footerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        reSetHeaderTopMargin(-mHeadViewHeight);
                    }
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private boolean getNowRefreshState() {
        if (mFooterState == REFRESHING || mHeaderState == REFRESHING) {
            return true;
        }
        return false;
    }

    /**
     * 根据deltaY 处理 是下拉刷新  还是 释放刷新
     * @param deltaY
     */
    private void initHeaderViewToRefresh(int deltaY) {
        if (mBaseHeaderManager == null) {
            return;
        }
        int topDistance = UpdateHeadViewMarginTop(deltaY);
        if (topDistance < 0 && topDistance > -mHeadViewHeight) {
            mBaseHeaderManager.pullViewToRefresh(deltaY);
            mHeaderState = PULL_TO_REFRESH;
        } else if (topDistance > 0 && mHeaderState != RELEASE_TO_REFRESH) {
            mBaseHeaderManager.releaseViewToRefresh(deltaY);
            mHeaderState = RELEASE_TO_REFRESH;
        }

    }

    /**
     * 根据deltaY 处理 是上拉加载  还是 释放加载
     * @param deltaY
     */
    private void initFooterViewToRefresh(int deltaY) {
        if (mBaseFooterManager == null) {
            return;
        }
        int topDistance = UpdateHeadViewMarginTop(deltaY);
        // 如果header view topMargin 的绝对值大于或等于(header + footer) 四分之一 的高度
        // 说明footer view 完全显示出来了，修改footer view 的提示状态
        if (Math.abs(topDistance) >= (mHeadViewHeight + mFooterViewHeight)/4
                && mFooterState != RELEASE_TO_REFRESH && Math.abs(topDistance) <(mHeadViewHeight + mFooterViewHeight)) {
            if (mFooterState == PULL_TO_OVER) {
                mBaseFooterManager.footerRefreshOver();
                return;
            }
            mBaseFooterManager.pullViewToRefresh(deltaY);
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(topDistance) >= (mHeadViewHeight + mFooterViewHeight) &&mFooterState != PULL_TO_REFRESH) {
            if (mFooterState == PULL_TO_OVER) {
                mBaseFooterManager.footerRefreshOver();
                return;
            }
            mBaseFooterManager.releaseViewToRefresh(deltaY);
            mFooterState = PULL_TO_REFRESH;
        }
    }

    /**
     * 处理头部布局 的位置
     * @param deltaY
     * @return
     */
    private int UpdateHeadViewMarginTop(int deltaY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        float topMargin = params.topMargin + deltaY * 0.3f;
        params.topMargin = (int) topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    /**
     * 处理正在刷新
     */
    private void headerRefreshing() {
        if (mBaseHeaderManager == null) {
            return;
        }
        mHeaderState = REFRESHING;
        setHeaderTopMargin(0);
        mBaseHeaderManager.headerRefreshing();
        if (mOnHeaderRefreshListener != null) {
            mOnHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    /**
     * 自动下拉刷新功能
     */
    public void autoHeaderRefreshing() {
        headerRefreshing();
    }

    /**
     * 头部是否正在刷新
     * @return
     */
    public boolean isHeaderRefreshing() {
        if (mPullState == PULL_DOWN_STATE) {
            return  mHeaderState == REFRESHING;
        }
        return false;
    }

    /**
     * 尾部是否正在刷新
     * @return
     */
    public boolean isFooterRefreshing() {
        if (mPullState == PULL_UP_STATE) {
            return mFooterState == REFRESHING;
        }
        return false;
    }

    /**
     * 处理正在加载
     */
    private void footerRefreshing() {
        if (mBaseFooterManager == null) {
            return;
        }

        mFooterState = REFRESHING;
        int top = mHeadViewHeight + mFooterViewHeight;
        setHeaderTopMargin(-top);
        mBaseFooterManager.footerRefreshing();
        if (mOnFooterRefreshListener != null) {
            mOnFooterRefreshListener.onFooterRefresh(this);
        }
    }

    /**
     * 回调之后的刷新完成
     */
    public void onHeaderRefreshComplete() {
        if (mBaseHeaderManager == null) {
            return;
        }
        setHeaderTopMarginComplete(-mHeadViewHeight);
        mBaseHeaderManager.headerRefreshComplete();
        mHeaderState = PULL_TO_REFRESH;
        mFooterState = PULL_TO_NORMOl;
    }

    /**
     * 顺滑的隐藏刷新布局
     * @param topMargin
     */
    private void setHeaderTopMarginComplete(final int topMargin) {
        final LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        ValueAnimator va = ValueAnimator.ofInt(0, topMargin);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentPaddingTop = (int) animation.getAnimatedValue();
                params.topMargin = currentPaddingTop;
                mHeaderView.setLayoutParams(params);
                invalidate();
            }
        });
        va.setDuration(300);
        va.start();
    }

    /**
     * 回调之后的加载完成
     */
    public void onFooterRefreshComplete() {
        if (mBaseFooterManager == null) {
            return;
        }
        setHeaderTopMargin(-mHeadViewHeight);
        mBaseFooterManager.footerRefreshComplete();
        mFooterState = PULL_TO_REFRESH;
    }

    /**
     * 回调之后的不再加载
     */
    public void onFooterRefreshOver() {
        mFooterState = PULL_TO_OVER;
        if (mBaseFooterManager == null) {
            return;
        }
        setHeaderTopMargin(-mHeadViewHeight);
        mBaseFooterManager.footerRefreshOver();
    }

    /**
     * 滑动由父View（当前View）处理
     *
     * @param deltaY
     * @return
     */
    private boolean isParentViewScroll(int deltaY) {
        boolean belongToParentView = false;
        if (mHeaderState == REFRESHING) {
            belongToParentView = false;
        }

        if (mAdapterView != null) {
            if (deltaY > 0) {
                View child = mAdapterView.getChildAt(0);
                if (child == null) {
                    belongToParentView = false;
                }

                if (mAdapterView.getFirstVisiblePosition() == 0 && child.getTop() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    belongToParentView = true;
                }
            } else if (deltaY < 0) {
                View lastChild = mAdapterView.getChildAt(mAdapterView
                        .getChildCount() - 1);
                if (lastChild == null) {
                    // 如果mAdapterView中没有数据,不拦截
                    belongToParentView = false;
                } else {
                    // 最后一个子view的Bottom小于父View的高度说明mAdapterView的数据没有填满父view,
                    // 等于父View的高度说明mAdapterView已经滑动到最后
                    if (lastChild.getBottom() <= getHeight()
                            && mAdapterView.getLastVisiblePosition() == mAdapterView
                            .getCount() - 1) {
                        mPullState = PULL_UP_STATE;
                        belongToParentView = true;
                    }
                }
            }
        }

        if (mRecyclerView != null) {
            if (deltaY > 0) {
                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    belongToParentView = false;
                }
                LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                /*set by ydg   由判断第一个完整可见view  改为判断第一个可见view 且处于最顶端
                * 第一种情况  第一个item 一个屏幕显示不下 也就是上述返回-1不能满足
                * */
//                int position = mLinearLayoutManager.findFirstVisibleItemPosition();
                int position = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position == 0) {
                    View firstVisiableChildView = mLinearLayoutManager.findViewByPosition(position);
                    if (firstVisiableChildView != null && firstVisiableChildView.getTop() >= 0) {
                        mPullState = PULL_DOWN_STATE;
                        belongToParentView = true;
                    }
                }

            } else if (deltaY < 0) {
                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    belongToParentView = false;
                }
                if (mRecyclerView.computeVerticalScrollExtent() + mRecyclerView.computeVerticalScrollOffset()
                        >= mRecyclerView.computeVerticalScrollRange()){
                    belongToParentView = true;
                    mPullState = PULL_UP_STATE;
                }else {
                    belongToParentView = false;
                }
            }
        }

        if (mScrollView != null) {
            View child = mScrollView.getChildAt(0);
            if (deltaY > 0) {

                if (child == null) {
                    belongToParentView = false;
                }

                int distance = mScrollView.getScrollY();
                if (distance == 0) {
                    mPullState = PULL_DOWN_STATE;
                    belongToParentView = true;
                }
            } else if (deltaY < 0
                    && child.getMeasuredHeight() <= getHeight()
                    + mScrollView.getScrollY()) {
                mPullState = PULL_UP_STATE;
                belongToParentView = true;

            }
        }


        if (mWebView != null) {
            View child = mWebView.getChildAt(0);
            if (deltaY > 0) {

                if (child == null) {
                    belongToParentView = false;
                }
//                int distance = mWebView.getWebScrollY();
                int distance = mWebView.getScrollY();
                if (distance == 0) {
                    mPullState = PULL_DOWN_STATE;
                    belongToParentView = true;
                }
            }
        }


        return belongToParentView;
    }


    /**
     * 获取当前header view 的topMargin
     *
     * @return
     * @description
     */

    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        return params.topMargin;
    }

    /**
     * 设置header view 的topMargin的值
     *
     * @param topMargin ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     * @description
     */
    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    /**
     * //上拉或下拉至一半时，放弃下来，视为完成一次下拉统一处理，初始化所有内容
     *
     * @param topMargin
     */
    private void reSetHeaderTopMargin(int topMargin) {

        if (mBaseHeaderManager != null) {
            mBaseHeaderManager.headerRefreshComplete();
        }

        if (mBaseFooterManager != null) {
            mBaseFooterManager.footerRefreshComplete();
        }

        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    public void setOnHeaderRefreshListener(OnHeaderRefreshListener onHeaderRefreshListener) {
        mOnHeaderRefreshListener = onHeaderRefreshListener;
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener onFooterRefreshListener) {
        mOnFooterRefreshListener = onFooterRefreshListener;
    }
}
