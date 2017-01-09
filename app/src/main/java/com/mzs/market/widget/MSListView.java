package com.mzs.market.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MSListView extends ListView {

	public MSListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MSListView(Context context) {
		super(context);
	}

	public MSListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
