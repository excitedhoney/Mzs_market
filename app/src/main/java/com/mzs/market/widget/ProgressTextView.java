
package com.mzs.market.widget;

import com.mzs.market.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class ProgressTextView extends TextView {

    private int progress;

    private Rect rect = new Rect();

    private Paint paint;

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.text_color_green));
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        rect.left = 0;
        rect.top = 0;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        double scale=(double)progress / 100 ;
        rect.right = (int) (scale* getWidth());
        rect.bottom = getHeight();
        canvas.drawRect(rect, paint);
        super.onDraw(canvas);
    }
}
