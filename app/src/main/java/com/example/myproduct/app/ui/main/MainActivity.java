package com.example.myproduct.app.ui.main;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.myproduct.app.R;
import com.example.myproduct.app.databinding.ActivityMainBinding;
import com.example.myproduct.app.ui.AppBaseActivity;
import com.example.myproduct.lib.common_ui.ui.widget.BottomBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppBaseActivity
        implements PortalListFragment.OnFragmentInteractionListener, DemoListFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;

    public static void startActivity(Context from) {
        Intent i = new Intent(from, MainActivity.class);
        from.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupViewPager(mBinding.viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            onFinishing();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onFinishing() {
    }

    protected void setupViewPager(final ViewPager viewPager) {
        PagerAdapter adapter = createPagerAdapter();
        viewPager.setAdapter(adapter);
        if (adapter.getCount() > 0) {
            viewPager.setOffscreenPageLimit(adapter.getCount());
        }

        setupBottomBar(mBinding.bottomBar, viewPager);
    }

    private void setupBottomBar(final BottomBar bottomBar, final ViewPager viewPager) {
        bottomBar.setTypeFace("fonts/Roboto-Regular.ttf");
        bottomBar.setupWithViewPager(viewPager);
        bottomBar.addOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(BottomBar.Tab tab) {
            }

            @Override
            public void onTabUnselected(BottomBar.Tab tab) {
            }

            @Override
            public void onTabReselected(BottomBar.Tab tab) {
            }
        });
    }

    private PagerAdapter createPagerAdapter() {
        final Context context = getApplicationContext();
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(context, getSupportFragmentManager());
        adapter
                .addPage(PortalListFragment.newInstance("A", "B"), "Message", ContextCompat.getDrawable(context, R.drawable.ic_message_black_48dp))
                .addPage(DemoListFragment.newInstance("C", "D"), "Demo List", ContextCompat.getDrawable(context, R.drawable.ic_format_list_bulleted_black_48dp));
        return adapter;
    }

    public static class SectionsPagerAdapter extends BottomBar.BottomBarFragmentPagerAdapter {
        private List<FragmentInfo> mFragmentInfos;

        private class FragmentInfo {
            private Fragment mFragment;
            private String mTitle;
            private Drawable mIcon;

            public FragmentInfo(Fragment fragment, String title, Drawable icon) {
                mFragment = fragment;
                mTitle = title;
                mIcon = icon;
            }
        }

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mFragmentInfos = new ArrayList<>(4);
        }

        public SectionsPagerAdapter addPage(Fragment fragment, String title, Drawable icon) {
            FragmentInfo info = new FragmentInfo(fragment, title, icon);
            mFragmentInfos.add(info);
            return this;
        }

        public int indexOf(Fragment fragment) {
            int result = -1;
            Iterator<FragmentInfo> iterator = mFragmentInfos.iterator();
            while (iterator.hasNext()) {
                result++;
                if (iterator.next().mFragment.equals(fragment)) {
                    break;
                }
            }
            return result;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentInfos.get(position).mFragment;
        }

        @Override
        public int getCount() {
            return mFragmentInfos.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentInfos.get(position).mTitle;
        }

        @Override
        protected Drawable getPageIconDrawable(int position) {
            return mFragmentInfos.get(position).mIcon;
        }
    }
}
