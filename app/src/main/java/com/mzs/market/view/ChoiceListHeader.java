
package com.mzs.market.view;

import com.mzs.market.R;
import com.mzs.market.bean.AdsImage;
import com.mzs.market.utils.ImageloaderHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ChoiceListHeader extends LinearLayout {

    private ViewFlow bannerFlow;

    private ArrayList<AdsImage> imgs;

    private BannerAdapter adapter;

    private CircleFlowIndicator bannerIndicator;

    private DisplayImageOptions options;

    public ChoiceListHeader(Context context) {
        super(context);
        initView();
    }

    public ChoiceListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        View.inflate(getContext(), R.layout.choice_list_header, this);
        bannerFlow = (ViewFlow) findViewById(R.id.choice_banner_pager);
        bannerIndicator = (CircleFlowIndicator) findViewById(R.id.choice_banner_indicator);
        adapter = new BannerAdapter();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.banner_bg)
                .showImageForEmptyUri(R.drawable.banner_bg)
                .showImageOnFail(R.drawable.banner_bg)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public void setImageData(ArrayList<AdsImage> imgs) {
        this.imgs = imgs;
        bannerFlow.setmSideBuffer(5);
        bannerFlow.setFlowIndicator(bannerIndicator);
        bannerFlow.setAdapter(adapter);
        bannerFlow.startAutoFlowTimer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bannerFlow.stopAutoFlowTimer();
    }

    class BannerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imgs != null ? Integer.MAX_VALUE : 0;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView im = new ImageView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            im.setLayoutParams(params);
            im.setScaleType(ScaleType.FIT_XY);
            ImageLoader.getInstance().displayImage(imgs.get(position % 5).imgUrl, im,options);
            return im;
        }
    }

}
