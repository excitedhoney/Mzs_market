package com.mzs.market.adapter;

import java.util.List;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

public class MainTabAdapter implements RadioGroup.OnCheckedChangeListener{
    private List<Fragment> fragments;
    private RadioGroup rgs; // 用于切换tab
    private FragmentActivity fragmentActivity;
    private int fragmentContentId;
    private int currentTab; 
    private OnExtraCheckedListener extraCheckedListener; 
    private Handler handler;
    
    public MainTabAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments, int fragmentContentId, RadioGroup rgs) {
        this.fragments = fragments;
        this.rgs = rgs;
        this.fragmentActivity = fragmentActivity;
        this.fragmentContentId = fragmentContentId;
        this.handler=new Handler();
    }
    
    public void setup(){
        // 默认显示第一页
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(0));
        ft.commit();
        rgs.setOnCheckedChangeListener(this);
    }
    

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        final int size=rgs.getChildCount();
        for(int i = 0; i < size; i++){
            if(rgs.getChildAt(i).getId() == checkedId){
                final Fragment fragment = fragments.get(i);
                FragmentTransaction ft = obtainFragmentTransaction(i);
                getCurrentFragment().onPause(); // 暂停当前tab
                if(fragment.isAdded()){  //已经添加
                    showTab(i);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            fragment.onResume();
                        }
                    });
                }else{
                    ft.add(fragmentContentId, fragment);
                    showTab(i);
                }
                ft.commit();
                // 如果设置了切换tab额外功能功能接口
                if(null != extraCheckedListener){
                    extraCheckedListener.onExtraChecked(radioGroup, checkedId,i);
                }
            }
        }
    }

    /**
     * 切换tab
     * @param idx
     */
    private void showTab(int idx){
        for(int i = 0; i < fragments.size(); i++){
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);
            if(idx == i){
                ft.show(fragment);
            }else{
                ft.hide(fragment);
            }
            ft.commit();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }

    /**
     * 获取一个带动画的FragmentTransaction
     * @param index
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction(int index){
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        return ft;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public Fragment getCurrentFragment(){
        return fragments.get(currentTab);
    }
    
    public void setOnExtraListener(OnExtraCheckedListener listener){
        this.extraCheckedListener=listener;
    }
    
    public OnExtraCheckedListener getOnExtraCheckedListener(){
        return extraCheckedListener;
    }
    
    /**
     *  切换tab额外功能功能接口
     */
    public interface OnExtraCheckedListener{
        void onExtraChecked(RadioGroup rg, int id, int position);
    }
}
