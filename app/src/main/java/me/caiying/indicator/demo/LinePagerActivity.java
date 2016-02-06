package me.caiying.indicator.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import me.caiying.indicator.R;
import me.caiying.library.viewpagerindicator.LinePagerIndicator;

public class LinePagerActivity extends FragmentActivity {
    private LinePagerIndicator linePagerIndicator;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_line);
        linePagerIndicator = (LinePagerIndicator) findViewById(R.id.line_tab_indicator);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(this, getSupportFragmentManager()));
        linePagerIndicator.setViewPager(viewPager);
    }
}
