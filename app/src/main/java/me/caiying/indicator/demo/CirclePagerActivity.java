package me.caiying.indicator.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import me.caiying.indicator.R;
import me.caiying.library.viewpagerindicator.CirclePagerIndicator;

/**
 * Created by caiying on 06/02/2016.
 */
public class CirclePagerActivity extends FragmentActivity {
    private CirclePagerIndicator circlePagerIndicator;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        circlePagerIndicator = (CirclePagerIndicator) findViewById(R.id.circle_pager_indicator);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(this, getSupportFragmentManager()));
        circlePagerIndicator.setViewPager(viewPager);
    }
}
