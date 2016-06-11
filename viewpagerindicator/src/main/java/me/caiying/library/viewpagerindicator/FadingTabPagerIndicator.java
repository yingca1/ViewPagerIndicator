
package me.caiying.library.viewpagerindicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FadingTabPagerIndicator extends LinearLayout {
    private ViewPager mViewPager;
    private PageListener pageListener = new PageListener();
    private LinearLayout.LayoutParams tabLayoutParams;
    private int iconHeight = 28; // dp
    private int iconWidth = 30; // dp
    private int textSize = 12; // sp
    private int paddingOffset = 4; // dp

    public FadingTabPagerIndicator(Context context) {
        super(context);
        init();
    }

    public FadingTabPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FadingTabPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        tabLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        tabLayoutParams.topMargin = Utils.dp2px(getContext(), paddingOffset);
        tabLayoutParams.bottomMargin = Utils.dp2px(getContext(), paddingOffset);
        iconHeight = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, iconHeight, getResources().getDisplayMetrics());
        iconWidth = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, iconWidth, getResources().getDisplayMetrics());
    }

    public void setViewPager(ViewPager pager) {
        this.mViewPager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setBadge(int index, int number) {
        if (number > 0) {
            BadgeView badgeView = ((TabView) getChildAt(index)).getBadgeView();
            badgeView.setText(String.valueOf(number));
            badgeView.show();
        } else {
            setNoneBadge(index);
        }
    }

    public void setNoneBadge(int index) {
        BadgeView badgeView = ((TabView) getChildAt(index)).getBadgeView();
        badgeView.setText("");
        badgeView.show();
    }

    public void hideBadge(int index) {
        ((TabView) getChildAt(index)).getBadgeView().hide();
    }

    public void setCurrentItem(int item) {
        mViewPager.setCurrentItem(item, false);
        tabSelect(item);
    }

    public void notifyDataSetChanged() {
        removeAllViews();

        int tabCount = mViewPager.getAdapter().getCount();

        FadingTab tabs = (FadingTab) mViewPager.getAdapter();
        for (int i = 0; i < tabCount; i++) {
            addTab(i, mViewPager.getAdapter().getPageTitle(i).toString(),
                tabs.getTabNormalIconResId(i), tabs.getTabSelectIconResId(i),
                tabs.getTabNormalTextColor(i), tabs.getTabSelectTextColor(i));
        }

        setCurrentItem(mViewPager.getCurrentItem());
    }

    private void addTab(final int position, String text, int normalResId, int selectResId,
                        int textNormalColorResId, int textSelectColorResId) {
        TabView tabView = new TabView(getContext(), text, normalResId, selectResId,
            textNormalColorResId, textSelectColorResId);
        tabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItem(position);
            }
        });
        addView(tabView, position, tabLayoutParams);
    }

    private void tabSelect(int index) {
        final int tabCount = getChildCount();
        for (int i = 0; i < tabCount; i++) {
            getChildAt(i).setSelected(i == index);
        }
    }

    private class FadingImageView extends FrameLayout {
        private ImageView mNormalImage;
        private ImageView mSelectImage;

        public FadingImageView(Context context, int normalResId, int selectResId) {
            super(context);
            mNormalImage = new ImageView(context);
            mSelectImage = new ImageView(context);
            mNormalImage.setImageResource(normalResId);
            mSelectImage.setImageResource(selectResId);
            mNormalImage.setAlpha(1.0f);
            mSelectImage.setAlpha(0.0f);
            addView(mNormalImage, 0, new LayoutParams(iconWidth, iconHeight, Gravity.CENTER));
            addView(mSelectImage, 1, new LayoutParams(iconWidth, iconHeight, Gravity.CENTER));
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            setAlpha(selected ? 1.0f : 0.0f);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void setAlpha(float alpha) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mNormalImage.setAlpha(1 - alpha);
                mSelectImage.setAlpha(alpha);
            } else {
                mNormalImage.setAlpha((int) (1 - alpha));
                mSelectImage.setAlpha((int) alpha);
            }
        }
    }

    private class FadingTextView extends FrameLayout {
        private TextView mNormalTextView;
        private TextView mSelectTextView;

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public FadingTextView(Context context, String text, int normalColor, int selectColor) {
            super(context);
            mNormalTextView = new TextView(context);
            mSelectTextView = new TextView(context);
            mNormalTextView.setTextColor(normalColor);
            mSelectTextView.setTextColor(selectColor);
            mNormalTextView.setAlpha(1.0f);
            mSelectTextView.setAlpha(0.0f);
            mNormalTextView.setText(text);
            mSelectTextView.setText(text);
            mNormalTextView.setTextSize(textSize);
            mSelectTextView.setTextSize(textSize);
            addView(mNormalTextView, 0, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            addView(mSelectTextView, 1, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            setAlpha(selected ? 1.0f : 0.0f);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void setAlpha(float alpha) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mNormalTextView.setAlpha(1 - alpha);
                mSelectTextView.setAlpha(alpha);
            } else {
                mNormalTextView.setAlpha((int) (1 - alpha));
                mSelectTextView.setAlpha((int) alpha);
            }
        }
    }

    private class TabView extends LinearLayout {
        private FadingImageView fadingImageView;
        private FadingTextView fadingTextView;
        private BadgeView badgeView;

        public TabView(Context context, String text, int normalResId, int selectResId,
                       int textNormalColorResId, int textSelectColorResId) {
            super(context);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);

            LayoutParams wpLayoutParams =
                new LayoutParams(Utils.dp2px(getContext(), 58), Utils.dp2px(getContext(), 28));
            LinearLayout wrapperLayout = new LinearLayout(getContext());
            wrapperLayout.setGravity(Gravity.CENTER);
            addView(wrapperLayout, wpLayoutParams);

            LayoutParams ivLayoutParams =
                new LayoutParams(Utils.dp2px(getContext(), 32), Utils.dp2px(getContext(), 28));
            fadingImageView = new FadingImageView(context, normalResId, selectResId);
            wrapperLayout.addView(fadingImageView, ivLayoutParams);

            LayoutParams tvLayoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            fadingTextView = new FadingTextView(context, text, textNormalColorResId, textSelectColorResId);
            addView(fadingTextView, tvLayoutParams);

            badgeView = new BadgeView(getContext(), wrapperLayout);
            badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badgeView.setBadgeMargin(0, 0);
        }

        public BadgeView getBadgeView() {
            return badgeView;
        }

        public FadingImageView getFadingImageView() {
            return fadingImageView;
        }

        public FadingTextView getFadingTextView() {
            return fadingTextView;
        }
    }

    private class PageListener implements OnPageChangeListener {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            ((TabView) getChildAt(position)).getFadingImageView().setAlpha(1.0f - positionOffset);
            ((TabView) getChildAt(position)).getFadingTextView().setAlpha(1.0f - positionOffset);
            if (position + 1 <= getChildCount() - 1) {
                ((TabView) getChildAt(position + 1)).getFadingImageView().setAlpha(positionOffset);
                ((TabView) getChildAt(position + 1)).getFadingTextView().setAlpha(positionOffset);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            tabSelect(position);
        }
    }

    public interface FadingTab {

        int getTabNormalIconResId(int position);

        int getTabSelectIconResId(int position);

        int getTabNormalTextColor(int position);

        int getTabSelectTextColor(int position);
    }
}

