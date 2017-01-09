
package com.mzs.market.fragment;

import com.google.gson.Gson;
import com.mzs.market.R;
import com.mzs.market.adapter.CategoryListAdapter;
import com.mzs.market.bean.APKCategoryList;
import com.mzs.market.utils.AssetUtils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CategoryFragment extends Fragment {

    private ListView category_list;
    private CategoryListAdapter adapter = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        category_list = (ListView) root.findViewById(R.id.category_list);
        Gson gson = new Gson();
        APKCategoryList list = null;
        String str = AssetUtils.getString(getActivity(), "category.json");
        list = gson.fromJson(str, APKCategoryList.class);
        adapter = new CategoryListAdapter(getActivity(), list.game_category);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        category_list.setAdapter(adapter);

    }
}
