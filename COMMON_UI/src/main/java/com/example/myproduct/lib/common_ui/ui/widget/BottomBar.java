package com.example.myproduct.lib.common_ui.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myproduct.lib.common_ui.R;
import com.example.myproduct.lib.common_ui.utils.view.DensityUtils;
import com.example.myproduct.lib.common_ui.utils.view.ViewUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

/**
 * 底部导航工具栏
 * <p/>
 * 1) 界面设计规范
 * https://www.google.com/design/spec/components/bottom-navigation.html#bottom-navigation-specs
 * <p/>
 * 2) 其他类似实现
 * - {@link android.support.design.widget.TabLayout}
 * - {a https://github.com/roughike/BottomBar roughike/BottomBar}
 *
 * @author lihanguang
 * @date 2016/4/15 13:54:39
 */
public class BottomBar extends LinearLayout {
    private static final String TAG = "GzbBottomBar";

    public interface OnTabSelectedListener {

        void onTabSelected(Tab tab);

        void onTabUnselected(Tab tab);

        void onTabReselected(Tab tab);
    }

    private static final int DEFAULT_GAP_TEXT_ICON = 0; // dps

    private final ArrayList<Tab> mTabs = new ArrayList<>();
    private Tab mSelectedTab;
    private Typeface mTypeface;

    private int mTabPaddingStart;
    private int mTabPaddingTopInactive;
    private int mTabPaddingTopActive;
    private int mTabPaddingEnd;
    private int mTabPaddingBottom;

    private int mTabTextSizeInactive;
    private int mTabTextSizeActive;

    private int mTabTextAppearance;
    private ColorStateList mTabTextColors;

    private ColorStateList mTabIconColors;

    private final int mTabBackgroundResId = 0;

    private List<OnTabSelectedListener> mOnTabSelectedListeners;
    private OnTabSelectedListener mOnTabSelectedListenerForViewPager;
    private View.OnClickListener mTabClickListener;

    public BottomBar(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mTabPaddingStart = DensityUtils.dip2px(context, 12);
        mTabPaddingEnd = DensityUtils.dip2px(context, 12);
        mTabPaddingTopInactive = DensityUtils.dip2px(context, 4);
        mTabPaddingTopActive = DensityUtils.dip2px(context, 2);
        mTabPaddingBottom = DensityUtils.dip2px(context, 6);

        mTabTextSizeInactive = 12;
        mTabTextSizeActive = 14;

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, android.support.design.R.styleable.TabLayout, defStyleAttr, defStyleRes);

        mTabTextAppearance = a.getResourceId(android.support.design.R.styleable.TabLayout_tabTextAppearance,
                android.support.design.R.style.TextAppearance_Design_Tab);

        // Text colors/sizes come from the text appearance first
        final TypedArray ta = context.obtainStyledAttributes(mTabTextAppearance,
                android.support.design.R.styleable.TextAppearance);
        try {
            mTabTextColors = ta.getColorStateList(android.support.design.R.styleable.TextAppearance_android_textColor);
        } finally {
            ta.recycle();
        }

        if (a.hasValue(android.support.design.R.styleable.TabLayout_tabTextColor)) {
            // If we have an explicit text color set, use it instead
            mTabTextColors = a.getColorStateList(android.support.design.R.styleable.TabLayout_tabTextColor);
            mTabIconColors = mTabTextColors;
        }

        if (a.hasValue(android.support.design.R.styleable.TabLayout_tabSelectedTextColor)) {
            // We have an explicit selected text color set, so we need to make merge it with the
            // current colors. This is exposed so that developers can use theme attributes to set
            // this (theme attrs in ColorStateLists are Lollipop+)
            final int selected = a.getColor(android.support.design.R.styleable.TabLayout_tabSelectedTextColor, 0);
            mTabTextColors = createColorStateList(mTabTextColors.getDefaultColor(), selected);
            mTabIconColors = mTabTextColors;
        }

        a.recycle();
    }

    public static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;

        return new ColorStateList(states, colors);
    }

    /*
        BEGIN: 创建Tab的接口
     */

    @NonNull
    public Tab newTab() {
        return new Tab(this);
    }

    public void addTab(@NonNull Tab tab) {
        addTab(tab, mTabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, int position) {
        addTab(tab, position, mTabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, boolean setSelected) {
        if (tab.mParent != this) {
            throw new IllegalArgumentException("Tab belongs to a different GzbBottomBar.");
        }

        addTabView(tab, setSelected);
        configureTab(tab, mTabs.size());
        if (setSelected) {
            tab.select();
        }
    }

    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        if (tab.mParent != this) {
            throw new IllegalArgumentException("Tab belongs to a different GzbBottomBar.");
        }

        addTabView(tab, position, setSelected);
        configureTab(tab, position);
        if (setSelected) {
            tab.select();
        }
    }

    private void addTabView(Tab tab, boolean setSelected) {
        final TabView tabView = createTabView(tab);
        addView(tabView, createLayoutParamsForTabs());
        tabView.setSelected(setSelected);
    }

    private void addTabView(Tab tab, int position, boolean setSelected) {
        final TabView tabView = createTabView(tab);
        addView(tabView, position, createLayoutParamsForTabs());
        tabView.setSelected(setSelected);
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs() {
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        return lp;
    }

    public TabView createTabView(@NonNull final Tab tab) {
        TabView tabView = null;
        if (tabView == null) {
            tabView = new TabView(getContext());
        }
        tabView.setTab(tab);
        tabView.setFocusable(true);
        //TODO
        /*tabView.setMinimumWidth(getTabMinWidth());*/

        if (mTabClickListener == null) {
            mTabClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabView tabView = (TabView) view;
                    tabView.getTab().select();
                }
            };
        }
        tabView.setOnClickListener(mTabClickListener);
        return tabView;
    }

    private void configureTab(Tab tab, int position) {
        tab.setPosition(position);
        mTabs.add(position, tab);

        final int count = mTabs.size();
        for (int i = position + 1; i < count; i++) {
            mTabs.get(i).setPosition(i);
        }
    }

    /*
        END: 创建Tab的接口
     */

    /*
        BEGIN: 删除Tab的接口
     */

    public void removeAllTabs() {
        // Remove all the views
        for (int i = getChildCount() - 1; i >= 0; i--) {
            removeTabViewAt(i);
        }

        for (final Iterator<Tab> i = mTabs.iterator(); i.hasNext(); ) {
            final Tab tab = i.next();
            i.remove();
            tab.reset();
        }

        mSelectedTab = null;
    }

    private void removeTabViewAt(int position) {
        final TabView view = (TabView) getChildAt(position);
        removeViewAt(position);
        if (view != null) {
            view.reset();
        }
        requestLayout();
    }

    /*
        END: 删除Tab的接口
     */

    /*
        BEGIN: 选择切换Tab的接口
     */

    void selectTab(Tab tab) {
        selectTab(tab, true);
    }

    void selectTab(Tab tab, boolean updateIndicator) {
        if (mSelectedTab == tab) {
            if (mSelectedTab != null) {
                notifyOnTabSelectedListener(mSelectedTab, 2);
            }
        } else {
            if (updateIndicator) {
                final int newPosition = tab != null ? tab.getPosition() : Tab.INVALID_POSITION;
                if (newPosition != Tab.INVALID_POSITION) {
                    setSelectedTabView(newPosition);
                }
            }
            if (mSelectedTab != null) {
                notifyOnTabSelectedListener(mSelectedTab, 1);
            }
            mSelectedTab = tab;
            if (mSelectedTab != null) {
                notifyOnTabSelectedListener(mSelectedTab, 0);
            }
        }
    }

    private void setSelectedTabView(int position) {
        final int tabCount = getChildCount();
        if (position < tabCount && !getChildAt(position).isSelected()) {
            for (int i = 0; i < tabCount; i++) {
                final View child = getChildAt(i);
                child.setSelected(i == position);
            }
        }
    }

    /*
        END: 选择切换Tab的接口
     */

    /*
        BEGIN: 更新Tab的接口
     */

    private void updateTab(int position) {
        final TabView view = getTabView(position);
        if (view != null) {
            view.update();
        }
    }

    private void updateAllTabs() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            updateTab(i);
        }
    }

    /*
        END: 更新Tab的接口
     */

    public int getTabCount() {
        return mTabs.size();
    }

    @Nullable
    public Tab getTabAt(int index) {
        return mTabs.get(index);
    }

    public TabView getTabView(int position) {
        return (TabView) getChildAt(position);
    }

    public int getSelectedTabPosition() {
        return mSelectedTab != null ? mSelectedTab.getPosition() : -1;
    }

    /**
     * Sets the text colors for the different states (normal, selected) used for the tabs.
     */
    public void setTabTextColors(@Nullable ColorStateList textColor) {
        if (mTabTextColors != textColor) {
            mTabTextColors = textColor;
            updateAllTabs();
        }
    }

    /**
     * Sets the text colors for the different states (normal, selected) used for the tabs.
     */
    public void setTabTextColors(int normalColor, int selectedColor) {
        setTabTextColors(createColorStateList(normalColor, selectedColor));
    }

    /**
     * Gets the text colors for the different states (normal, selected) used for the tabs.
     */
    @Nullable
    public ColorStateList getTabTextColors() {
        return mTabTextColors;
    }

    /**
     * Sets the text colors for the different states (normal, selected) used for the tabs.
     */
    public void setTabIconColors(@Nullable ColorStateList textColor) {
        if (mTabIconColors != textColor) {
            mTabIconColors = textColor;
            updateAllTabs();
        }
    }

    /**
     * Sets the text colors for the different states (normal, selected) used for the tabs.
     */
    public void setTabIconColors(int normalColor, int selectedColor) {
        setTabIconColors(createColorStateList(normalColor, selectedColor));
    }

    /**
     * Gets the text colors for the different states (normal, selected) used for the tabs.
     */
    @Nullable
    public ColorStateList getTabIconColors() {
        return mTabIconColors;
    }

    /**
     * Set a custom TypeFace for the tab titles.
     * The .ttf file should be located at "/src/main/assets".
     *
     * @param typeFacePath path for the custom typeface in the assets directory.
     */
    public void setTypeFace(String typeFacePath) {
        AssetManager am = getContext().getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, typeFacePath);
        mTypeface = typeface;
    }

    public void addOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        if (mOnTabSelectedListeners == null) {
            mOnTabSelectedListeners = new LinkedList<>();
        }
        mOnTabSelectedListeners.add(onTabSelectedListener);
    }

    public void removeOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        if (mOnTabSelectedListeners != null) {
            mOnTabSelectedListeners.remove(onTabSelectedListener);
        }
    }

    private void notifyOnTabSelectedListener(Tab tab, int selectType) {
        if (mOnTabSelectedListeners != null && !mOnTabSelectedListeners.isEmpty()) {
            for (OnTabSelectedListener l : mOnTabSelectedListeners) {
                switch (selectType) {
                    case 0:
                        l.onTabSelected(tab);
                        break;
                    case 1:
                        l.onTabUnselected(tab);
                        break;
                    case 2:
                        l.onTabReselected(tab);
                        break;
                }
            }
        }
    }

    public static class Tab {

        public static final int INVALID_POSITION = -1;

        private Object mTag;
        private Drawable mIcon;
        private CharSequence mText;
        private CharSequence mContentDesc;
        private int mPosition = INVALID_POSITION;
        private View mCustomView;

        private final BottomBar mParent;

        /* package */
        Tab(BottomBar parent) {
            mParent = parent;
        }

        @Nullable
        public Object getTag() {
            return mTag;
        }

        @NonNull
        public Tab setTag(@Nullable Object tag) {
            mTag = tag;
            return this;
        }

        @Nullable
        public View getCustomView() {
            return mCustomView;
        }

        @NonNull
        public Tab setCustomView(@Nullable View view) {
            mCustomView = view;
            if (mPosition >= 0) {
                mParent.updateTab(mPosition);
            }
            return this;
        }

        @NonNull
        public Tab setCustomView(@LayoutRes int resId) {
            final TabView tabView = mParent.getTabView(mPosition);
            final LayoutInflater inflater = LayoutInflater.from(tabView.getContext());
            return setCustomView(inflater.inflate(resId, tabView, false));
        }

        @Nullable
        public Drawable getIcon() {
            return mIcon;
        }

        public int getPosition() {
            return mPosition;
        }

        void setPosition(int position) {
            mPosition = position;
        }

        @Nullable
        public CharSequence getText() {
            return mText;
        }

        @NonNull
        public Tab setIcon(@Nullable Drawable icon) {
            mIcon = icon;
            if (mPosition >= 0) {
                mParent.updateTab(mPosition);
            }
            return this;
        }

        @NonNull
        public Tab setIcon(@DrawableRes int resId) {
            return setIcon(ContextCompat.getDrawable(mParent.getContext(), resId));
        }

        @NonNull
        public Tab setText(@Nullable CharSequence text) {
            mText = text;
            if (mPosition >= 0) {
                mParent.updateTab(mPosition);
            }
            return this;
        }

        @NonNull
        public Tab setText(@StringRes int resId) {
            return setText(mParent.getResources().getText(resId));
        }

        public void select() {
            mParent.selectTab(this);
        }

        public boolean isSelected() {
            return mParent.getSelectedTabPosition() == mPosition;
        }

        @NonNull
        public Tab setContentDescription(@StringRes int resId) {
            return setContentDescription(mParent.getResources().getText(resId));
        }

        @NonNull
        public Tab setContentDescription(@Nullable CharSequence contentDesc) {
            mContentDesc = contentDesc;
            if (mPosition >= 0) {
                mParent.updateTab(mPosition);
            }
            return this;
        }

        @Nullable
        public CharSequence getContentDescription() {
            return mContentDesc;
        }

        private void reset() {
            mTag = null;
            mIcon = null;
            mText = null;
            mContentDesc = null;
            mPosition = INVALID_POSITION;
            mCustomView = null;
        }
    }

    public class TabView extends LinearLayout {
        private Tab mTab;
        private TextView mTextView;
        private ImageView mIconView;

        private View mCustomView;
        private TextView mCustomTextView;
        private ImageView mCustomIconView;

        public TabView(Context context) {
            super(context);
            init(context, null, 0, 0);
        }

        public TabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs, 0, 0);
        }

        public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context, attrs, defStyleAttr, 0);
        }

        @TargetApi(21)
        public TabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init(context, attrs, defStyleAttr, defStyleRes);
        }

        private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            if (mTabBackgroundResId != 0) {
                setBackgroundDrawable(ContextCompat.getDrawable(context, mTabBackgroundResId));
            }
            ViewCompat.setPaddingRelative(this, mTabPaddingStart, mTabPaddingTopInactive,
                    mTabPaddingEnd, mTabPaddingBottom);
            setGravity(Gravity.CENTER);
            setOrientation(VERTICAL);
        }

        @Override
        public void setSelected(boolean selected) {
            final boolean changed = (isSelected() != selected);
            super.setSelected(selected);
            if (changed) {
                if (selected) {
                    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
                }
                if (mCustomView == null) {
                    if (mIconView != null) {
                        mIconView.setSelected(selected);
                    }
                    if (mTextView != null) {
                        mTextView.setSelected(selected);
                        updateBuiltInLayout(selected);
                    }
                } else {
                    if (mCustomIconView != null) {
                        mCustomIconView.setSelected(selected);
                    }
                    if (mCustomTextView != null) {
                        mCustomTextView.setSelected(selected);
                    }
                    mCustomView.setSelected(selected); // 建议 CustomView 重写 View#setSelected(...)方法, 用来传递 select 状态.
                }
            }
        }

        private void updateBuiltInLayout(final boolean selected) {
            if (selected) {
                //mTextView.setTextSize(mTabTextSizeActive); // 文字不放大了
                mTextView.setTextSize(mTabTextSizeInactive);
                /*ViewCompat.setPaddingRelative(this, mTabPaddingStart, mTabPaddingTopActive,
                        mTabPaddingEnd, mTabPaddingBottom);*/ // 不突出显示
                ViewCompat.setPaddingRelative(this, mTabPaddingStart, mTabPaddingTopInactive,
                        mTabPaddingEnd, mTabPaddingBottom);
            } else {
                mTextView.setTextSize(mTabTextSizeInactive);
                ViewCompat.setPaddingRelative(this, mTabPaddingStart, mTabPaddingTopInactive,
                        mTabPaddingEnd, mTabPaddingBottom);
            }
        }

        private void setTab(@Nullable final Tab tab) {
            if (tab != mTab) {
                mTab = tab;
                update();
            }
        }

        public Tab getTab() {
            return mTab;
        }

        private void reset() {
            setTab(null);
            setSelected(false);
        }

        final void update() {
            final Tab tab = mTab;
            final View custom = tab != null ? tab.getCustomView() : null;
            if (custom != null) {
                final ViewParent customParent = custom.getParent();
                if (customParent != this) {
                    if (customParent != null) {
                        ((ViewGroup) customParent).removeView(custom);
                    }
                    addView(custom);
                }
                mCustomView = custom;
                if (mTextView != null) {
                    mTextView.setVisibility(GONE);
                }
                if (mIconView != null) {
                    mIconView.setVisibility(GONE);
                    mIconView.setImageDrawable(null);
                }

                mCustomTextView = (TextView) custom.findViewById(android.R.id.text1);
                mCustomIconView = (ImageView) custom.findViewById(android.R.id.icon);
            } else {
                // We do not have a custom view. Remove one if it already exists
                if (mCustomView != null) {
                    removeView(mCustomView);
                    mCustomView = null;
                }
                mCustomTextView = null;
                mCustomIconView = null;
            }

            if (mCustomView == null) {
                // If there isn't a custom view, we'll us our own in-built layouts
                if (mIconView == null) {
                    View tabIconLayout = ViewUtils.inflate(getContext(), R.layout.common_ui_bottom_bar_tab_icon, this, false);
                    ImageView iconView = ViewUtils.findViewById(tabIconLayout, android.R.id.icon);
                    addView(tabIconLayout, 0);
                    mIconView = iconView;
                }
                if (mTextView == null) {
                    View tabTextLayout = ViewUtils.inflate(getContext(), R.layout.common_ui_bottom_bar_tab_text, this, false);
                    TextView textView = ViewUtils.findViewById(tabTextLayout, android.R.id.text1);
                    addView(tabTextLayout);
                    mTextView = textView;
                }
                mTextView.setTextAppearance(getContext(), mTabTextAppearance);

                updateTextAndIcon(mTextView, mIconView);
                updateBuiltInLayout(isSelected());
            } else {
                // Else, we'll see if there is a TextView or ImageView present and update them
                if (mCustomTextView != null || mCustomIconView != null) {
                    updateTextAndIcon(mCustomTextView, mCustomIconView);
                    mCustomTextView.setSelected(isSelected());
                    mCustomIconView.setSelected(isSelected());
                }
                mCustomView.setSelected(isSelected());
            }
        }

        private void tintIconView(ImageView iconView, ColorStateList colorStateList) {
            if (colorStateList != null && iconView != null) {
                final Drawable wrappedDrawable = DrawableCompat.wrap(iconView.getDrawable());
                DrawableCompat.setTintList(wrappedDrawable, colorStateList);
                iconView.setImageDrawable(wrappedDrawable);
            }
        }

        private void tintTextView(TextView textView, ColorStateList colorStateList) {
            if (colorStateList != null && textView != null) {
                textView.setTextColor(colorStateList);
            }
        }

        private void updateTextAndIcon(@Nullable final TextView textView,
                                       @Nullable final ImageView iconView) {
            final Drawable icon = mTab != null ? mTab.getIcon() : null;
            final CharSequence text = mTab != null ? mTab.getText() : null;
            final CharSequence contentDesc = mTab != null ? mTab.getContentDescription() : null;

            if (iconView != null) {
                if (icon != null) {
                    iconView.setImageDrawable(icon);
                    tintIconView(iconView, mTabIconColors);
                    iconView.setVisibility(VISIBLE);
                    setVisibility(VISIBLE);
                } else {
                    iconView.setVisibility(GONE);
                    iconView.setImageDrawable(null);
                }
                iconView.setContentDescription(contentDesc);
            }

            final boolean hasText = !TextUtils.isEmpty(text);
            if (textView != null) {
                if (hasText) {
                    textView.setText(text);
                    tintTextView(textView, mTabTextColors);
                    textView.setVisibility(VISIBLE);
                    setVisibility(VISIBLE);
                } else {
                    textView.setVisibility(GONE);
                    textView.setText(null);
                }
                textView.setContentDescription(contentDesc);
                if (mTypeface != null) {
                    textView.setTypeface(mTypeface);
                }
            }

            if (iconView != null) {
                MarginLayoutParams lp = ((MarginLayoutParams) iconView.getLayoutParams());
                int bottomMargin = 0;
                if (hasText && iconView.getVisibility() == VISIBLE) {
                    // If we're showing both text and icon, add some margin bottom to the icon
                    bottomMargin = DensityUtils.dip2px(getContext(), DEFAULT_GAP_TEXT_ICON);
                }
                if (bottomMargin != lp.bottomMargin) {
                    lp.bottomMargin = bottomMargin;
                    iconView.requestLayout();
                }
            }
        }
    }

    /*
        BEGIN: 与 ViewPager 快速继承的方法
     */

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private DataSetObserver mPagerAdapterObserver;
    private GzbBottomBarOnPageChangeListener mPageChangeListener;

    public void setupWithViewPager(@Nullable final ViewPager viewPager) {
        if (mViewPager != null && mPageChangeListener != null) {
            // If we've already been setup with a ViewPager, remove us from it
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
        }

        if (viewPager != null) {
            final PagerAdapter adapter = viewPager.getAdapter();
            if (adapter == null) {
                throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
            }

            mViewPager = viewPager;

            // Add our custom OnPageChangeListener to the ViewPager
            if (mPageChangeListener == null) {
                mPageChangeListener = new GzbBottomBarOnPageChangeListener(this);
            }
            mPageChangeListener.reset();
            viewPager.addOnPageChangeListener(mPageChangeListener);

            // Now we'll add a tab selected listener to set ViewPager's current item
            mOnTabSelectedListenerForViewPager = new ViewPagerOnTabSelectedListener(viewPager);
            addOnTabSelectedListener(mOnTabSelectedListenerForViewPager);

            // Now we'll populate ourselves from the pager adapter
            setPagerAdapter(adapter, true);
        } else {
            // We've been given a null ViewPager so we need to clear out the internal state,
            // listeners and observers
            mViewPager = null;
            removeOnTabSelectedListener(mOnTabSelectedListenerForViewPager);
            mOnTabSelectedListenerForViewPager = null;
            setPagerAdapter(null, true);
        }
    }

    private void setPagerAdapter(@Nullable final PagerAdapter adapter, final boolean addObserver) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            // If we already have a PagerAdapter, unregister our observer
            mPagerAdapter.unregisterDataSetObserver(mPagerAdapterObserver);
        }

        mPagerAdapter = adapter;

        if (addObserver && adapter != null) {
            // Register our observer on the new adapter
            if (mPagerAdapterObserver == null) {
                mPagerAdapterObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(mPagerAdapterObserver);
        }

        // Finally make sure we reflect the new adapter
        populateFromPagerAdapter();
    }

    private void populateFromPagerAdapter() {
        removeAllTabs();

        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                Tab tab = newTab().setText(mPagerAdapter.getPageTitle(i));
                if (mPagerAdapter instanceof BottomBarFragmentPagerAdapter) {
                    BottomBarFragmentPagerAdapter adapter = (BottomBarFragmentPagerAdapter) mPagerAdapter;
                    tab.setIcon(adapter.getPageIconDrawable(i));
                }
                addTab(tab, false);
            }

            // Make sure we reflect the currently set ViewPager item
            if (mViewPager != null && adapterCount > 0) {
                final int curItem = mViewPager.getCurrentItem();
                if (curItem != getSelectedTabPosition() && curItem < getTabCount()) {
                    selectTab(getTabAt(curItem));
                }
            }
        } else {
            removeAllTabs();
        }
    }

    public static class GzbBottomBarOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final WeakReference<BottomBar> mTabLayoutRef;
        private int mPreviousScrollState;
        private int mScrollState;

        public GzbBottomBarOnPageChangeListener(BottomBar tabLayout) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            final BottomBar tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                // Only update the text selection if we're not settling, or we are settling after
                // being dragged
                final boolean updateText = mScrollState != SCROLL_STATE_SETTLING ||
                        mPreviousScrollState == SCROLL_STATE_DRAGGING;
                // Update the indicator if we're not settling after being idle. This is caused
                // from a setCurrentItem() call and will be handled by an animation from
                // onPageSelected() instead.
                final boolean updateIndicator = !(mScrollState == SCROLL_STATE_SETTLING
                        && mPreviousScrollState == SCROLL_STATE_IDLE);


                final int roundedPosition = Math.round(position + positionOffset);
                if (roundedPosition < 0 || roundedPosition >= tabLayout.getChildCount()) {
                    return;
                }

                if (updateText) {
                    tabLayout.setSelectedTabView(roundedPosition);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            final BottomBar tabLayout = mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
                final boolean updateIndicator = mScrollState == SCROLL_STATE_IDLE
                        || (mScrollState == SCROLL_STATE_SETTLING
                        && mPreviousScrollState == SCROLL_STATE_IDLE);
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator);
            }
        }

        private void reset() {
            mPreviousScrollState = mScrollState = SCROLL_STATE_IDLE;
        }
    }

    public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final ViewPager mViewPager;

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onTabSelected(Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab tab) {
            // No-op
        }

        @Override
        public void onTabReselected(Tab tab) {
            // No-op
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            populateFromPagerAdapter();
        }

        @Override
        public void onInvalidated() {
            populateFromPagerAdapter();
        }
    }

    public static abstract class BottomBarFragmentPagerAdapter extends FragmentPagerAdapter {

        protected abstract Drawable getPageIconDrawable(int position);

        public BottomBarFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }
    }

    /*
        END: 与 ViewPager 快速继承的方法
     */
}
