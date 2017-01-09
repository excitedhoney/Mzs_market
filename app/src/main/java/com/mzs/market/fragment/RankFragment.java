
package com.mzs.market.fragment;

import com.mzs.market.R;
import org.taptwo.android.widget.UnderlinePageIndicator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class RankFragment extends Fragment implements ViewPager.OnPageChangeListener,
        OnCheckedChangeListener {

    private ViewPager vPager;

    private UnderlinePageIndicator tabIndicator;

    private RankPagerAdapter vPadapter;

    private RadioGroup rgIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rank, container, false);
        vPager = (ViewPager) root.findViewById(R.id.pager);
        rgIndicator = (RadioGroup) root.findViewById(R.id.rg_indicator);
        tabIndicator = (UnderlinePageIndicator) root.findViewById(R.id.indicator);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("wh","rankFragment onActivityCreated");
        vPadapter = new RankPagerAdapter(getChildFragmentManager());
        vPager.setAdapter(vPadapter);
        tabIndicator.setViewPager(vPager);
        tabIndicator.setOnPageChangeListener(this);
        rgIndicator.setOnCheckedChangeListener(this);
    }

    private static class RankPagerAdapter extends FragmentPagerAdapter {
        private static final String[] TITLES = new String[] {
                "应用排行", "游戏排行"
        };

        public RankPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new AppRankFragment();
            else if (position == 1) {
                return new AppRankFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_app_rank:
                vPager.setCurrentItem(0, true);
                break;
            case R.id.rb_game_rank:
                vPager.setCurrentItem(1, true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton) rgIndicator.getChildAt(position)).setChecked(true);
    }
}
