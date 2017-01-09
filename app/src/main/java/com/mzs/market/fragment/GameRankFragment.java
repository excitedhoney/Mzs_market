package com.mzs.market.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mzs.market.R;

public class GameRankFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_game_rank,container, false);
        return root;
    }
}
