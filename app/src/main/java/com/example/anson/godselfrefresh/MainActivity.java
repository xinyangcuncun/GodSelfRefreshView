package com.example.anson.godselfrefresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.godseflrefresh.GodSeflRefreshView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GodSeflRefreshView godSeflRefreshView;
    private RecyclerView recyclerView;
    private Handler handler = new Handler();
    private int i = 0;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<String> mFilter = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mFilter.add("非自定义");
        mFilter.add("自定义");
        mFragments.add(new NotSelfFragment());
        mFragments.add(new SelfFragment());

        viewPager = (ViewPager) findViewById(R.id.viewpager_grow_course);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_grow_course);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFilter.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
            }
        });
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

    }
}
