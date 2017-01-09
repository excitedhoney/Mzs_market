
package com.mzs.market.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class TriangleCornerView extends View {

    private int colorVaule = Color.TRANSPARENT;
    private String text = "";
    private Paint paint;
    private Paint textPaint;
    private Path path = new Path();

    public TriangleCornerView(Context context) {
        super(context);
        initWithContext(context);
    }

    public TriangleCornerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(colorVaule);
        setBackgroundColor(0);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        int fontSize = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, context.getResources()
                        .getDisplayMetrics());
        textPaint.setTextSize(fontSize);
    }

    public void setCornerColorAndText(int color, String text) {
        this.colorVaule = color;
        this.text = text;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        path.reset();
        path.moveTo(0, 0);
        path.lineTo(w, 0);
        path.lineTo(0, h);
        path.lineTo(0, 0);
        path.close();
        canvas.clipPath(path);
        canvas.drawColor(colorVaule);
        float xx = (float) w / 4;
        canvas.drawText(text, xx, xx + textPaint.getTextSize() / 2, textPaint);
    }
}
