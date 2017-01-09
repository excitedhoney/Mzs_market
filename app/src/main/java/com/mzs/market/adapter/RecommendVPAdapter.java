package com.mzs.market.adapter;

import com.mzs.market.fragment.ChoiceFragment;
import com.mzs.market.fragment.MustHaveFragment;
import com.mzs.market.fragment.OnlineFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class RecommendVPAdapter extends FragmentPagerAdapter {

    private static final String[] TITLES = new String[] { "精选", "必备", "网游"};
    
    public RecommendVPAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
            return new ChoiceFragment();
        else if (position==1) {
            return new MustHaveFragment();
        }else if (position==2) {
            return new OnlineFragment();
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
