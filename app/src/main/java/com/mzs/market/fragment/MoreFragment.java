package com.mzs.market.fragment;

import com.mzs.market.R;
import com.mzs.market.activity.AppUninstallActivity;
import com.mzs.market.activity.FeedbackActivity;
import com.mzs.market.activity.ManagerApkActivity;
import com.mzs.market.utils.UIUtils;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MoreFragment extends Fragment implements View.OnClickListener{
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_more,container,false);
        root.findViewById(R.id.rl_app_update).setOnClickListener(this);
        root.findViewById(R.id.rl_app_manager).setOnClickListener(this);
        root.findViewById(R.id.rl_app_uninstall).setOnClickListener(this);
        root.findViewById(R.id.rl_app_move).setOnClickListener(this);
        root.findViewById(R.id.rl_connect_pc).setOnClickListener(this);
        root.findViewById(R.id.rl_user_center).setOnClickListener(this);
        root.findViewById(R.id.rl_setting).setOnClickListener(this);
        root.findViewById(R.id.rl_feedback).setOnClickListener(this);
        return root;
    }

    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_app_update:
               
                break;
            case R.id.rl_app_manager:
                UIUtils.openActivity(getActivity(),ManagerApkActivity.class);
                break;
            case R.id.rl_app_uninstall:
                UIUtils.openActivity(getActivity(),AppUninstallActivity.class);
                break;
            case R.id.rl_app_move:
                
                break;
            case R.id.rl_connect_pc:
                
                break;
            case R.id.rl_user_center:
                
                break;
            case R.id.rl_setting:
                
                break;
            case R.id.rl_feedback:
                UIUtils.openActivity(getActivity(), FeedbackActivity.class);
                break;
        }
    }
}
