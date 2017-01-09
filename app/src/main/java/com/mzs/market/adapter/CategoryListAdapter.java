package com.mzs.market.adapter;

import com.mzs.market.R;
import com.mzs.market.bean.APKCategory;
import com.mzs.market.widget.MSGridView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

public class CategoryListAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<APKCategory> categories;
    
    public CategoryListAdapter(Context context,ArrayList<APKCategory> categories){
        this.context=context;
        this.inflater=LayoutInflater.from(context);
        this.categories=categories;
    }
    
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.item_category_list,parent,false);
        MSGridView gv=(MSGridView) convertView.findViewById(R.id.gv_category);
        gv.setAdapter(new CategoryGVAdapter(context,categories));
        return convertView;
    }

}
