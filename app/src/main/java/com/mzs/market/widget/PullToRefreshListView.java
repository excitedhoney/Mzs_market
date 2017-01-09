
package com.mzs.market.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.mzs.market.widget.PullToRefreshLayout.Pullable;

public class PullToRefreshListView extends ListView implements Pullable {

    public PullToRefreshListView(Context context)
    {
        super(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown()
    {
        if (getCount() == 0)
        {
            return true;
        } else if (getFirstVisiblePosition() == 0
                && getChildAt(0).getTop() >= 0)
        {
            return true;
        } else
            return false;
    }
}
