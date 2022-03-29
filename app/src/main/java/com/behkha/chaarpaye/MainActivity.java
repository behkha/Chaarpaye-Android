package com.behkha.chaarpaye;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import category.CategoryFragment;
import collection.CollectionFragment;
import io.fabric.sdk.android.Fabric;
import posts.PostsFragment;
import setting.SettingActivity;
import utils.Fonts;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ImageView mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        this.mContext = getApplicationContext();

        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setTypeface(Fonts.get_iran_sans_bold_font(mContext));

        mSetting = (ImageView) findViewById(R.id.toolbar_setting);
        mSetting.setVisibility(View.GONE);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager mViewpager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(mViewpager);
        onViewPagerChangeListener(mViewpager);
        mTabLayout.setupWithViewPager(mViewpager);
        changeTabsFont(mTabLayout);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewpagerAdapter adapter = new ViewpagerAdapter(getSupportFragmentManager());
        adapter.addFragment( new PostsFragment() , mContext.getResources().getString( R.string.news ));
        adapter.addFragment( new CategoryFragment() , mContext.getResources().getString( R.string.category ));
        adapter.addFragment( new CollectionFragment() , mContext.getResources().getString( R.string.collections ));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }
    private void changeTabsFont(TabLayout tabLayout) {
        ViewGroup childTabLayout = (ViewGroup) tabLayout.getChildAt(0);
        for (int i = 0; i < childTabLayout.getChildCount(); i++) {
            ViewGroup viewTab = (ViewGroup) childTabLayout.getChildAt(i);
            for (int j = 0; j < viewTab.getChildCount(); j++) {
                View tabTextView = viewTab.getChildAt(j);
                if (tabTextView instanceof TextView) {
                    Typeface typeface = Fonts.get_iran_sans_font(mContext);
                    ((TextView) tabTextView).setTypeface(typeface);
                }
            }
        }
    }
    private void onViewPagerChangeListener(ViewPager viewPager){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2)
                    mSetting.setVisibility(View.VISIBLE);
                else
                    mSetting.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public void onSettingClick(View view){
        Intent intent = new Intent(mContext, SettingActivity.class);
        startActivity(intent);
    }
}
