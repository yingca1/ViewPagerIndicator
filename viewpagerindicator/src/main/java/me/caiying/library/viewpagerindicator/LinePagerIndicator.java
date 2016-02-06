package me.caiying.library.viewpagerindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LinePagerIndicator extends HorizontalScrollView {

    public interface OnTabSelectedListener {

        void onTabSelected(int position);
    }

    public OnPageChangeListener mOnPageChangeListener;
    private OnTabSelectedListener mTabSelectedListener;

    private LinearLayout mTabsContainer;
    private ViewPager mPager;

    private int tabCount;
    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint linePaint;
    private Paint diviPaint;

    private int indicatorColor = 0xFF52B800;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;
    private int textSelectedColor = 0xFF52B800;
    private int textUnselectColor = 0xFF808080;

    private boolean enableExpand = true;
    private boolean enableDivider = false;
    private boolean indicatorOnTop = false;
    private boolean viewPagerScrollWithAnimation = true;

    private int tabTextSize = 16;
    private int scrollOffset = 52;
    private float indicatorHeight = 1.5f;
    private float underlineHeight = 1.0f;
    private int dividerPadding = 12;
    private int tabPadding = 24;
    private int dividerWidth = 1;
    private int lastScrollX = 0;

    public LinePagerIndicator(Context context) {
        this(context, null);
    }

    public LinePagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinePagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        mTabsContainer = new LinearLayout(context);
        mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
        mTabsContainer.setLayoutParams(params);
        addView(mTabsContainer);

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        indicatorHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Style.FILL);

        diviPaint = new Paint();
        diviPaint.setAntiAlias(true);
        diviPaint.setStrokeWidth(dividerWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        linePaint.setColor(underlineColor);
        if(indicatorOnTop) {
            canvas.drawRect(0, 0, mTabsContainer.getWidth(), underlineHeight, linePaint);
        } else {
            canvas.drawRect(0, height - underlineHeight, mTabsContainer.getWidth(), height, linePaint);
        }

        linePaint.setColor(indicatorColor);

        View currentTab = mTabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
            View nextTab = mTabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        if(indicatorOnTop) {
            canvas.drawRect(lineLeft, 0, lineRight, indicatorHeight, linePaint);
        } else {
            canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, linePaint);
        }

        if(enableDivider) {
            diviPaint.setColor(dividerColor);
            for (int i = 0; i < tabCount - 1; i++) {
                View tab = mTabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, diviPaint);
            }
        }
    }

    public void setViewPager(ViewPager pager) {
        this.mPager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(new PageListener());

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    public void setOnTabReselectedListener(OnTabSelectedListener listener) {
        mTabSelectedListener = listener;
    }

    public void notifyDataSetChanged() {

        mTabsContainer.removeAllViews();

        tabCount = mPager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {
            addTab(i, mPager.getAdapter().getPageTitle(i).toString());
        }

        updateTabStyles();
    }

    private class TabView extends RelativeLayout {
        private TextView mTabText;

        public TabView(Context context) {
            super(context);
            init();
        }

        public TabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public TabView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            mTabText = new TextView(getContext());
            mTabText.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
            mTabText.setTextSize(tabTextSize / getResources().getConfiguration().fontScale);
            mTabText.setSingleLine(true);
            mTabText.setGravity(Gravity.CENTER);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.addView(mTabText, params);

        }

        public TextView getTextView() {
            return mTabText;
        }
    }

    private void addTab(final int position, String title) {
        TabView tab = new TabView(getContext());
        tab.getTextView().setText(title);
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final int oldSelected = mPager.getCurrentItem();
                if (oldSelected != position && mTabSelectedListener != null) {
                    mTabSelectedListener.onTabSelected(position);
                }

                mPager.setCurrentItem(position, viewPagerScrollWithAnimation);
            }
        });

        if(!enableExpand) {
            tab.setPadding(tabPadding, 0, tabPadding, 0);
        }
        mTabsContainer.addView(tab, position, enableExpand ?
                new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) :
                    new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    public void setTabText(int position, String text) {
        if (position < 0 || position > (mTabsContainer.getChildCount() - 1))
            throw new RuntimeException("tabs does not have this position.");

        View tab = mTabsContainer.getChildAt(position);
        if (tab instanceof TextView) {
            ((TextView) tab).setText(text);
        }
    }

    public boolean isIndicatorOnTop() {
        return indicatorOnTop;
    }

    public void setIndicatorOnTop(boolean indicatorOnTop) {
        this.indicatorOnTop = indicatorOnTop;
    }

    public boolean isEnableExpand() {
        return enableExpand;
    }

    public void setEnableExpand(boolean enableExpand) {
        this.enableExpand = enableExpand;
    }

    public boolean isEnableDivider() {
        return enableDivider;
    }

    public void setEnableDivider(boolean enableDivider) {
        this.enableDivider = enableDivider;
    }

    public void setViewPagerScrollWithAnimation(boolean enable) {
        this.viewPagerScrollWithAnimation = enable;
    }

    public boolean getViewPagerScrollWithAnimation() {
       return this.viewPagerScrollWithAnimation;
    }

    public void setCurrentItem(int item) {
        mPager.setCurrentItem(item, viewPagerScrollWithAnimation);
    }

    private void tabSelect(int index) {
        final int tabCount = mTabsContainer.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabsContainer.getChildAt(i);
            final boolean isSelected = (i == index);
            child.setSelected(isSelected);
            if(isSelected) {
                ((TabView) child).getTextView().setTextColor(textSelectedColor);
            } else {
                ((TabView) child).getTextView().setTextColor(textUnselectColor);
            }
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < tabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            v.setBackgroundColor(Color.TRANSPARENT);
        }
        tabSelect(mPager.getCurrentItem());
    }

    private void scrollToChild(int position, int offset) {
        if (tabCount == 0) {
            return;
        }

        int newScrollX = mTabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * mTabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(mPager.getCurrentItem(), 0);
            }

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            tabSelect(position);

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }
    }
}