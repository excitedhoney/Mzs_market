package com.mzs.market.adapter;

import com.mzs.market.R;
import com.mzs.market.bean.APKCategory;
import com.mzs.market.utils.ImageloaderHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryGVAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<APKCategory> categories;
    public CategoryGVAdapter(Context context,ArrayList<APKCategory> categories){
        this.inflater=LayoutInflater.from(context);
        this.categories=categories;
    }
    
    @Override
    public int getCount() {
        return categories.size();
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
        convertView=inflater.inflate(R.layout.gv_item_category,parent,false);
        APKCategory category=categories.get(position);
        ImageView category_icon=(ImageView) convertView.findViewById(R.id.category_icon);
        TextView category_name=(TextView) convertView.findViewById(R.id.category_name);
        ImageLoader.getInstance().displayImage(category.category_icon_url,category_icon,ImageloaderHelper.getOptions());
        category_name.setText(category.category_name);
        return convertView;
    }

}
