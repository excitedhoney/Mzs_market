
package com.mzs.market.fragment;

import com.mzs.market.R;
import com.mzs.market.adapter.RecommendVPAdapter;
import org.taptwo.android.widget.UnderlinePageIndicator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class RecommendFragment extends Fragment implements ViewPager.OnPageChangeListener,
        OnCheckedChangeListener {

    private ViewPager vPager;

    private UnderlinePageIndicator tabIndicator;
    
    private RecommendVPAdapter vPadapter;

    private RadioGroup rgIndicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recommend, container, false);
        vPager = (ViewPager) root.findViewById(R.id.pager);
        vPager.setOffscreenPageLimit(3);
        rgIndicator = (RadioGroup) root.findViewById(R.id.rg_indicator);
        tabIndicator = (UnderlinePageIndicator) root.findViewById(R.id.indicator);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vPadapter= new RecommendVPAdapter(getFragmentManager());
        vPager.setAdapter(vPadapter);
        tabIndicator.setViewPager(vPager);
        tabIndicator.setOnPageChangeListener(this);
        rgIndicator.setOnCheckedChangeListener(this);
    }
    
    @Override
    public void onPageScrollStateChanged(int position) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton) rgIndicator.getChildAt(position)).setChecked(true);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
            case R.id.rb_choice:
                vPager.setCurrentItem(0,true);
                break;
            case R.id.rb_musthave:
                vPager.setCurrentItem(1,true);
                break;
            case R.id.rb_online:
                vPager.setCurrentItem(2,true);
                break;
        }
    }

}
