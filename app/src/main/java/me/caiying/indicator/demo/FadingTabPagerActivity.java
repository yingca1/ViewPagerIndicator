
package me.caiying.indicator.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import me.caiying.indicator.R;
import me.caiying.library.viewpagerindicator.FadingTabPagerIndicator;

public class FadingTabPagerActivity extends FragmentActivity {
    private FadingTabPagerIndicator fadingTabPagerIndicator;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fading_tab);
        fadingTabPagerIndicator = (FadingTabPagerIndicator) findViewById(R.id.fade_tab_indicator);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new FadeTabFragmentPagerAdapter(this, getSupportFragmentManager()));
        fadingTabPagerIndicator.setViewPager(viewPager);
        fadingTabPagerIndicator.setBackgroundResource(R.drawable.bg_tabs);
        fadingTabPagerIndicator.setBadge(0, 3);
        fadingTabPagerIndicator.setBadge(1, 23);
        fadingTabPagerIndicator.setBadge(2, 999);
        fadingTabPagerIndicator.setNoneBadge(3);
    }

    private class FadeTabFragmentPagerAdapter extends SampleFragmentPagerAdapter implements FadingTabPagerIndicator.FadingTab {

        public FadeTabFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(context, fm);
        }

        @Override
        public int getTabNormalIconResId(int position) {
            return new int[]{R.drawable.ic_1_1, R.drawable.ic_2_1,
                R.drawable.ic_3_1, R.drawable.ic_4_1}[position];
        }

        @Override
        public int getTabSelectIconResId(int position) {
            return new int[]{R.drawable.ic_1_0, R.drawable.ic_2_0,
                R.drawable.ic_3_0, R.drawable.ic_4_0}[position];
        }

        @Override
        public int getTabNormalTextColor(int position) {
            return getResources().getColor(R.color.text_normal);
        }

        @Override
        public int getTabSelectTextColor(int position) {
            return getResources().getColor(R.color.text_select);
        }
    }
}
